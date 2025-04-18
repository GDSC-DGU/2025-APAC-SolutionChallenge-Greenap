FROM openjdk:21-jdk-slim AS builder
WORKDIR /buildMyApp

COPY ../server-legacy/gradlew settings.gradle.kts ./
COPY ../server-legacy/gradle gradle
RUN chmod +x gradlew

COPY ../server-legacy/build.gradle.kts .

RUN ./gradlew dependencies

COPY ../server-legacy/src ./src

RUN ./gradlew clean build

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /buildMyApp/build/libs/*.jar sc.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=prod", "-jar", "sc.jar"]