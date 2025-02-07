package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String URL = "jdbc:postgresql://ep-weathered-frog-a5h9s5u2-pooler.us-east-2.aws.neon.tech:5432/neondb";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_2anZdxqHh6eK";

    public static Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Помилка підключення: " + e.getMessage());
            return null;
        }
    }

    // Додати нову гру
    public static void createGame(Connection conn, String title, String developer, String publisher, String releaseDate, double price) {
        String sql = "INSERT INTO games (title, developer, publisher, release_date, price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, developer);
            stmt.setString(3, publisher);
            stmt.setDate(4, Date.valueOf(releaseDate));
            stmt.setDouble(5, price);
            stmt.executeUpdate();
            System.out.println("Гру додано успішно!");
        } catch (SQLException e) {
            System.out.println("Помилка при додаванні: " + e.getMessage());
        }
    }

    // Отримати всі ігри
    public static void readGames(Connection conn) {
        String sql = "SELECT * FROM games";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("ID: %d | Назва: %s | Розробник: %s | Видавець: %s | Дата виходу: %s | Ціна: %.2f\n",
                        rs.getInt("id"), rs.getString("title"), rs.getString("developer"),
                        rs.getString("publisher"), rs.getDate("release_date"), rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("Помилка при читанні: " + e.getMessage());
        }
    }

    // Оновити гру
    public static void updateGame(Connection conn, int id, String newTitle, double newPrice) {
        String sql = "UPDATE games SET title = ?, price = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newTitle);
            stmt.setDouble(2, newPrice);
            stmt.setInt(3, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Гру оновлено!");
            } else {
                System.out.println("Гру з таким ID не знайдено.");
            }
        } catch (SQLException e) {
            System.out.println("Помилка при оновленні: " + e.getMessage());
        }
    }

    // Видалити гру
    public static void deleteGame(Connection conn, int id) {
        String sql = "DELETE FROM games WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Гру видалено!");
            } else {
                System.out.println("Гру з таким ID не знайдено.");
            }
        } catch (SQLException e) {
            System.out.println("Помилка при видаленні: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Connection conn = connect();
        if (conn == null) return;

        String sql = """
                CREATE TABLE IF NOT EXISTS games (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(100) NOT NULL,
                    developer VARCHAR(100) NOT NULL,
                    publisher VARCHAR(100) NOT NULL,
                    release_date DATE NOT NULL,
                    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0)
                );
                """;
        try(var command = conn.createStatement()) {
            int rows = command.executeUpdate(sql);
            System.out.println("Таблицю створено. Оновлено записів: " + rows);
        } catch(SQLException ex) {
            System.out.println("Щось пішло не так "+ ex.getMessage());
        }


        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Додати гру");
            System.out.println("2. Показати всі ігри");
            System.out.println("3. Оновити гру");
            System.out.println("4. Видалити гру");
            System.out.println("5. Вийти");
            System.out.print("Виберіть опцію: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера

            switch (choice) {
                case 1:
                    System.out.print("Введіть назву: ");
                    String title = scanner.nextLine();
                    System.out.print("Введіть розробника: ");
                    String developer = scanner.nextLine();
                    System.out.print("Введіть видавця: ");
                    String publisher = scanner.nextLine();
                    System.out.print("Введіть дату виходу (YYYY-MM-DD): ");
                    String releaseDate = scanner.nextLine();
                    System.out.print("Введіть ціну: ");
                    double price = scanner.nextDouble();
                    createGame(conn, title, developer, publisher, releaseDate, price);
                    break;
                case 2:
                    readGames(conn);
                    break;
                case 3:
                    System.out.print("Введіть ID гри для оновлення: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Введіть нову назву: ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Введіть нову ціну: ");
                    double newPrice = scanner.nextDouble();
                    updateGame(conn, id, newTitle, newPrice);
                    break;
                case 4:
                    System.out.print("Введіть ID гри для видалення: ");
                    int deleteId = scanner.nextInt();
                    deleteGame(conn, deleteId);
                    break;
                case 5:
                    System.out.println("Вихід...");
                    return;
                default:
                    System.out.println("Невірний вибір!");
            }
        }
    }
}