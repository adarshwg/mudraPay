package org.example.Server.Handlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Server.ServerUtil;
import org.example.Services.AuthService;
import org.example.models.User;
import org.example.utils.Exceptions.*;
import org.example.utils.Token;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    private final AuthService authService = new AuthService();
    private final Gson gson = new Gson();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        User user = getUserFromRequest(exchange);
        try{
            if(user==null||user.getUsername()==null||user.getHashedPassword()==null||user.getPin()==null){
                throw new IllegalArgumentException("Missing required fields");
            }
        }  catch (IllegalArgumentException e){
            ServerUtil.sendResponse(exchange,400,Map.of("BadRequest","Invalid or invalid username or password/pin format!"));
        }
        try {
            boolean isUserAuthenticated = authService.login(user);
            if(!isUserAuthenticated){
                ServerUtil.sendResponse(exchange,401,Map.of("Unauthenticated","Invalid credentials!!"));
                return;
            }
            String token = Token.generateToken(user.getUsername());
            ServerUtil.sendResponse(exchange,201,Map.of("token",token));
        } catch (UserNotFoundException e) {
            ServerUtil.sendResponse(exchange,404,Map.of("NotFound","User not found!"));
        } catch(IllegalArgumentException e){
            ServerUtil.sendResponse(exchange,400,Map.of("BadRequest","Invalid or invalid username or password/pin format!"));
        } catch (SQLException | DatabaseException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            ServerUtil.sendResponse(exchange,500,Map.of("ServerError","Internal Server Error"));
        }
    }
    private User getUserFromRequest(HttpExchange exchange) throws IOException {
        String jsonString  = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return  gson.fromJson(jsonString, User.class);
    }
}
