
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine as builder
LABEL authors="dfurchert"

WORKDIR /workspace
COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/target/user-management-service-0.0.1-SNAPSHOT.jar user-management.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "/app/user-management.jar"]
