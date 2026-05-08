# ─── Build stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Copy maven wrapper and pom.xml first (layer-cache friendly)
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Download all dependencies offline (cached unless pom changes)
RUN ./mvnw dependency:go-offline -B

# Copy application source and build
COPY src src
RUN ./mvnw package -DskipTests

# ─── Run stage ───────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create a non-root user and setup uploads directory
RUN addgroup --system spring && adduser --system spring --ingroup spring
RUN mkdir -p /app/uploads && chown -R spring:spring /app/uploads
USER spring:spring

COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
