FROM openjdk:11
WORKDIR /backend

ADD ./build/libs/pis-backend-0.1-all.jar application.jar

EXPOSE 8080

CMD java -jar application.jar