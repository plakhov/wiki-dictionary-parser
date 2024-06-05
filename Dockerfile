FROM openjdk:21
COPY ./build/libs/wiki-dictionary-parser.jar /wiki-dictionary-parser.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=20.0", "-jar", "wiki-dictionary-parser.jar"]
