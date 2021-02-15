FROM openjdk:8-alpine

COPY target/uberjar/elogger.jar /elogger/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/elogger/app.jar"]
