FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
RUN mkdir -p /root/.m2/repository
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline -B -Dcheckstyle.skip=true
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository mvn clean package -Dmaven.test.skip=true -Dcheckstyle.skip=true

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup && \
    mkdir -p /app/uploads && chown -R appuser:appgroup /app/uploads

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

USER appuser

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
