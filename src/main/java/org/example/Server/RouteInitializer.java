package org.example.Server;

import com.sun.net.httpserver.HttpServer;
import org.example.Server.Handlers.*;
import org.example.Server.Middleware.MiddlewareHandler;

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
        server.createContext("/password", new MiddlewareHandler(exchange -> {
            String method = exchange.getRequestMethod().toUpperCase();
            switch (method) {
                case "GET":
                    new GetWalletBalanceHandler().handle(exchange);
                    break;
                case "POST":
                    new UpdatePasswordHandler().handle(exchange);
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);  // Method Not Allowed
                    exchange.getResponseBody().close();
            }
        }));
        server.createContext("/pin", new MiddlewareHandler(exchange -> {
            String method = exchange.getRequestMethod().toUpperCase();
            switch (method) {
                case "GET":
                    new VerifyPinHandler().handle(exchange);
                    break;
                case "POST":
                    new UpdatePinHandler().handle(exchange);
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);  // Method Not Allowed
                    exchange.getResponseBody().close();
            }
        }));
        server.createContext("/transactions/recent-contacts", new MiddlewareHandler(new GetRecentContactsHandler()));
        server.createContext("/transactions/contact", new MiddlewareHandler(new GetContactTransactionsHelper()));
        server.createContext("/transactions", new MiddlewareHandler(new GetTransactionsHandler()));
    }
}
