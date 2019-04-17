FROM openjdk:8
MAINTAINER syhc006
LABEL app="gateway" version="1.0" by="syhc006"
COPY ./target/gateway-web-1.0.jar gateway-web-1.0.jar
CMD java -jar gateway-web-1.0.jar
