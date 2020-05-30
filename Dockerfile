FROM openjdk:8-alpine

COPY target/uberjar/laconic-cms.jar /laconic-cms/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/laconic-cms/app.jar"]
