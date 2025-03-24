package org.example.Server.Middleware;
import com.sun.net.httpserver.*;
import org.example.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;

public class MiddlewareHandler implements HttpHandler {
    public final HttpHandler nextHandler;
    public MiddlewareHandler(HttpHandler nextHandler){
        this.nextHandler=nextHandler;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Middleware : Request intercepted");
        String token = exchange.getRequestHeaders().getFirst("Authorization");
        if(Utils.isPublicURI(exchange.getRequestURI())){
            nextHandler.handle(exchange);
            return;
        }
        if(token==null || !token.equals("Bearer my-token")){
            String response = "Unauthorized";
            exchange.sendResponseHeaders(401, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }
        nextHandler.handle(exchange);
    }
}
