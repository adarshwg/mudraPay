package org.example.Server;

import com.sun.net.httpserver.HttpServer;
import org.example.Server.Handlers.HelloHandler;
import org.example.Server.Handlers.LoginHandler;
import org.example.Server.Middleware.MiddlewareHandler;

public class RouteInitializer {
    public static void initializeRoutes(HttpServer server){
        server.createContext("/login",new MiddlewareHandler(new LoginHandler()));
        server.createContext("/signup",new MiddlewareHandler(new HelloHandler()));
    }
}
