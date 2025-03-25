package org.example.Server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.utils.AppConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerInitializer {

    private HttpServer server;
    private ExecutorService executorService;
    private static String JWTSecret ;

    public ServerInitializer(){
        loadEnvVariables();
    }
    public static String getJwtSecret() {
        return JWTSecret;
    }
    public static Algorithm getJWTAlgorithm(){
        loadEnvVariables();
        return Algorithm.HMAC256(getJwtSecret());
    }

    private static void loadEnvVariables() {
        Dotenv dotenv = Dotenv.configure().directory("src/main/resources").load();
        JWTSecret = dotenv.get("JWT_SECRET");
        if (JWTSecret == null || JWTSecret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET not found in environment variables!");
        }
    }

    private InetSocketAddress getServerAddress() {
        int port = ServerConfig.getPort();
        System.out.println("Starting server on port: " + port);
        return new InetSocketAddress(port);
    }

    private void configureExecutor() {
        int threadPoolSize = ServerConfig.getThreadPoolSize();
        try{
            executorService = Executors.newFixedThreadPool(threadPoolSize);
        }catch (IllegalArgumentException e){
            executorService = null;
        }finally {
            server.setExecutor(executorService);
        }
    }

    private void startServer() {
        server.start();
        System.out.println("Server started successfully on port: " + ServerConfig.getPort());
    }

    private void shutdownServer() {
        if (server != null) {
            server.stop(ServerConfig.getGraceTime());
            System.out.println("Server stopped.");
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            System.out.println("Executor service shut down.");
        }
    }

    public void start() throws IOException {
        server = HttpServer.create(getServerAddress(), AppConstants.ServerConstants.BACKLOG);
        RouteInitializer.initializeRoutes(server);
        configureExecutor();

        // Graceful shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownServer));

        startServer();
    }
}
