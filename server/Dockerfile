FROM openjdk:21-jdk-slim AS builder
WORKDIR /buildMyApp

COPY gradlew settings.gradle.kts ./
COPY gradle gradle
RUN chmod +x gradlew

COPY build.gradle.kts .

RUN ./gradlew dependencies

COPY src ./src

RUN ./gradlew clean build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /buildMyApp/build/libs/server-0.0.1-SNAPSHOT.jar greenap.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=prod", "-jar", "greenap.jar"]