public class Student {
    private int id;
    private String name;
    private String rollNo;
    private String department;

    public Student(int id, String name, String rollNo, String department) {
        this.id = id;
        this.name = name;
        this.rollNo = rollNo;
        this.department = department;
    }

    public Student(String name, String rollNo, String department) {
        this(0, name, rollNo, department);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRollNo() { return rollNo; }
    public String getDepartment() { return department; }
}
