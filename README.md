# Dynamic Database Manager (SQLite)

A powerful, dynamically-rendered desktop application and CLI tool built using Java (Swing) for managing ANY SQLite database.

## 🚀 Future Additions & Roadmap

This project is continuously evolving. The following features are planned for future distribution:
- **Pre-defined Templates:** One-click table generation for common systems (Schools, Inventory, Businesses).
- **Template Maker:** An advanced UI to build, save, and distribute your own database schema templates.
- **Enhanced UI/UX:** Better GUI frameworks and styling for a more modern desktop experience.
- **MCP Server & Automation:** Integration with a Model Context Protocol (MCP) server for AI-driven database automation and autonomous data entry.

## 🏗 Project Architecture & Internal Workings

This project utilizes a completely dynamic DAO (Data Access Object) architecture, capable of reading and manipulating any SQLite database schema on the fly!

1. **`Main.java` (Entrypoint):**
   - Routes execution to either the graphical UI (`DynamicDBManagerGUI`) or the command-line interface (`DynamicDBManagerCLI`) based on startup arguments.
2. **`DynamicDBManagerGUI.java` (Graphical UI):** 
   - Uses `JOptionPane` to prompt database selection at startup.
   - Dynamically builds `JTable` columns and Advanced Search side panels by interrogating the selected database table schema.
3. **`DynamicDBManagerCLI.java` (Command Line UI):**
   - Provides an interactive text-based menu for terminal users.
   - Features paginated results and an incremental Advanced Search filter (using the `add` keyword).
4. **`DynamicDAO.java` (Data Access Object):**
   - Connects to the SQLite database.
   - Uses native `SELECT name FROM sqlite_master` and `PRAGMA table_info()` queries to discover tables and columns dynamically.
   - Executes dynamic `INSERT`, `SELECT`, `UPDATE`, and `DELETE` commands based on maps of data.


## 📁 Project Structure

```text
📁 DynamicDatabaseManager/
├── database/                (Local Database Storage - Ignored by Git)
│   └── students.db          (Generated automatically when app runs)
├── lib/
│   ├── slf4j-api.jar        (SLF4J Logging API)
│   ├── slf4j-simple.jar     (SLF4J Simple Binding)
│   └── sqlite-jdbc.jar      (SQLite JDBC Driver)
├── src/
│   ├── Main.java                 (Entrypoint)
│   ├── DynamicDBManagerGUI.java  (Main GUI application)
│   ├── DynamicDBManagerCLI.java  (Main CLI application)
│   └── DynamicDAO.java           (Dynamic Data Access Object)
├── build.sh                 (Compilation script for macOS/Linux)
├── run.sh                   (Execution script for macOS/Linux)
├── build.bat                (Compilation script for Windows)
├── run.bat                  (Execution script for Windows)
└── Dockerfile               (Docker container specification)
```

## 🚀 Setup & Execution

### 1. Building the Project
Before running the application, you must compile the Java source files. Build scripts are provided that will intelligently check if Java is installed and offer to automatically install it via `brew`, `apt`, or `winget` if it is missing!

**On macOS / Linux:**
1. Open your terminal in the project directory.
2. Ensure the scripts are executable:
   ```bash
   chmod +x build.sh run.sh
   ```
3. Compile the project:
   ```bash
   ./build.sh
   ```

**On Windows:**
1. Open Command Prompt in the project directory.
2. Compile the project:
   ```cmd
   .\build.bat
   ```

### 2. Running the Program (GUI Mode)
**On macOS / Linux:**
```bash
./run.sh
```

**On Windows:**
```cmd
.\run.bat
```

### 3. Running the Program (CLI Mode)
If you prefer a terminal-based interface (which is particularly great for headless servers or remote SSH), just pass the `CLI` argument:

**On macOS / Linux:**
```bash
./run.sh CLI
```

**On Windows:**
```cmd
.\run.bat CLI
```

## ✨ Features
- **Dual Interfaces:** Seamlessly switch between a full desktop GUI or a fast interactive CLI.
- **Dynamic Schema Discovery:** Reads any SQLite database and auto-generates UI components based on `PRAGMA` queries.
- **DDL Table Creation:** Dynamically construct and create tables with custom datatypes and constraints directly from the app.
- **Add Record:** Dynamically insert rows into any discovered table.
- **Edit Record:** Use the dynamically generated GUI side panel or CLI prompt to modify any field.
- **Advanced Search:** Filter data across multiple columns simultaneously in both GUI and CLI.
- **Delete Record:** Accurately target and delete rows.

## 🐳 Docker Support

While this project is a Desktop GUI application using Java Swing, it is fully supported via Docker by utilizing X11 forwarding.

### 1. Build the Docker Image
```bash
docker build -t student-management-sys .
```

### 2. Run the Docker Container

Running a graphical UI container depends on your operating system:

#### Option A: On Linux (Native X11)
To allow the Docker container to render the Java Swing GUI on your host machine, you must share your X11 socket and `DISPLAY`:
```bash
# Allow local connections to X11 (run on host machine)
xhost +local:

# Run the container with X11 volume mapping
docker run -it --rm \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  student-management-sys
```

#### Option B: On macOS (Using XQuartz)
Because macOS doesn't use X11 natively, the Linux method will throw errors. You must use XQuartz:
1. Install XQuartz: `brew install --cask xquartz`
2. Open XQuartz, go to **Preferences > Security** and check **"Allow connections from network clients"**.
3. **Restart XQuartz** (or restart your Mac).
4. Run this in your macOS terminal to allow connections:
   ```bash
   xhost +localhost
   ```
5. Run the container using `host.docker.internal`:
   ```bash
   docker run -it --rm \
     -e DISPLAY=host.docker.internal:0 \
     student-management-sys
   ```

*(Note: To run the CLI version via Docker without X11, simply append `CLI` to the run command: `docker run -it --rm student-management-sys CLI`)*

#### Option C: On Windows
*(Note for Windows Users: Running Docker X11 GUI applications requires an X Server installed on Windows like VcXsrv or Xming, and configuring the DISPLAY variable to your host IP).*

## 🤝 Git & Collaboration
This project has been initialized as a Git repository. To collaborate and push this to a remote private repository, you can run the following commands:
```bash
git remote add origin <YOUR_PRIVATE_REPO_URL>
git branch -M main
git push -u origin main
```
