package org.example.Services;

import org.example.DataAccess.DataAccess;
import org.example.models.ConnectDB;
import org.example.models.User;
import org.example.utils.Exceptions.*;
import org.example.utils.Validators;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserService {

    private final Connection conn;
    private final WalletService walletService;  // Constructor Injection

    public UserService(WalletService walletService) {
        this.conn = new ConnectDB().getConnection();
        this.walletService = walletService;
    }

    public boolean checkIfUserExists(String username) throws DatabaseException {
        System.out.println("Fetching user: " + username);
        ArrayList<ArrayList<Object>> userDataList;
        try {
            userDataList = DataAccess.executeQuery(this.conn, "user", "*", "username=?", username);
        } catch (SQLException e) {
            throw new DatabaseException("Internal Server Error!!");
        }
        return !userDataList.isEmpty();
    }

    public void addUser(String username, String password, String mudraPin)
            throws SQLException, DatabaseException, UserAlreadyExistsException {

        // ✅ Validate the username, password, and PIN before proceeding
        if (!Validators.checkUsernameFormat(username)) {
            throw new IllegalArgumentException("Invalid username format.");
        }
        if (!Validators.checkPasswordFormat(password)) {
            throw new IllegalArgumentException("Invalid password format.");
        }
        if (!Validators.checkPinFormat(mudraPin)) {
            throw new IllegalArgumentException("Invalid PIN format.");
        }

        try {
            conn.setAutoCommit(false);
            if (checkIfUserExists(username)) {
                throw new UserAlreadyExistsException("User already Exists!!");
            }

            DataAccess.executeUpdate(this.conn, "user", "insert",
                    "(username, password, mudraPin) values (?, ?, ?)", "", username, password, mudraPin);

            DataAccess.executeUpdate(this.conn, "wallet", "insert",
                    "(username, balance) values (?, ?)", "", username, 0);

        } catch (SQLException e) {
            conn.rollback();
            throw new DatabaseException("Internal Server Error!");
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public User getUser(String username) throws SQLException, UserNotFoundException, DatabaseException {
        if (!checkIfUserExists(username)) {
            throw new UserNotFoundException("User not found!!");
        }

        ArrayList<ArrayList<Object>> userDataList;
        try {
            userDataList = DataAccess.executeQuery(this.conn, "user", "*", "username=?", username);
        } catch (SQLException e) {
            throw new DatabaseException("Internal Server Error!!");
        }

        ArrayList<Object> userData = userDataList.getFirst();
        String hashedPassword = (String) userData.get(1);
        String mudraPin = (String) userData.get(2);
        return new User(username, hashedPassword, mudraPin);
    }

    public void updatePassword(String username, String enteredPassword, String newPassword)
            throws UserNotFoundException, SQLException, DatabaseException {

        if (!Validators.checkPasswordFormat(newPassword)) {
            throw new IllegalArgumentException("Invalid password format.");
        }
        if (verifyPassword(username, enteredPassword)) {
            DataAccess.executeUpdate(this.conn, "user", "update",
                    "set password=?", "username=?", newPassword, username);
            System.out.println("Password updated successfully!");
        } else {
            throw new SQLException("Incorrect current password!");
        }
    }

    public void updatePin(String username, String enteredPin, String newPin)
            throws UserNotFoundException, SQLException, DatabaseException {
        if (!Validators.checkPinFormat(newPin)) {
            throw new IllegalArgumentException("Invalid PIN format.");
        }

        if (verifyPin(username, enteredPin)) {
            DataAccess.executeUpdate(this.conn, "user", "update",
                    "set mudraPin=?", "username=?", newPin, username);
            System.out.println("Pin updated successfully!");
        } else {
            throw new SQLException("Incorrect current pin!");
        }
    }
    public boolean verifyPin(String username, String enteredPin)
            throws UserNotFoundException, SQLException, DatabaseException {
        User userDetails = getUser(username);
        return userDetails.getPin().equals(enteredPin);
    }
    public boolean verifyPassword(String username, String enteredPassword)
            throws UserNotFoundException, SQLException, DatabaseException {
        User userDetails = getUser(username);
        return userDetails.getHashedPassword().equals(enteredPassword);
    }
}
