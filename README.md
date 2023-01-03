# Backend project for PIS (WUT)

This project was bootstrapped with [Micronaut Launch](https://micronaut.io/launch/)

## Scripts

### `./gradlew build`

Builds development version of application

### `./gradlew run`

Builds and runs development version of application on `http://localhost:8000`

You need to run this script again if there are any changes to the code.

### `./gradlew test`

Launches the test runner and generates test raport with coverage information.

### `./gradlew nativeBuild`

Creates executable of application optimized for production use.

## Deploy to production server

Our production server is prepared for application deployment with the use of Jenkins job. You need to login into server (mion network -> virtual machine) and create port passthrough (or use ngrok) to access Jenkins web interface. 

You also need to create ngrok url for frontend running on server, and change (or add) this url to cors allowed origins in `src/main/resources/application.yml`. Those changes need to be applied to "prezentacja" branch, and with ngrok still running you can start the Jenkins job.

After Jenklins job finishes, create ngrok url for backend connection, change url in frontend project to this url, build frontend project and you should be able to test application.

## Micronaut 3.7.3 Documentation

- [User Guide](https://docs.micronaut.io/3.7.3/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.7.3/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.7.3/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)


