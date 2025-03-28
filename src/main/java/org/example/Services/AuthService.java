package org.example.Services;
import org.example.models.User;
import org.example.utils.AuthHasher;
import org.example.utils.Validators;
import org.example.utils.Exceptions.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
        String enteredPassword = user.getHashedPassword();
        String enteredMudraPin = user.getPin();
        System.out.println(Validators.checkCredentialsFormat(username,enteredPassword,enteredMudraPin)+" credentials format");
        if (!Validators.checkCredentialsFormat(username, enteredPassword, enteredMudraPin)) {
            throw new IllegalArgumentException("Invalid credentials format.");
        }
        try {
            if (userService.checkIfUserExists(username)) {
                throw new UserAlreadyExistsException("User already exists!");
            }
            enteredPassword = AuthHasher.hashPassword(enteredPassword);
            enteredMudraPin = AuthHasher.hashPassword(enteredMudraPin);
            System.out.println("had come to add the user!!!!");
            userService.addUser(username, enteredPassword, enteredMudraPin);
            System.out.println("post adding user!!");
            return true;
        } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new DatabaseException("Internal Server Error.");
        }
    }

    public boolean login(User user)
            throws UserNotFoundException, SQLException, DatabaseException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = user.getUsername();
        String enteredPassword = user.getHashedPassword();
        String enteredPin = user.getPin();
        User userDetails  = userService.getUser(username);
        String password = userDetails.getHashedPassword();
        System.out.println("password is "+password+" entered password is "+enteredPassword+" "+ AuthHasher.verifyPassword(enteredPassword,password));
        String mudraPin = userDetails.getPin();
        if (!Validators.checkCredentialsFormat(username, enteredPassword, enteredPin)) {
            System.out.println("here in credentials check");
            throw new IllegalArgumentException("Invalid credentials format.");
        }
        System.out.println(user.getHashedPassword()+" "+password);
        try {
            if (!AuthHasher.verifyPassword(enteredPassword,password)) {
                throw new IllegalArgumentException("Incorrect password.");
            }
            if (!AuthHasher.verifyPassword(enteredPin,mudraPin)) {
                System.out.println(enteredPin+" "+mudraPin);
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
