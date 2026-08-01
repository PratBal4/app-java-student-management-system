import java.util.List;
import java.util.Scanner;

public class AppCLI {
    private static final Scanner scanner = new Scanner(System.in);

    public static void start() {
        System.out.println("Starting CLI mode...");
        while (true) {
            System.out.println("\n--- Student Management System CLI ---");
            System.out.println("1. Add Student");
            System.out.println("2. Modify Student");
            System.out.println("3. Display Students");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addStudent();
                    break;
                case "2":
                    handleSearchFlow("modify");
                    break;
                case "3":
                    handleSearchFlow("display");
                    break;
                case "4":
                    handleSearchFlow("delete");
                    break;
                case "5":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addStudent() {
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Roll No: ");
        String roll = scanner.nextLine().trim();
        System.out.print("Enter Department: ");
        String dept = scanner.nextLine().trim();

        if (name.isEmpty() || roll.isEmpty() || dept.isEmpty()) {
            System.out.println("Error: All fields must be filled!");
            return;
        }

        if (StudentDAO.addStudent(new Student(name, roll, dept))) {
            System.out.println("Success: Student added successfully!");
        } else {
            System.out.println("Error: Could not add student. (Roll No must be unique)");
        }
    }

    private static void handleSearchFlow(String mode) {
        System.out.println("\nAvailable fields: id, name, roll_no, department");
        System.out.print("Enter the field to filter by: ");
        String filterField = scanner.nextLine().trim().toLowerCase();
        if (!filterField.equals("id") && !filterField.equals("name") && !filterField.equals("roll_no") && !filterField.equals("department")) {
            System.out.println("Error: Invalid field.");
            return;
        }

        System.out.print("Enter value to search for: ");
        String filterValue = scanner.nextLine().trim();

        List<Student> results = StudentDAO.searchStudents(filterField, filterValue);
        if (results.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        int currentIndex = 0;
        int pageSize = 10;

        while (true) {
            System.out.println("\n--- Search Results (" + (currentIndex + 1) + " to " + Math.min(currentIndex + pageSize, results.size()) + " of " + results.size() + ") ---");
            for (int i = currentIndex; i < Math.min(currentIndex + pageSize, results.size()); i++) {
                Student s = results.get(i);
                System.out.println("ID: " + s.getId() + " | Name: " + s.getName() + " | Roll: " + s.getRollNo() + " | Dept: " + s.getDepartment());
            }

            System.out.print("\nEnter 'down' for next 10, 'up' for previous 10, '" + mode + "' to act, or 'exit' to return: ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            if (cmd.equals("exit")) {
                return;
            } else if (cmd.equals("down")) {
                if (currentIndex + pageSize < results.size()) {
                    currentIndex += pageSize;
                } else {
                    System.out.println("Already at the end of the list.");
                }
            } else if (cmd.equals("up")) {
                if (currentIndex - pageSize >= 0) {
                    currentIndex -= pageSize;
                } else {
                    System.out.println("Already at the beginning of the list.");
                }
            } else if (cmd.equals(mode)) {
                if (mode.equals("delete")) {
                    executeDelete(filterField, filterValue);
                    return;
                } else if (mode.equals("modify")) {
                    executeModify(filterField, filterValue);
                    return;
                } else {
                    System.out.println("Displaying mode. Type 'exit' to return.");
                }
            } else {
                System.out.println("Invalid command.");
            }
        }
    }

    private static void executeDelete(String primaryField, String primaryValue) {
        System.out.print("Enter a second filter field (id, name, roll_no, department): ");
        String secField = scanner.nextLine().trim().toLowerCase();
        System.out.print("Enter value for " + secField + ": ");
        String secValue = scanner.nextLine().trim();

        boolean success = StudentDAO.deleteStudentByFilters(primaryField, primaryValue, secField, secValue);
        if (success) {
            System.out.println("Success: The row has been deleted.");
        } else {
            System.out.println("Error: There was a problem deleting the row, no modifications were made.");
        }
    }

    private static void executeModify(String primaryField, String primaryValue) {
        System.out.print("Enter field to modify (name, roll_no, department): ");
        String modifyField = scanner.nextLine().trim().toLowerCase();
        System.out.print("Enter the current value of " + modifyField + " in that row: ");
        String currentVal = scanner.nextLine().trim();
        System.out.print("Enter the new value for " + modifyField + ": ");
        String newVal = scanner.nextLine().trim();

        boolean success = StudentDAO.updateStudentByFilters(primaryField, primaryValue, modifyField, currentVal, newVal);
        if (success) {
            System.out.println("Success: The row has been modified. New " + modifyField + " is: " + newVal);
        } else {
            System.out.println("Error: There was a problem modifying the row, no modifications were made.");
        }
    }
}
