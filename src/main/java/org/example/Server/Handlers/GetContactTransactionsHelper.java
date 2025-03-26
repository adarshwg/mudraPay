package org.example.Server.Handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Server.ServerUtil;
import org.example.Services.TransactionService;
import org.example.models.Transaction;
import org.example.utils.Exceptions.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class GetContactTransactionsHelper implements HttpHandler {
    private final TransactionService transactionService = new TransactionService();
    private static final int DEFAULT_N = 100;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String username = (String) exchange.getAttribute("username");
        String receiver = "";
        int n = getRequestedTransactionsSize(exchange);
        ArrayList<Transaction> contactTransactions;
        try {
            receiver = getContactName(exchange);
            contactTransactions = transactionService.getContactTransactions(username, receiver, n);
            ServerUtil.sendResponse(exchange, 200, Map.of("size",String.valueOf(contactTransactions.size()),"transactions",  contactTransactions.toString()));
        } catch (DatabaseException | SQLException e) {
            ServerUtil.sendResponse(exchange,500,Map.of("ServerError","Internal Server Error"));
        } catch (UserNotFoundException e) {
            ServerUtil.sendResponse(exchange,404,Map.of("UserNotFound","No user with the username "+receiver));
        }
    }
    public String getContactName(HttpExchange exchange) throws UserNotFoundException {
        String pathString  = exchange.getRequestURI().getPath();
        System.out.println(pathString);
        String[] parts = pathString.split("/");
        if (parts.length >= 4) {
            return parts[3];
        }
        else throw new UserNotFoundException("user not found!!");
    }
    public int getRequestedTransactionsSize(HttpExchange exchange){
        String pathString  = exchange.getRequestURI().getPath();
        System.out.println(pathString);
        String[] parts = pathString.split("/");
        if (parts.length >= 5) {
            try {
                return Integer.parseInt(parts[4]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid path param, using default value.");
            }
        }
        return DEFAULT_N;
    }
}