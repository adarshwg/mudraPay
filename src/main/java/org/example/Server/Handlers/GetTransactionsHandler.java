package org.example.Server.Handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Server.ServerUtil;
import org.example.Services.TransactionService;
import org.example.models.Transaction;
import org.example.utils.Exceptions.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class GetTransactionsHandler implements HttpHandler {
    private final TransactionService transactionService = new TransactionService();
    private static final int DEFAULT_N = 100;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String username = (String) exchange.getAttribute("username");
        Map<String, String> queryParams = ServerUtil.getQueryParams(exchange);
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        String mode = queryParams.getOrDefault("mode", "0");
        String month = queryParams.getOrDefault("month", String.valueOf(currentMonth));
        String year = queryParams.getOrDefault("year", String.valueOf(currentYear));
        try {
            ArrayList<Transaction> fetchedTransactions = transactionService.getTransactionsByMonth(username, Integer.parseInt(month), Integer.parseInt(year));
            ServerUtil.sendResponse(exchange, 200, Map.of("size", String.valueOf(fetchedTransactions.size()), "transactions", fetchedTransactions.toString()));
        } catch (DatabaseException e) {
            ServerUtil.sendResponse(exchange, 500, Map.of("ServerError", "Internal Server Error"));
        }
    }
}