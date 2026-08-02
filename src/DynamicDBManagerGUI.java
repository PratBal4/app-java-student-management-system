import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class DynamicDBManagerGUI extends JFrame {
    private DynamicDAO dao;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> tableSelector;
    private String currentTable = "";
    private Map<String, String> currentSearchFilters = new HashMap<>();

    public void start() {
        File dbFolder = new File("database");
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }

        while (true) {
            File[] files = dbFolder.listFiles((dir, name) -> name.endsWith(".db"));
            List<String> dbFiles = new ArrayList<>();
            if (files != null) {
                for (File f : files) dbFiles.add(f.getName());
            }

            dbFiles.add("++ Create New Database ++");
            
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            JComboBox<String> fileCombo = new JComboBox<>(dbFiles.toArray(new String[0]));
            panel.add(new JLabel("Select a Database:"), BorderLayout.NORTH);
            panel.add(fileCombo, BorderLayout.CENTER);

            Object[] options = {"Open", "Delete", "Exit"};
            int result = JOptionPane.showOptionDialog(null, panel, "Database Selector",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (result == 2 || result == JOptionPane.CLOSED_OPTION) { // Exit
                System.exit(0);
            } else if (result == 1) { // Delete
                String selected = (String) fileCombo.getSelectedItem();
                if (selected != null && !selected.startsWith("++")) {
                    try {
                        Files.delete(new File("database/" + selected).toPath());
                        JOptionPane.showMessageDialog(null, "Deleted successfully.");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Error deleting file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (result == 0) { // Open
                String selected = (String) fileCombo.getSelectedItem();
                if (selected != null) {
                    if (selected.startsWith("++")) {
                        String newDbName = JOptionPane.showInputDialog(null, "Enter new database name (e.g., inventory.db):");
                        if (newDbName != null && !newDbName.trim().isEmpty()) {
                            if (!newDbName.endsWith(".db")) newDbName += ".db";
                            dao = new DynamicDAO("database/" + newDbName);
                            break;
                        }
                    } else {
                        dao = new DynamicDAO("database/" + selected);
                        break;
                    }
                }
            }
        }
        
        initializeMainGUI();
    }

    private void initializeMainGUI() {
        setTitle("Dynamic Database Manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel: Table Selector & Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableSelector = new JComboBox<>();
        refreshTableSelector();
        
        tableSelector.addActionListener(e -> {
            if (tableSelector.getSelectedItem() != null) {
                currentTable = (String) tableSelector.getSelectedItem();
                currentSearchFilters.clear();
                refreshTable();
            }
        });

        JButton createTableBtn = new JButton("Create Table");
        createTableBtn.addActionListener(e -> openCreateTableDialog());

        JButton searchBtn = new JButton("Advanced Search");
        searchBtn.addActionListener(e -> openSearchPanel());

        JButton clearSearchBtn = new JButton("Clear Search");
        clearSearchBtn.addActionListener(e -> {
            currentSearchFilters.clear();
            refreshTable();
        });

        topPanel.add(new JLabel("Table: "));
        topPanel.add(tableSelector);
        topPanel.add(createTableBtn);
        topPanel.add(searchBtn);
        topPanel.add(clearSearchBtn);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Data Table
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Panel: Add, Modify, Delete
        JPanel bottomPanel = new JPanel();
        JButton addBtn = new JButton("Add Record");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");

        addBtn.addActionListener(e -> openRecordPanel(false));
        editBtn.addActionListener(e -> openRecordPanel(true));
        deleteBtn.addActionListener(e -> deleteSelectedRecord());

        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        if (tableSelector.getItemCount() > 0) {
            tableSelector.setSelectedIndex(0); // Triggers refreshTable()
        }

        setVisible(true);
    }

    private void refreshTable() {
        if (currentTable == null || currentTable.isEmpty()) return;

        List<String> columns = dao.getColumns(currentTable);
        tableModel.setColumnIdentifiers(columns.toArray());
        tableModel.setRowCount(0);

        List<Map<String, Object>> records = dao.searchRecords(currentTable, currentSearchFilters);
        for (Map<String, Object> row : records) {
            Object[] rowData = new Object[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                rowData[i] = row.get(columns.get(i));
            }
            tableModel.addRow(rowData);
        }
    }

    private void openSearchPanel() {
        if (currentTable == null || currentTable.isEmpty()) return;
        List<String> columns = dao.getColumns(currentTable);
        
        JPanel panel = new JPanel(new GridLayout(columns.size(), 2, 5, 5));
        Map<String, JTextField> fields = new HashMap<>();
        
        for (String col : columns) {
            panel.add(new JLabel(col + ":"));
            JTextField tf = new JTextField();
            if (currentSearchFilters.containsKey(col)) {
                tf.setText(currentSearchFilters.get(col));
            }
            fields.put(col, tf);
            panel.add(tf);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, "Advanced Search", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            currentSearchFilters.clear();
            for (Map.Entry<String, JTextField> entry : fields.entrySet()) {
                String val = entry.getValue().getText().trim();
                if (!val.isEmpty()) {
                    currentSearchFilters.put(entry.getKey(), val);
                }
            }
            refreshTable();
        }
    }

    private void openRecordPanel(boolean isEdit) {
        if (currentTable == null || currentTable.isEmpty()) return;
        
        int selectedRow = table.getSelectedRow();
        if (isEdit && selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to edit.");
            return;
        }

        List<String> columns = dao.getColumns(currentTable);
        JPanel panel = new JPanel(new GridLayout(columns.size(), 2, 5, 5));
        Map<String, JTextField> fields = new LinkedHashMap<>();
        
        Map<String, String> originalFilters = new HashMap<>();

        for (int i = 0; i < columns.size(); i++) {
            String col = columns.get(i);
            panel.add(new JLabel(col + ":"));
            JTextField tf = new JTextField();
            if (isEdit) {
                Object val = tableModel.getValueAt(selectedRow, i);
                String strVal = (val == null) ? "" : val.toString();
                tf.setText(strVal);
                originalFilters.put(col, strVal);
                if (col.equalsIgnoreCase("id")) tf.setEditable(false);
            } else if (col.equalsIgnoreCase("id")) {
                tf.setEditable(false);
                tf.setText("Auto");
            }
            fields.put(col, tf);
            panel.add(tf);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, isEdit ? "Edit Record" : "Add Record", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Map<String, String> data = new HashMap<>();
            for (Map.Entry<String, JTextField> entry : fields.entrySet()) {
                String val = entry.getValue().getText().trim();
                if (!val.isEmpty() && !val.equals("Auto")) {
                    data.put(entry.getKey(), val);
                }
            }
            
            if (isEdit) {
                if (dao.updateRecord(currentTable, originalFilters, data)) {
                    JOptionPane.showMessageDialog(this, "Record updated.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update record.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (dao.insertRecord(currentTable, data)) {
                    JOptionPane.showMessageDialog(this, "Record added.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add record.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            refreshTable();
        }
    }

    private void deleteSelectedRecord() {
        if (currentTable == null || currentTable.isEmpty()) return;
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<String> columns = dao.getColumns(currentTable);
            Map<String, String> filters = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                Object val = tableModel.getValueAt(selectedRow, i);
                if (val != null) filters.put(columns.get(i), val.toString());
            }

            if (dao.deleteRecord(currentTable, filters)) {
                JOptionPane.showMessageDialog(this, "Record deleted.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshTableSelector() {
        tableSelector.removeAllItems();
        List<String> tables = dao.getTables();
        for (String t : tables) {
            tableSelector.addItem(t);
        }
    }

    private void openCreateTableDialog() {
        String tableName = JOptionPane.showInputDialog(this, "Enter new table name:");
        if (tableName == null || tableName.trim().isEmpty()) return;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JPanel columnsPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        columnsPanel.add(new JLabel("Column Name"));
        columnsPanel.add(new JLabel("Data Type"));
        columnsPanel.add(new JLabel("Constraints (e.g. PRIMARY KEY)"));

        List<JTextField> nameFields = new ArrayList<>();
        List<JComboBox<String>> typeBoxes = new ArrayList<>();
        List<JTextField> constraintFields = new ArrayList<>();

        JButton addRowBtn = new JButton("Add Column");
        addRowBtn.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JComboBox<String> typeBox = new JComboBox<>(new String[]{"INTEGER", "TEXT", "REAL", "BLOB"});
            JTextField constraintField = new JTextField();
            
            nameFields.add(nameField);
            typeBoxes.add(typeBox);
            constraintFields.add(constraintField);
            
            columnsPanel.add(nameField);
            columnsPanel.add(typeBox);
            columnsPanel.add(constraintField);
            mainPanel.revalidate();
            mainPanel.repaint();
        });

        // Add initial row
        addRowBtn.doClick();

        mainPanel.add(new JScrollPane(columnsPanel));
        mainPanel.add(addRowBtn);

        int result = JOptionPane.showConfirmDialog(this, mainPanel, "Create Table: " + tableName, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            Map<String, String> columnDefs = new LinkedHashMap<>();
            for (int i = 0; i < nameFields.size(); i++) {
                String name = nameFields.get(i).getText().trim();
                if (name.isEmpty()) continue;
                
                String type = (String) typeBoxes.get(i).getSelectedItem();
                String constraints = constraintFields.get(i).getText().trim();
                
                columnDefs.put(name, type + " " + constraints);
            }
            
            if (dao.createTable(tableName, columnDefs)) {
                JOptionPane.showMessageDialog(this, "Table '" + tableName + "' created successfully!");
                refreshTableSelector();
                tableSelector.setSelectedItem(tableName);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create table.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
