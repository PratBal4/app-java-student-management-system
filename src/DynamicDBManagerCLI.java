import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class DynamicDBManagerCLI {
    private static final Scanner scanner = new Scanner(System.in);
    private static DynamicDAO dao;

    public static void start() {
        System.out.println("Starting Dynamic CLI mode...");
        File dbFolder = new File("database");
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }

        while (true) {
            System.out.println("\n--- Database Selection ---");
            File[] files = dbFolder.listFiles((dir, name) -> name.endsWith(".db"));
            List<String> dbFiles = new ArrayList<>();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    System.out.println((i + 1) + ". " + files[i].getName());
                    dbFiles.add(files[i].getName());
                }
            } else {
                System.out.println("No database files found.");
            }
            
            System.out.println("\nOptions:");
            System.out.println("[number] Select a database");
            System.out.println("[C] Create new default database (students.db)");
            System.out.println("[D] Delete a database");
            System.out.println("[E] Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            if (choice.equals("E")) {
                System.out.println("Exiting...");
                return;
            } else if (choice.equals("C")) {
                dao = new DynamicDAO("database/students.db");
                dao.initializeDefaultDatabase();
                System.out.println("Created students.db. Proceeding...");
                handleTables();
            } else if (choice.equals("D")) {
                System.out.print("Enter the number of the database to delete: ");
                try {
                    int delIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (delIdx >= 0 && delIdx < dbFiles.size()) {
                        File fileToDelete = new File("database/" + dbFiles.get(delIdx));
                        try {
                            Files.delete(fileToDelete.toPath());
                            System.out.println("Successfully deleted " + dbFiles.get(delIdx));
                        } catch (IOException e) {
                            System.out.println("Error: Could not delete file. It may be locked or you lack permissions. Exception: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Invalid selection.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            } else {
                try {
                    int idx = Integer.parseInt(choice) - 1;
                    if (idx >= 0 && idx < dbFiles.size()) {
                        dao = new DynamicDAO("database/" + dbFiles.get(idx));
                        handleTables();
                    } else {
                        System.out.println("Invalid selection.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
        }
    }

    private static void handleTables() {
        while (true) {
            List<String> tables = dao.getTables();
            if (tables.isEmpty()) {
                System.out.println("No tables found in this database. Returning to menu...");
                return;
            }

            System.out.println("\n--- Tables ---");
            for (int i = 0; i < tables.size(); i++) {
                System.out.println((i + 1) + ". " + tables.get(i));
            }
            System.out.println("0. Back");
            System.out.print("Select a table: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) return;
                if (choice > 0 && choice <= tables.size()) {
                    handleTableOperations(tables.get(choice - 1));
                } else {
                    System.out.println("Invalid selection.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private static void handleTableOperations(String tableName) {
        while (true) {
            System.out.println("\n--- Operations for Table: " + tableName + " ---");
            System.out.println("1. Add Record");
            System.out.println("2. Display All Records");
            System.out.println("3. Advanced Search");
            System.out.println("4. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addRecord(tableName);
                    break;
                case "2":
                    handlePagination(tableName, new HashMap<>());
                    break;
                case "3":
                    advancedSearch(tableName);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addRecord(String tableName) {
        List<String> columns = dao.getColumns(tableName);
        Map<String, String> data = new LinkedHashMap<>();
        for (String col : columns) {
            if (col.equalsIgnoreCase("id")) continue; // Skip auto-incrementing ID usually
            System.out.print("Enter " + col + " (or leave blank if null): ");
            String val = scanner.nextLine().trim();
            if (!val.isEmpty()) {
                data.put(col, val);
            }
        }
        if (dao.insertRecord(tableName, data)) {
            System.out.println("Success: Record added!");
        } else {
            System.out.println("Error: Failed to add record.");
        }
    }

    private static void advancedSearch(String tableName) {
        List<String> columns = dao.getColumns(tableName);
        Map<String, String> filters = new LinkedHashMap<>();
        
        System.out.println("Available fields: " + String.join(", ", columns));
        System.out.print("Enter initial field to filter by (or press Enter to skip): ");
        String field = scanner.nextLine().trim();
        
        if (!field.isEmpty()) {
            if (!columns.contains(field)) {
                System.out.println("Error: Invalid field.");
                return;
            }
            System.out.print("Enter value for " + field + ": ");
            String val = scanner.nextLine().trim();
            filters.put(field, val);
        }

        handlePagination(tableName, filters);
    }

    private static void handlePagination(String tableName, Map<String, String> currentFilters) {
        List<String> columns = dao.getColumns(tableName);
        int currentIndex = 0;
        int pageSize = 10;

        while (true) {
            List<Map<String, Object>> results = dao.searchRecords(tableName, currentFilters);
            
            if (results.isEmpty()) {
                System.out.println("\nNo records found with current filters.");
            } else {
                System.out.println("\n--- Records (" + (currentIndex + 1) + " to " + Math.min(currentIndex + pageSize, results.size()) + " of " + results.size() + ") ---");
                for (int i = currentIndex; i < Math.min(currentIndex + pageSize, results.size()); i++) {
                    Map<String, Object> row = results.get(i);
                    StringBuilder sb = new StringBuilder();
                    for (String col : columns) {
                        sb.append(col).append(": ").append(row.get(col)).append(" | ");
                    }
                    System.out.println(sb.toString());
                }
            }

            System.out.print("\nEnter command ('up', 'down', 'add' filter, 'modify', 'delete', 'exit'): ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            if (cmd.equals("exit")) {
                return;
            } else if (cmd.equals("down")) {
                if (currentIndex + pageSize < results.size()) currentIndex += pageSize;
                else System.out.println("At end of list.");
            } else if (cmd.equals("up")) {
                if (currentIndex - pageSize >= 0) currentIndex -= pageSize;
                else System.out.println("At beginning of list.");
            } else if (cmd.equals("add")) {
                if (currentFilters.size() >= columns.size()) {
                    System.out.println("Error: All fields have already been filtered!");
                } else {
                    System.out.print("Enter field to add filter (available: " + String.join(", ", columns) + "): ");
                    String newField = scanner.nextLine().trim();
                    if (!columns.contains(newField)) {
                        System.out.println("Invalid field.");
                    } else if (currentFilters.containsKey(newField)) {
                        System.out.println("Error: Field already used in filter.");
                    } else {
                        System.out.print("Enter value for " + newField + ": ");
                        String newVal = scanner.nextLine().trim();
                        currentFilters.put(newField, newVal);
                        currentIndex = 0; // Reset pagination on new search
                    }
                }
            } else if (cmd.equals("modify")) {
                executeModify(tableName, columns);
            } else if (cmd.equals("delete")) {
                executeDelete(tableName, columns);
            } else {
                System.out.println("Invalid command.");
            }
        }
    }

    private static void executeDelete(String tableName, List<String> columns) {
        System.out.println("Identify the record to delete:");
        System.out.print("Enter filter field (" + String.join(", ", columns) + "): ");
        String field = scanner.nextLine().trim();
        System.out.print("Enter value: ");
        String val = scanner.nextLine().trim();
        
        Map<String, String> filters = new HashMap<>();
        filters.put(field, val);
        
        if (dao.deleteRecord(tableName, filters)) {
            System.out.println("Success: Record deleted.");
        } else {
            System.out.println("Error: Could not delete record. No modifications made.");
        }
    }

    private static void executeModify(String tableName, List<String> columns) {
        System.out.println("Identify the record to modify:");
        System.out.print("Enter filter field (" + String.join(", ", columns) + "): ");
        String filterField = scanner.nextLine().trim();
        System.out.print("Enter value: ");
        String filterVal = scanner.nextLine().trim();
        
        Map<String, String> filters = new HashMap<>();
        filters.put(filterField, filterVal);
        
        System.out.print("Enter field to modify: ");
        String modField = scanner.nextLine().trim();
        System.out.print("Enter new value: ");
        String newVal = scanner.nextLine().trim();
        
        Map<String, String> updates = new HashMap<>();
        updates.put(modField, newVal);
        
        if (dao.updateRecord(tableName, filters, updates)) {
            System.out.println("Success: Record modified.");
        } else {
            System.out.println("Error: Could not modify record. No modifications made.");
        }
    }
}
