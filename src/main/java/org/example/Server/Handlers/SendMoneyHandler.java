package org.example.Server.Handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Server.ServerUtil;
import org.example.Services.TransactionService;
import org.example.Services.UserService;
import org.example.Services.WalletService;
import org.example.models.PaymentModel;
import org.example.models.Transaction;
import org.example.models.User;
import org.example.utils.Exceptions.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class SendMoneyHandler implements HttpHandler {
    private final Gson gson = new Gson();
    private final TransactionService transactionService = new TransactionService();
    private final WalletService walletService  = new WalletService(transactionService);
    private final UserService userService= new UserService(walletService);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String username = (String) exchange.getAttribute("username");
        PaymentModel paymentDetails = getPaymentDetails(exchange);
        String receiverName = paymentDetails.receiver();
        int amount = paymentDetails.amount();
        try {
            User user = userService.getUser(username);
            User receiver = userService.getUser(receiverName);
            int remainingBalance = walletService.getBalance(user);
            Transaction newTransaction = walletService.sendMoney(user,receiver,amount);
            ServerUtil.sendResponse(exchange,201, Map.of(
                    "TransactionID", newTransaction.transactionId(),
                    "Sender", newTransaction.sender(),
                    "Receiver", newTransaction.receiver(),
                    "Amount", String.valueOf(newTransaction.amount()),
                    "Remaining Balance",String.valueOf(remainingBalance)
            ));
        } catch (SQLException | DatabaseException e) {;
            ServerUtil.sendResponse(exchange,500,Map.of("ServerError","Internal Server Error "+ e.getMessage()));
        } catch (UserNotFoundException e) {
            ServerUtil.sendResponse(exchange,404,Map.of("NotFound","User not found!"));
        } catch (LowBalanceException e) {
            ServerUtil.sendResponse(exchange,402,Map.of("PaymentFailed","Payment failed due to insufficient balance!!"));
        } catch (InvalidAmountException e) {
            ServerUtil.sendResponse(exchange,400,Map.of("BadRequest","Invalid amount entered!!"));
        }
    }
    public PaymentModel getPaymentDetails(HttpExchange exchange) throws IOException {
        String jsonString  = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return  gson.fromJson(jsonString, PaymentModel.class);
    }

}

