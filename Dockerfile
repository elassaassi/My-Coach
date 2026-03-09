# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /app

# Cache dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Build application
COPY src/ ./src/
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:24-jre-alpine AS runtime

# Create non-root user
RUN addgroup -S momentum && adduser -S momentum -G momentum

WORKDIR /app

# Copy jar
COPY --from=build /app/target/momentum-*.jar app.jar

# Set ownership
RUN chown momentum:momentum app.jar

USER momentum

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
