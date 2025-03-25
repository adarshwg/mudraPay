package org.example.Server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Server.ServerUtil;
import org.example.Services.TransactionService;
import org.example.Services.UserService;
import org.example.Services.WalletService;
import org.example.models.UpdatePasswordModel;
import org.example.utils.Exceptions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class UpdatePasswordHandler implements HttpHandler {
    private final Gson gson = new Gson();
    private final TransactionService transactionService = new TransactionService();
    private final WalletService walletService  = new WalletService(transactionService);
    private final UserService userService= new UserService(walletService);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String username = (String) exchange.getAttribute("username");
        try {
            UpdatePasswordModel passwordDetails = passwordDetails(exchange);
            userService.updatePassword(username, passwordDetails.enteredPassword(), passwordDetails.newPassword());
            ServerUtil.sendResponse(exchange,201, Map.of("PasswordChangeSuccess","Password Changed Successfully!!"));
        } catch (UserNotFoundException e) {
            ServerUtil.sendResponse(exchange,404,Map.of("NotFound","User not found!"));
        } catch (SQLException | DatabaseException e) {
            ServerUtil.sendResponse(exchange,500,Map.of("ServerError","Internal Server Error "+ e.getMessage()));
        } catch (InvalidCredentials e) {
            ServerUtil.sendResponse(exchange,401,Map.of("Unauthorized","Incorrect current password!!"));
        }
    }
    private UpdatePasswordModel passwordDetails(HttpExchange exchange) throws IOException {
        String jsonString  = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return  gson.fromJson(jsonString, UpdatePasswordModel.class);
    }
}
