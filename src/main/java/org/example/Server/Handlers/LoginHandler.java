package org.example.Server.Handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Services.AuthService;
import org.example.models.User;
import org.example.utils.Exceptions;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    private final AuthService authService = new AuthService();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        User user;
        String jsonString  = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Gson gson= new Gson();
        try{
            user = gson.fromJson(jsonString,User.class);
        }
        catch (Exception e){
            System.out.println("Thrown from inside login handler");
            throw e;
        }
        OutputStream os = exchange.getResponseBody();
        try {
            boolean isUserAuthenticated = authService.login(user);
            if(!isUserAuthenticated){
                String response = gson.toJson(Map.of("Unauthenticated","Invalid credentials!!"));
                sendResponse(exchange,401,response);
            }
            exchange.sendResponseHeaders(200, user.getUsername().length());
            os.write(user.getUsername().getBytes());
            os.close();
        } catch (Exceptions.UserNotFoundException | SQLException | Exceptions.DatabaseException e) {
            //todo raise http exceptions
            throw new RuntimeException(e);
        }
    }
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException{
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode,response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
    }
}
