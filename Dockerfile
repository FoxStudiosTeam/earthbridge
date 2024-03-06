FROM gradle:8.4.0-jdk21-alpine AS build
LABEL authors="Senko-san"
LABEL authors="AgniaEndie"
LABEL authors="GekkStr"

WORKDIR /earthbridge
COPY . /earthbridge
RUN gradle bootJar
ENTRYPOINT ["java","-XX:+UseZGC","-jar", "/earthbridge/build/libs/earthbridge-0.0.1-SNAPSHOT.jar"]
