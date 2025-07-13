FROM maven:3.9.8-eclipse-temurin-21 as builder

WORKDIR /workspace
COPY . .

# Build the protocol module
WORKDIR /workspace/gRPCProtocol
RUN mvn clean install

# Build the server
WORKDIR /workspace/Server
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/Server/target/*.jar app.jar
EXPOSE 8081 9090
ENTRYPOINT ["java", "-jar", "app.jar"]