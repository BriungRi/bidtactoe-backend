FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/bidtactoe-backend-latest.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
EXPOSE 3001