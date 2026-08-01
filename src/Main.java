import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Database.initializeDatabase();
        if (args.length > 0 && args[0].equalsIgnoreCase("CLI")) {
            AppCLI.start();
        } else {
            SwingUtilities.invokeLater(() -> new AppGUI().setVisible(true));
        }
    }
}
