import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppGUI extends JFrame {
    private JTextField txtName, txtRoll, txtDept;
    private JTable table;
    private DefaultTableModel model;

    public AppGUI() {
        setTitle("Student Management System (SQLite)");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Input Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.add(new JLabel(" Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel(" Roll No:"));
        txtRoll = new JTextField();
        formPanel.add(txtRoll);

        formPanel.add(new JLabel(" Department:"));
        txtDept = new JTextField();
        formPanel.add(txtDept);

        JButton btnAdd = new JButton("Add Student");
        JButton btnDelete = new JButton("Delete Selected");
        formPanel.add(btnAdd);
        formPanel.add(btnDelete);

        add(formPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Name", "Roll No", "Department"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions
        btnAdd.addActionListener(e -> addRecord());
        btnDelete.addActionListener(e -> deleteRecord());

        refreshTable();
    }

    private void addRecord() {
        String name = txtName.getText().trim();
        String roll = txtRoll.getText().trim();
        String dept = txtDept.getText().trim();

        if (name.isEmpty() || roll.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        if (StudentDAO.addStudent(new Student(name, roll, dept))) {
            txtName.setText("");
            txtRoll.setText("");
            txtDept.setText("");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Error adding student (Roll No must be unique).");
        }
    }

    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            StudentDAO.deleteStudent(id);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Student> students = StudentDAO.getAllStudents();
        for (Student s : students) {
            model.addRow(new Object[]{s.getId(), s.getName(), s.getRollNo(), s.getDepartment()});
        }
    }

    public static void main(String[] args) {
        Database.initializeDatabase();
        SwingUtilities.invokeLater(() -> new AppGUI().setVisible(true));
    }
}
