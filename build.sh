#!/bin/bash
echo "Compiling the project..."
javac -cp "lib/sqlite-jdbc.jar:lib/slf4j-api.jar:lib/slf4j-simple.jar" src/*.java
echo "Compilation completed successfully."
