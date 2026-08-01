import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppGUI extends JFrame {
    private JTextField txtName, txtRoll, txtDept;
    private JTable table;
    private DefaultTableModel model;

    // Side panel components
    private JPanel sidePanel;
    private JTextField editName, editRoll, editDept;
    private int selectedIdToEdit = -1;

    public AppGUI() {
        setTitle("Student Management System (SQLite)");
        setSize(800, 400); // Increased width to accommodate side panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Input Form (Top)
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
        JButton btnEdit = new JButton("Edit Selected");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(northPanel, BorderLayout.NORTH);

        // Table (Center)
        model = new DefaultTableModel(new String[]{"ID", "Name", "Roll No", "Department"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Side Panel (East - Initially Hidden)
        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        sidePanel.setPreferredSize(new Dimension(250, 0));
        
        sidePanel.add(new JLabel("Edit Name:"));
        editName = new JTextField();
        editName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        sidePanel.add(editName);
        sidePanel.add(Box.createVerticalStrut(10));

        sidePanel.add(new JLabel("Edit Roll No:"));
        editRoll = new JTextField();
        editRoll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        sidePanel.add(editRoll);
        sidePanel.add(Box.createVerticalStrut(10));

        sidePanel.add(new JLabel("Edit Department:"));
        editDept = new JTextField();
        editDept.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        sidePanel.add(editDept);
        sidePanel.add(Box.createVerticalStrut(20));

        JButton btnSaveEdit = new JButton("Save Updates");
        JButton btnCancelEdit = new JButton("Cancel");
        JPanel sideButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sideButtonPanel.add(btnSaveEdit);
        sideButtonPanel.add(btnCancelEdit);
        sidePanel.add(sideButtonPanel);

        sidePanel.setVisible(false); // Hidden by default
        add(sidePanel, BorderLayout.EAST);

        // Actions
        btnAdd.addActionListener(e -> addRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnEdit.addActionListener(e -> openEditPanel());
        btnSaveEdit.addActionListener(e -> saveEdit());
        btnCancelEdit.addActionListener(e -> sidePanel.setVisible(false));

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

    private void openEditPanel() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            selectedIdToEdit = (int) model.getValueAt(selectedRow, 0);
            editName.setText((String) model.getValueAt(selectedRow, 1));
            editRoll.setText((String) model.getValueAt(selectedRow, 2));
            editDept.setText((String) model.getValueAt(selectedRow, 3));
            sidePanel.setVisible(true);
            revalidate();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to edit.");
        }
    }

    private void saveEdit() {
        if (selectedIdToEdit == -1) return;

        String name = editName.getText().trim();
        String roll = editRoll.getText().trim();
        String dept = editDept.getText().trim();

        if (name.isEmpty() || roll.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        if (StudentDAO.updateStudent(new Student(selectedIdToEdit, name, roll, dept))) {
            sidePanel.setVisible(false);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Error updating student (Roll No must be unique).");
        }
    }

    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            StudentDAO.deleteStudent(id);
            refreshTable();
            if (id == selectedIdToEdit) {
                sidePanel.setVisible(false);
            }
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
}
