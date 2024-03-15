FROM maven:3.9.6-eclipse-temurin-21 as mvn
ARG MAVEN_LOCAL_REP
COPY . /app
WORKDIR /app
RUN    ["mvn", "clean" ,"package", "-Punit-test-only"]
WORKDIR /app/target
RUN ["mv", "EducationSchedule-1.0-SNAPSHOT.war", "EducationSchedule.war"]

FROM tomcat:jre21-temurin-jammy as tomcat
WORKDIR $CATALINA_HOME
RUN ["mv","webapps","webapps2"]
RUN ["mv","webapps.dist", "webapps"]

COPY --from=mvn /app/target/EducationSchedule.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]
RUN /bin/bash