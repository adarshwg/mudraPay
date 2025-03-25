package org.example.Server.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Services.AuthService;
import org.example.models.User;
import org.example.utils.Exceptions.*;
import org.example.utils.Token;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class SignupHandler implements HttpHandler {
    private final AuthService authService = new AuthService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        User user = getUserFromRequest(exchange);
        try{
            if(user ==null|| user.getUsername()==null|| user.getHashedPassword()==null|| user.getPin()==null){
                throw new IllegalArgumentException("Missing required fields");
            }
        } catch (JsonSyntaxException e) {
            ResponseUtil.sendResponse(exchange, 400, "BadRequest","Malformed JSON!");
        } catch (IllegalArgumentException e){
            ResponseUtil.sendResponse(exchange,400,"BadRequest","Invalid or invalid username or password/pin format!");
        }
        try {
            boolean isUserAuthenticated = authService.signup(user);
            if(!isUserAuthenticated){
                ResponseUtil.sendResponse(exchange,401,"Unauthenticated","Invalid credentials!!");
                return;
            }
            String token = Token.generateToken(user.getUsername());
            ResponseUtil.sendResponse(exchange,201,"token",token);
        }catch(IllegalArgumentException e){
            ResponseUtil.sendResponse(exchange,400,"BadRequest","Invalid or invalid username or password/pin format!");
        } catch (SQLException | DatabaseException e) {
            ResponseUtil.sendResponse(exchange,500,"ServerError","Internal Server Error");
        } catch (UserAlreadyExistsException  e) {
            ResponseUtil.sendResponse(exchange,409,"Conflict","User already exists!!");
        }
    }
    private User getUserFromRequest(HttpExchange exchange) throws IOException {
        String jsonString  = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return  gson.fromJson(jsonString, User.class);
    }
}
