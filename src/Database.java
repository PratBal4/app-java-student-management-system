import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DIR = "database";
    private static final String URL = "jdbc:sqlite:" + DIR + "/students.db";

    public static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "name TEXT NOT NULL, " +
                     "roll_no TEXT NOT NULL UNIQUE, " +
                     "department TEXT NOT NULL" +
                     ");";
                     
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Database init error: " + e.getMessage());
        }
    }
}
