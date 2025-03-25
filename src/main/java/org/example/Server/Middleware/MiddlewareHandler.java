package org.example.Server.Middleware;
import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import com.sun.security.auth.callback.TextCallbackHandler;
import org.example.utils.Exceptions;
import org.example.utils.Token;
import org.example.utils.Utils;
import org.example.utils.Validators;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class MiddlewareHandler implements HttpHandler {
    private final Gson gson = new Gson();
    public final HttpHandler nextHandler;
    public MiddlewareHandler(HttpHandler nextHandler){
        this.nextHandler=nextHandler;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Middleware : Request intercepted");
        if(Utils.isPublicURI(exchange.getRequestURI())){
            nextHandler.handle(exchange);
            return;
        }
        String token = extractToken(exchange);
        if(token==null || !Validators.checkTokenFormat(token)){
            sendResponse(exchange,401,"Unauthorized","User unauthorized!");
        }
        String jwtToken = token.split(" ")[1];
        String username = getUser(jwtToken);
        if(username==null){
            sendResponse(exchange,401,"Unauthorized","User unauthorized!");
        }
        exchange.setAttribute("username",username);
        nextHandler.handle(exchange);
    }
    public String getUser(String token){
        try{
            System.out.println(token+" is the token which came here");
            return Token.decodeToken(token);
        }
        catch (Exceptions.InvalidTokenException e){
            return null;
        }
    }
    public String extractToken(HttpExchange exchange){
        return exchange.getRequestHeaders().get("Authorization").getFirst();
    }
    private String createJSONResponse(String key, String value){
        return gson.toJson(Map.of(key,value));
    }
    private void sendResponse(HttpExchange exchange, int statusCode, String responseKey,String responseValue) throws IOException{
        String response = createJSONResponse(responseKey,responseValue);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode,response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
