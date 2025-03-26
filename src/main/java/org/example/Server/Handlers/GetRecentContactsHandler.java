package org.example.Server.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Server.ServerUtil;
import org.example.Services.TransactionService;
import org.example.utils.Exceptions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class GetRecentContactsHandler implements HttpHandler {
    private final TransactionService transactionService = new TransactionService();
    private static final int DEFAULT_N = 10;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String username = (String) exchange.getAttribute("username");
        int n = getRequestedTransactionsSize(exchange);
        ArrayList<String> recentContacts;
        try {
            recentContacts = transactionService.getRecentContacts(username, n);
            ServerUtil.sendResponse(exchange, 200, Map.of("size",String.valueOf(recentContacts.size()),"contacts", String.join(",", recentContacts)));
        } catch (DatabaseException e) {
            ServerUtil.sendResponse(exchange,500,Map.of("ServerError","Internal Server Error"));
        }
    }
    public int getRequestedTransactionsSize(HttpExchange exchange){
        String pathString  = exchange.getRequestURI().getPath();
        System.out.println(pathString);
        String[] parts = pathString.split("/");
        if (parts.length >= 4) {
            try {
                return Integer.parseInt(parts[3]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid path param, using default value.");
            }
        }
        return DEFAULT_N;
    }
}
