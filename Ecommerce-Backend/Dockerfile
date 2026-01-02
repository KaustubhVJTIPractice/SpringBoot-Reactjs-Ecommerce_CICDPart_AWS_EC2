# =========================
# Build Stage
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy full source directly
COPY . .

# Build Spring Boot fat JAR
RUN mvn clean package -DskipTests


# =========================
# Runtime Stage (Distroless)
# =========================
FROM gcr.io/distroless/java21-debian12

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

USER nonroot
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
