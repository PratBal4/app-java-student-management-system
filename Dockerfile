# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:22-jdk-jammy

# Install necessary libraries for X11 rendering (required for Java Swing)
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libfreetype6 \
    fontconfig \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Compile the Java application
RUN javac -cp "lib/sqlite-jdbc.jar:lib/slf4j-api.jar:lib/slf4j-simple.jar" src/*.java

# Run the application (ENTRYPOINT allows passing arguments like 'CLI')
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-cp", "lib/sqlite-jdbc.jar:lib/slf4j-api.jar:lib/slf4j-simple.jar:src", "Main"]
