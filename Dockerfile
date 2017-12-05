FROM openjdk:alpine
ADD build/libs/scorekeep-api-1.0.0.jar scorekeep-api-1.0.0.jar
ENV AWS_REGION=""
ENV NOTIFICATION_TOPIC=""
ENV JAVA_OPTS=""
EXPOSE 5000
ENTRYPOINT [ "sh", "-c", "java -Dserver.port=5000 -jar scorekeep-api-1.0.0.jar" ]
