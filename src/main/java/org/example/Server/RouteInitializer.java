package org.example.Server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Server.Handlers.GetWalletBalanceHandler;
import org.example.Server.Handlers.LoginHandler;
import org.example.Server.Handlers.SignupHandler;
import org.example.Server.Handlers.SendMoneyHandler;
import org.example.Server.Middleware.MiddlewareHandler;

import java.io.IOException;

public class RouteInitializer {

    public static void initializeRoutes(HttpServer server) {
        server.createContext("/login", new MiddlewareHandler(new LoginHandler()));
        server.createContext("/signup", new MiddlewareHandler(new SignupHandler()));

        // Unified handler for both GET and POST methods on /wallet
        server.createContext("/wallet", new MiddlewareHandler(exchange -> {
            String method = exchange.getRequestMethod().toUpperCase();
            switch (method) {
                case "GET":
                    new GetWalletBalanceHandler().handle(exchange);
                    break;
                case "POST":
                    new SendMoneyHandler().handle(exchange);
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);  // Method Not Allowed
                    exchange.getResponseBody().close();
            }
        }));
    }
}
