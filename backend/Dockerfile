FROM openjdk:8-jre-alpine
VOLUME /tmp
ARG JAR_FILE=webtoon-api/build/libs/webtoon-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod","-jar", "/app.jar"]