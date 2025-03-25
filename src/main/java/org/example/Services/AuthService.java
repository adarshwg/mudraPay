package org.example.Services;
import org.example.models.User;
import org.example.utils.Validators;
import org.example.utils.Exceptions.*;
import java.sql.Connection;
import java.sql.SQLException;
public class AuthService {
    private final UserService userService;
    private final WalletService walletService;

    public AuthService() {
        this.userService = new UserService(new WalletService(new TransactionService()));
        this.walletService = new WalletService(new TransactionService());
    }

    public boolean signup(User user)
            throws SQLException, UserAlreadyExistsException, DatabaseException {
        String username = user.getUsername();
        String password = user.getHashedPassword();
        String mudraPin = user.getPin();
        if (!Validators.checkCredentialsFormat(username, password, mudraPin)) {
            throw new IllegalArgumentException("Invalid credentials format.");
        }
        try {
            if (userService.checkIfUserExists(username)) {
                throw new UserAlreadyExistsException("User already exists!");
            }
            userService.addUser(username, password, mudraPin);
            return true;
        } catch (SQLException e) {
            throw new DatabaseException("Database error during signup.");
        }
    }

    public boolean login(User user)
            throws UserNotFoundException, SQLException, DatabaseException {
        String username = user.getUsername();
        String enteredPassword = user.getHashedPassword();
        String enteredPin = user.getPin();
        User userDetails  = userService.getUser(username);
        String password = userDetails.getHashedPassword();
        String mudraPin = userDetails.getPin();
        if (!Validators.checkCredentialsFormat(username, password, mudraPin)) {
            throw new IllegalArgumentException("Invalid credentials format.");
        }
        System.out.println(user.getHashedPassword()+" "+password);
        try {
            if (!enteredPassword.equals(password)) {
                throw new IllegalArgumentException("Incorrect password.");
            }
            if (!enteredPin.equals(mudraPin)) {
                throw new IllegalArgumentException("Incorrect Mudra PIN.");
            }
            System.out.println("User logged in successfully: " + username);
            return true;
        }
        catch (Exception e){
            throw e;
        }
    }
}
