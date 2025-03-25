package org.example.Server;

import com.sun.net.httpserver.HttpServer;
import org.example.Server.Handlers.GetWalletBalanceHandler;
import org.example.Server.Handlers.HelloHandler;
import org.example.Server.Handlers.LoginHandler;
import org.example.Server.Handlers.SignupHandler;
import org.example.Server.Middleware.MiddlewareHandler;

public class RouteInitializer {
    public static void initializeRoutes(HttpServer server){
        server.createContext("/login",new MiddlewareHandler(new LoginHandler()));
        server.createContext("/signup",new MiddlewareHandler(new SignupHandler()));
        server.createContext("/wallet",new MiddlewareHandler(new GetWalletBalanceHandler()));
    }
}
