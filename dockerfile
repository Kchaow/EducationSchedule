FROM tomcat:jre21-temurin-jammy
#COPY target/*.war $CATALINA_BASE/webapps/app.war
WORKDIR $CATALINA_HOME
RUN ["mv","webapps","webapps2"]
RUN ["mv","webapps.dist", "webapps"]
COPY ./target/*.war /usr/local/tomcat/webapps
CMD ["catalina.sh", "run"]