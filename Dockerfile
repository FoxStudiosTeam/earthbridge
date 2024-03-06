FROM gradle:8.4.0-jdk21-alpine AS build
LABEL authors="Senko-san"
LABEL authors="AgniaEndie"
LABEL authors="GekkStr"
LABEL autors="xxlegendzxx22"
WORKDIR /earthbridge
COPY . /earthbridge
RUN gradle jar
ENTRYPOINT ["java","-XX:+UseZGC","-jar", "/earthbridge/build/libs/earthbridge-1.0-SNAPSHOT.jar"]
