# Student Management System

A simple desktop-based **Student Management System** built using Java Swing for the graphical user interface (GUI) and SQLite for the database. 

## 📸 Screenshots

![Main Screen](screenshots/main_screen.png)
![Edit Panel](screenshots/edit_panel.png)


## 🏗 Project Architecture & Internal Workings

The project is structured into three main layers, following a simplified MVC (Model-View-Controller) / DAO (Data Access Object) pattern:

1. **`AppGUI.java` (View & Controller):** 
   - Handles the Java Swing interface (buttons, text fields, table, and the Side Panel).
   - Listens to user interactions and routes them to the DAO layer.
   - Refreshes the UI (like updating the table) whenever data changes.
2. **`StudentDAO.java` (Data Access Object):**
   - The intermediary between the UI and the database.
   - Contains static methods (`addStudent`, `getAllStudents`, `updateStudent`, `deleteStudent`) that safely execute raw SQL queries (`INSERT`, `SELECT`, `UPDATE`, `DELETE`) using `PreparedStatement`s to prevent SQL injection.
3. **`Database.java` (Configuration):**
   - Manages the SQLite database connection using the SQLite-JDBC driver.
   - Creates the `students.db` file automatically on first run and ensures the `students` table schema exists.
4. **`Student.java` (Model):**
   - A standard Java Object (POJO) representing the structure of a single student record (id, name, rollNo, department).

### Database ID Behavior (Important Note)
If you delete a row, you may notice that the `ID` counter does not reset or shift backward for remaining rows. This is an intended feature of relational databases utilizing `AUTOINCREMENT` primary keys. IDs are meant to be immutable, unique identifiers for the entire lifecycle of a database. Re-using old IDs can accidentally corrupt data relations in larger, complex databases.

## 📁 Project Structure

```text
📁 StudentManagementSystem/
├── database/                (Local Database Storage - Ignored by Git)
│   └── students.db          (Generated automatically when app runs)
├── lib/
│   ├── slf4j-api.jar        (SLF4J Logging API)
│   ├── slf4j-simple.jar     (SLF4J Simple Binding)
│   └── sqlite-jdbc.jar      (SQLite JDBC Driver)
├── src/
│   ├── AppGUI.java          (Main GUI application)
│   ├── Database.java        (Database connection & initialization)
│   ├── Student.java         (Student model)
│   └── StudentDAO.java      (Data Access Object)
├── build.sh                 (Compilation script for macOS/Linux)
├── run.sh                   (Execution script for macOS/Linux)
├── build.bat                (Compilation script for Windows)
├── run.bat                  (Execution script for Windows)
└── Dockerfile               (Docker container specification)
```

### Database Behavior & Architecture Note
- **Isolated Database:** The database file (`database/students.db`) is completely isolated and ignored by Git. When a new team member clones this repository and runs the application, a fresh database folder and file will be automatically created on their local machine.
- **SQLite ID Counter:** If you delete a row, you may notice that the `ID` counter does not reset or shift backward for remaining rows. This is an intended feature of relational databases utilizing `AUTOINCREMENT` primary keys. IDs are meant to be immutable, unique identifiers for the entire lifecycle of a database. Re-using old IDs can accidentally corrupt data relations in larger, complex databases.

## 🛠 Prerequisites

- **Java Development Kit (JDK)**: You need Java installed on your machine to compile and run this application. (Tested with OpenJDK).

## 🚀 How to Compile & Run (Using Scripts)

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
   .\build.bat
   ```
3. Run the application:
   ```cmd
   .\run.bat
   ```

*(Note: The build scripts will now intelligently check if Java is installed and offer to automatically install it via `brew`, `apt`, or `winget` if it is missing!)*

## ✨ Features
- **Add Student:** Insert a student's Name, Roll No, and Department into the SQLite database.
- **Edit Student (Side Panel):** Select a student from the table, click "Edit Selected", and a side panel will dynamically open allowing you to update and save any parameter.
- **View Students:** Automatically lists all students fetched from the database in a table.
- **Delete Student:** Select a student from the table and delete them directly from the database.

## 🐳 Docker Support

While this project is a Desktop GUI application using Java Swing, it is fully supported via Docker by utilizing X11 forwarding.

### 1. Build the Docker Image
```bash
docker build -t student-management-sys .
```

### 2. Run the Docker Container (Linux / macOS with XQuartz)
To allow the Docker container to render the Java Swing GUI on your host machine, you must share your X11 socket and `DISPLAY` environment variable:
```bash
# Allow local connections to X11 (run on host machine)
xhost +local:

# Run the container with X11 volume mapping
docker run -it --rm \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  student-management-sys
```
*(Note for Windows Users: Running Docker X11 GUI applications requires an X Server installed on Windows like VcXsrv or Xming, and configuring the DISPLAY variable to your host IP).*

## 🤝 Git & Collaboration
This project has been initialized as a Git repository. To collaborate and push this to a remote private repository, you can run the following commands:
```bash
git remote add origin <YOUR_PRIVATE_REPO_URL>
git branch -M main
git push -u origin main
```
