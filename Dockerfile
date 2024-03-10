FROM gradle:8.4.0-jdk21-alpine AS build
LABEL authors="Senko-san"
LABEL authors="AgniaEndie"
LABEL authors="GekkStr"
LABEL autors="xxlegendzxx22"
WORKDIR /earthbridge
COPY . /earthbridge
ENTRYPOINT ["java","-jar", "/earthbridge/build/libs/earthbridge-1.0-SNAPSHOT.jar"]
