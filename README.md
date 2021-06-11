# football-project
http://localhost:8080/standingresult?teamname=Real%20Madrid&leaguename=La%20Liga&countryname=Spain

<Build the project and create JAR for it.
<br>mvn clean install
<br>Move /targer/football-project-2.0.0.RELEASE.jar to /football-project.jar

Docker Commands to Build Image and run container:-
<br>docker build -t sapimage/football .
<br>docker stop sapservice
<br>docker rm sapservice
<br>docker run -d -p 8080:8080 --name sapservice sapimage/football