import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:xdq_game.db";

    public DatabaseManager() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player1 TEXT,
                    player2 TEXT,
                    winner TEXT,
                    date_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
        } catch (SQLException e) {
            System.out.println("Erreur SQLite : " + e.getMessage());
        }
    }

    public void insertUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(username, password) VALUES(?,?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur insertUser : " + e.getMessage());
        }
    }

    public boolean authenticate(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("Erreur authenticate : " + e.getMessage());
            return false;
        }
    }

    public void insertGame(String player1, String player2, String winner) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO history(player1, player2, winner) VALUES(?,?,?)");
            ps.setString(1, player1);
            ps.setString(2, player2);
            ps.setString(3, winner);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur insertGame : " + e.getMessage());
        }
    }

    public void showHistory() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM history ORDER BY date_played DESC");

            System.out.println("\n=== Historique des parties ===");
            while (rs.next()) {
                System.out.printf("Match : %s vs %s | Gagnant : %s | Date : %s\n",
                        rs.getString("player1"),
                        rs.getString("player2"),
                        rs.getString("winner"),
                        rs.getString("date_played"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur showHistory : " + e.getMessage());
        }
    }
}
