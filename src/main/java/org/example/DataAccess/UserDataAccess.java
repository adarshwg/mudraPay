package org.example.DataAccess;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDataAccess {
    public static void deleteUser(Connection conn, String username) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM user WHERE username = ?")) {
            conn.setAutoCommit(false);
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("User not found.");
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException("Failed to delete user: " + e.getMessage());
        }
    }
    public static void addUser(Connection conn,String username, String hashedPassword, Integer mudraPin) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (username, password, mudraPin) VALUES (?, ?, ?)")) {
            conn.setAutoCommit(false);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setInt(3, mudraPin);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException();
        }
    }
    public static boolean checkIfUserExists(Connection conn, String enteredUsername) throws  SQLException{
        try (PreparedStatement stmt = conn.prepareStatement("select username from user where username=?")) {
            stmt.setString(1,enteredUsername);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    public static String getHashedUserPassword(Connection conn, String enteredUsername) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("select password from user where username=?")) {
            stmt.setString(1,enteredUsername);
            ResultSet rs = stmt.executeQuery();
            return rs.getString("password");
        }
    }
    public static String getUserMudraPin(Connection conn, String enteredUsername) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("select mudraPin from user where username=?")) {
            stmt.setString(1,enteredUsername);
            ResultSet rs = stmt.executeQuery();
            return rs.getString("mudraPin");
        }
    }
    public static void updateUserPassword(Connection conn,String username, String enteredPassword) throws SQLException {
        try(PreparedStatement stmt = conn.prepareStatement("update user set password=? where username=?")){
            stmt.setString(1,enteredPassword);
            stmt.setString(2,username);
            stmt.executeUpdate();
            conn.commit();
        }
    }
    public static void updateUserMudraPin(Connection conn,String username, Integer enteredMudraPin) throws SQLException {
        try(PreparedStatement stmt = conn.prepareStatement("update user set mudraPin=? where username=?")){
            stmt.setInt(1,enteredMudraPin);
            stmt.setString(2,username);
            stmt.executeUpdate();
            conn.commit();
        }
    }


}
