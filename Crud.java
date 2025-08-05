import java.sql.*;
import java.util.Scanner;

public class Crud {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String USER = "root";
    private static final String PASS = "root1234"; 

    private Connection connection;
    private Scanner scanner;

    // Constructor
    public Crud() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully!");
            scanner = new Scanner(System.in);
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("Database connection failed. Check your DB_URL, USER, PASS, and ensure MySQL is running.");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.err.println("JDBC Driver not found. Make sure mysql-connector-j-x.x.x.jar is in your classpath.");
        }
    }

    public void start() {
        if (connection == null) {
            System.err.println("Application cannot start without a database connection.");
            return;
        }

        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    viewAllProducts();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    System.out.println("Exiting Inventory Application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n------------------------------------\n");
        } while (choice != 5);

        closeResources();
    }

    private void displayMenu() {
        System.out.println("--- Simple Inventory Application ---");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Update Product Quantity/Price");
        System.out.println("4. Delete Product");
        System.out.println("5. Exit");
    }

    private void addProduct() {
        try {
            System.out.print("Enter product name: ");
            String name = scanner.nextLine();
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            System.out.print("Enter price: ");
            double price = scanner.nextDouble();

            String sql = "INSERT INTO inventory (name, quantity, price) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Product added.");
            } else {
                System.out.println("Failed to add product.");
            }
        } catch (SQLException e) {
            System.err.println("Error while adding product.");
            e.printStackTrace();
        }
    }

    private void viewAllProducts() {
        try {
            String sql = "SELECT * FROM inventory";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("ID\tName\tQuantity\tPrice");
            while (rs.next()) {
                System.out.printf("%d\t%s\t%d\t\t%.2f\n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching products.");
            e.printStackTrace();
        }
    }

    private void updateProduct() {
        try {
            System.out.print("Enter product ID to update: ");
            int id = scanner.nextInt();
            System.out.print("Enter new quantity: ");
            int quantity = scanner.nextInt();
            System.out.print("Enter new price: ");
            double price = scanner.nextDouble();

            String sql = "UPDATE inventory SET quantity = ?, price = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, quantity);
            stmt.setDouble(2, price);
            stmt.setInt(3, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Product updated.");
            } else {
                System.out.println("Product ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error while updating product.");
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        try {
            System.out.print("Enter product ID to delete: ");
            int id = scanner.nextInt();

            String sql = "DELETE FROM inventory WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Product deleted.");
            } else {
                System.out.println("Product ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting product.");
            e.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // Main method
    public static void main(String[] args) {
        Crud app = new Crud();
        app.start();
    }
}