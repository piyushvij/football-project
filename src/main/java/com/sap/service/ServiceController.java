package com.sap.service;

import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sap.config.ConfigIface;

@RestController
public class ServiceController {

	// Develop, Test and Deploy a microservice to find standings of a team playing
	// league football match using country name, league name and team name.
	//http://localhost:8080/standingresult?teamname=Real%20Madrid&leaguename=La%20Liga&countryname=Spain
	String restEndPointURL = "https://apiv3.apifootball.com";


	@GetMapping("/")
	public String getRootMessage(){
		return "You are root context";
	}
	
	@GetMapping("/standingresult")
	public String getStandingbyTeamLeagueCountryName(@RequestParam String teamname, 
			@RequestParam String leaguename,
			@RequestParam String countryname) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

		Gson gson = new Gson();

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(restEndPointURL)
				.queryParam("action", "get_standings")
				.queryParam("league_id", "302")
				.queryParam("APIkey", ConfigIface.APIKEY);

		RestTemplate restTemplate = restTemplate();

		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), 
				HttpMethod.GET, 
				entity,
				String.class);
		
		Type collectionType = new TypeToken<List<StandingModel>>(){}.getType();
		List<StandingModel> standingModelList = gson.fromJson(responseEntity.getBody().toString(), collectionType);
		
		StringBuffer sb =  new StringBuffer();
		for(StandingModel obj: standingModelList) {
			if(obj.getCountryName().equalsIgnoreCase(countryname)
					&& obj.getLeagueName().equalsIgnoreCase(leaguename)
					&& obj.getTeamName().equalsIgnoreCase(teamname)) {
				sb.append("Country ID & Name:" + obj.getCountryName()).append("<br>");
				sb.append("League ID & Name:" +obj.getLeagueId() +  "-" + obj.getLeagueName()).append("<br>");
				sb.append("Team ID & Name:" +obj.getTeamId() +  "-" + obj.getTeamName()).append("<br>");
				sb.append("Overall League Position:" +obj.getOverallLeaguePosition()).append("<br>");
			}
		}
		if(sb.length()<=0) {
			sb.append("No result found for the Team Name:"+teamname + " leaguename:"+leaguename + " countryname:"+ countryname);
		}
		
		return sb.toString();
	}

	public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
}
