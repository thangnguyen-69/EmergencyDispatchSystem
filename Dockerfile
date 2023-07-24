FROM openjdk:17-slim-buster

EXPOSE 8082
VOLUME /tmp /var/log
COPY ./build/libs/dispatcher.jar /app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=${ENV}","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
