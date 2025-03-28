package org.example.Services;
import org.example.DataAccess.DataAccess;
import org.example.models.ConnectDB;
import org.example.models.User;
import org.example.utils.AuthHasher;
import org.example.utils.Exceptions.*;
import org.example.utils.Validators;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

    public void addUser(String username, String hashedPassword, String hashedMudraPin)
            throws SQLException, DatabaseException, UserAlreadyExistsException {
        try {
            conn.setAutoCommit(false);
            if (checkIfUserExists(username)) {
                throw new UserAlreadyExistsException("User already Exists!!");
            }
            DataAccess.executeUpdate(this.conn, "user", "insert",
                    "(username, password, mudraPin) values (?, ?, ?)", "", username, hashedPassword, hashedMudraPin);
            DataAccess.executeUpdate(this.conn, "wallet", "insert",
                    "(username, balance) values (?, ?)", "", username, 0);
        } catch (SQLException e) {
            System.out.println("user adding sql error");
            System.out.println(e.getMessage());
            conn.rollback();
            throw new DatabaseException("Internal Server Error!"+e.getMessage());
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

            throw new DatabaseException("Internal Server Error!!"+e.getMessage());
        }

        ArrayList<Object> userData = userDataList.getFirst();
        String hashedPassword = (String) userData.get(1);
        String mudraPin = (String) userData.get(2);
        return new User(username, hashedPassword, mudraPin);
    }

    public void updatePassword(String username, String enteredPassword, String newPassword)
            throws UserNotFoundException, SQLException, DatabaseException, InvalidCredentials, NoSuchAlgorithmException, InvalidKeySpecException {

        if (!Validators.checkPasswordFormat(newPassword)) {
            throw new IllegalArgumentException("Invalid password format.");
        }
        if (verifyPassword(username, enteredPassword)) {
            DataAccess.executeUpdate(this.conn, "user", "update",
                    "set password=?", "username=?", AuthHasher.hashPassword(enteredPassword), username);
            System.out.println("Password updated successfully!");
        } else {
            throw new InvalidCredentials("Incorrect current password!");
        }
    }

    public void updatePin(String username, String enteredPin, String newPin)
            throws UserNotFoundException, SQLException, DatabaseException, InvalidCredentials, NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println("came here aaaa");
        System.out.println(enteredPin);
        System.out.println(newPin);
        if (!Validators.checkPinFormat(newPin)) {
            System.out.println("chck 111");
            throw new IllegalArgumentException("Invalid PIN format.");
        }
        System.out.println("chck 1");

        if (verifyPin(username, enteredPin)) {
            DataAccess.executeUpdate(this.conn, "user", "update",
                    "set mudraPin=?", "username=?", AuthHasher.hashPassword(newPin), username);
            System.out.println("Pin updated successfully!");
        } else {
            throw new InvalidCredentials("Incorrect current password!");
        }
    }
    public boolean verifyPin(String username, String enteredPin)
            throws UserNotFoundException, SQLException, DatabaseException, NoSuchAlgorithmException, InvalidKeySpecException {
        User userDetails = getUser(username);
        return AuthHasher.verifyPassword(enteredPin,userDetails.getPin());
    }
    public boolean verifyPassword(String username, String enteredPassword)
            throws UserNotFoundException, SQLException, DatabaseException, NoSuchAlgorithmException, InvalidKeySpecException {
        User userDetails = getUser(username);
        return AuthHasher.verifyPassword(enteredPassword,userDetails.getHashedPassword());
    }
}
