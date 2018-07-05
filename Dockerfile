FROM openjdk:8u141-jdk-slim
MAINTAINER jeremydeane.net
EXPOSE 9002
RUN mkdir /app/
COPY target/amqp-cep-1.0.1.jar /app/
ENTRYPOINT exec java $JAVA_OPTS -Damqp.hostname='amqp-broker' -jar /app/amqp-cep-1.0.1.jar