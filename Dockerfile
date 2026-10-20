# --- Phase 1: Compile and Package the Project ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy only the dependency pom descriptor file first
COPY pom.xml .

# Copy all codebase package blocks into standard Maven structure
COPY robotroutefms ./src/main/java/com/example/robotroutefms
COPY resources ./src/main/resources

# Run clean production package bundling
RUN mvn clean package -DskipTests

# --- Phase 2: Create the Lightweight Runtime Footprint ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Pull the compiled jar from the build pipeline container
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
