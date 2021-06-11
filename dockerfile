FROM adoptopenjdk/openjdk8

<<<<<<< HEAD

=======
RUN	mkdir -p /apps/portal

COPY ./football-project.jar /apps/portal

CMD java -jar /apps/portal/football-project.jar
>>>>>>> branch 'main' of https://github.com/piyushvij/football-project.git

