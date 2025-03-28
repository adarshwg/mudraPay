package org.example.Server.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Server.ServerUtil;
import org.example.Services.TransactionService;
import org.example.Services.UserService;
import org.example.Services.WalletService;
import org.example.models.User;
import org.example.utils.Exceptions.*;
import java.io.IOException;
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
            ServerUtil.sendResponse(exchange,201,Map.of("walletBalance", String.valueOf(walletBalance)));
        } catch (SQLException | DatabaseException e) {
            ServerUtil.sendResponse(exchange,500,Map.of("ServerError","Internal Server Error"));
        } catch (UserNotFoundException e) {
            ServerUtil.sendResponse(exchange,404,Map.of("NotFound","User not found!"));
        }
    }
}
