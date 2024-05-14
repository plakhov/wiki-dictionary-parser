FROM openjdk:11
COPY build/libs/JARNAME $APP_HOME/
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=20.0", "-jar", "JARNAME"]
