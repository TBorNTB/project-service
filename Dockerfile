FROM eclipse-temurin:21-jdk-alpine
COPY ./build/libs/*SNAPSHOT.jar projectDto.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "projectDto.jar"]
