#!/bin/bash

# Check if java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed."
    read -p "Would you like to install it now? (y/n): " confirm
    if [[ "$confirm" == [yY] || "$confirm" == [yY][eE][sS] ]]; then
        if [[ "$OSTYPE" == "darwin"* ]]; then
            echo "Installing OpenJDK using Homebrew..."
            brew install openjdk
        elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
            echo "Installing default-jdk using apt..."
            sudo apt update && sudo apt install -y default-jdk
        else
            echo "Unsupported OS for auto-install. Please install Java manually."
            exit 1
        fi
    else
        echo "Please install Java manually to run the project."
        exit 1
    fi
fi

echo "Running the application..."
java --enable-native-access=ALL-UNNAMED -cp "lib/sqlite-jdbc.jar:lib/slf4j-api.jar:lib/slf4j-simple.jar:src" AppGUI
