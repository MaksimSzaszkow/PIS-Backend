FROM gradle
WORKDIR /backend

ADD . /backend

RUN ./gradlew build

EXPOSE 8080

ENTRYPOINT ["./gradlew", "run"]