FROM adoptopenjdk/openjdk8

RUN	mkdir -p /apps/portal

COPY ./football-project.jar /apps/portal

CMD java -jar /apps/portal/football-project.jar

