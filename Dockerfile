# =========================================================================
# Multi-Stage Dockerfile for VECTOR Bank
# Stage 1: Build fat executable jar using Maven & Eclipse Temurin JDK 17
# Stage 2: Minimal runtime image using Eclipse Temurin JRE 17
# =========================================================================

# --- Build Stage ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copy pom.xml first to take advantage of Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Package shaded executable jar
RUN mvn clean package -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:17-jre-alpine AS runner

WORKDIR /app

# Non-root user setup for production container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy shaded jar from builder stage
COPY --from=builder /build/target/vector-bank.jar /app/vector-bank.jar

# Run interactive console banking app
ENTRYPOINT ["java", "-jar", "/app/vector-bank.jar"]
