package org.example.Server.Handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Server.ServerUtil;
import org.example.Services.TransactionService;
import org.example.Services.UserService;
import org.example.Services.WalletService;
import org.example.models.PinModel;
import org.example.models.User;
import org.example.utils.Exceptions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class VerifyPinHandler implements HttpHandler {
    private final Gson gson = new Gson();
    private final TransactionService transactionService = new TransactionService();
    private final WalletService walletService  = new WalletService(transactionService);
    private final UserService userService= new UserService(walletService);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String username = (String) exchange.getAttribute("username");
        User user;
        PinModel enteredPin = pinDetails(exchange);
        try{
            user = userService.getUser(username);
            String pinVerification="false";
            if(enteredPin.enteredPin().equals(user.getHashedPassword())){
                pinVerification="true";
            }else {
                pinVerification="false";
            }
            ServerUtil.sendResponse(exchange,200, Map.of("PinVerification",pinVerification));
        } catch (Exceptions.UserNotFoundException e) {
            ServerUtil.sendResponse(exchange,404,Map.of("NotFound","User not found!"));
        } catch (SQLException | Exceptions.DatabaseException e) {
            ServerUtil.sendResponse(exchange,500,Map.of("ServerError","Internal Server Error"));
        }

    }
    private PinModel pinDetails(HttpExchange exchange) throws IOException {
        String jsonString  = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return  gson.fromJson(jsonString, PinModel.class);
    }
}
