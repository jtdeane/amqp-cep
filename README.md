amqp-cep
=======================
Demo complex event processing - via RabbitMQ (AMQP)

Built with Java 8+, Spring-Boot (2.0.3.RELEASE)

##Spring

* Build

`mvn clean install`

* Start RabbitMQ

* Create VHost cogito

* Create User evaluator/evaluator w/ admin role

* Set client VHosts permissions (Read,Write,Configure): / and cogito

* Run

`mvn spring-boot:run -Drun.arguments="-Xmx256m,-Xms128m"`

Check health

`http://localhost:9002/actuator/info`

##Docker

* Create Network

`docker network create amqp-network`

* Start RabbitMQ

`docker run -d -p 15672:15672 --net=amqp-network --name amqp-broker --hostname amqp-broker rabbitmq:3.6.12-management`

* Create VHost cogito

* Create User evaluator/evaluator w/ admin role

* Set client VHosts permissions (Read,Write,Configure): / and cogito

* Pull down Image

`docker pull jtdeane/amqp-cep`

OR

* Build locally

`docker build -t amqp-producer:cep .`

* Run Docker

`docker run -d -p 9002:9002 -e JAVA_OPTS='-Xmx256m -Xms128m' --net=amqp-network --hostname amqp-cep amqp-cep:latest`