package org.example.Server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Services.TransactionService;
import org.example.Services.UserService;
import org.example.Services.WalletService;
import org.example.models.User;
import org.example.utils.Exceptions.*;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;

public class GetWalletBalanceHandler implements HttpHandler {
    private final TransactionService transactionService = new TransactionService();
    private final WalletService walletService  = new WalletService(transactionService);
    private final UserService userService= new UserService(walletService);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
       String username = (String) exchange.getAttribute("username");
       User user;
        try {
            user = userService.getUser(username);
            int walletBalance = walletService.getBalance(user);
            ResponseUtil.sendResponse(exchange,201,"walletBalance", String.valueOf(walletBalance));
        } catch (SQLException | DatabaseException e) {
            ResponseUtil.sendResponse(exchange,500,"ServerError","Internal Server Error");
        } catch (UserNotFoundException e) {
            ResponseUtil.sendResponse(exchange,404,"NotFound","User not found!");
        }
    }
}
