# Student Management System

A simple desktop-based **Student Management System** built using Java Swing for the graphical user interface (GUI) and SQLite for the database. 

## Project Structure

```text
📁 StudentManagementSystem/
├── lib/
│   ├── slf4j-api.jar        (SLF4J Logging API)
│   ├── slf4j-simple.jar     (SLF4J Simple Binding)
│   └── sqlite-jdbc.jar      (SQLite JDBC Driver)
├── src/
│   ├── AppGUI.java          (Main GUI application)
│   ├── Database.java        (Database connection & initialization)
│   ├── Student.java         (Student model)
│   └── StudentDAO.java      (Data Access Object for Student CRUD operations)
├── build.sh                 (Compilation script for macOS/Linux)
├── run.sh                   (Execution script for macOS/Linux)
├── build.bat                (Compilation script for Windows)
└── run.bat                  (Execution script for Windows)
```

## Prerequisites

- **Java Development Kit (JDK)**: You need Java installed on your machine to compile and run this application. (Tested with OpenJDK).

## How to Compile & Run (Using Scripts)

For your convenience, build and run scripts have been provided for different operating systems.

### On macOS / Linux
1. Open your terminal in the project directory.
2. Ensure the scripts are executable:
   ```bash
   chmod +x build.sh run.sh
   ```
3. Compile the project:
   ```bash
   ./build.sh
   ```
4. Run the application:
   ```bash
   ./run.sh
   ```

### On Windows
1. Open Command Prompt or PowerShell in the project directory.
2. Compile the project:
   ```cmd
   build.bat
   ```
3. Run the application:
   ```cmd
   run.bat
   ```

*(Note: The `run` scripts automatically include the `--enable-native-access=ALL-UNNAMED` flag to suppress warnings related to the SQLite native JDBC driver loading on Java 22+).*

## Git & Collaboration
This project has been initialized as a Git repository. To collaborate and push this to a remote private repository, you can run the following commands:
```bash
git remote add origin <YOUR_PRIVATE_REPO_URL>
git branch -M main
git push -u origin main
```

## Features
- **Add Student:** Insert a student's Name, Roll No, and Department into the SQLite database.
- **View Students:** Automatically lists all students fetched from the database in a table.
- **Delete Student:** Select a student from the table and delete them directly from the database.
