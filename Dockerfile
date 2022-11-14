FROM ghcr.io/graalvm/native-image
WORKDIR /backend

ADD ./build/native/nativeCompile/pis-backend .

EXPOSE 8080

ENTRYPOINT ["/backend/pis-backend"]
