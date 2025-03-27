package org.example.Server;

import com.auth0.jwt.algorithms.Algorithm;
import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.Services.DatabaseInitializer;
import org.example.utils.AppConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerInitializer {

    private HttpServer server;
    private ExecutorService executorService;
    private static String JWTSecret;

    // ✅ Updated constructor with backward compatibility
    public ServerInitializer() {
        loadEnvVariables();
    }

    public static String getJwtSecret() {
        return JWTSecret;
    }

    public static Algorithm getJWTAlgorithm() {
        loadEnvVariables();
        return Algorithm.HMAC256(getJwtSecret());
    }

    /**
     * ✅ Environment-agnostic .env loading with backward compatibility.
     */
    private static void loadEnvVariables() {
        Dotenv dotenv;

        try {
            if (Files.exists(Paths.get("/app/.env"))) {
                // Docker environment
                dotenv = Dotenv.configure()
                        .directory("/app")
                        .load();
                System.out.println("✅ Loaded .env from Docker environment.");
            } else {
                // Local environment
                dotenv = Dotenv.configure()
                        .directory("src/main/resources")
                        .load();
                System.out.println("✅ Loaded .env from local environment.");
            }

            // Load the JWT secret
            JWTSecret = dotenv.get("JWT_SECRET");

            // Backward compatibility: Use default value if JWT_SECRET is missing
            if (JWTSecret == null || JWTSecret.isEmpty()) {
                System.out.println("⚠️ JWT_SECRET not found. Using default secret for backward compatibility.");
                JWTSecret = "default-secret";  // Fallback value
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load .env file: " + e.getMessage());
        }
    }

    private InetSocketAddress getServerAddress() {
        int port = ServerConfig.getPort();
        System.out.println("Starting server on port: " + port);
        return new InetSocketAddress(port);
    }

    private void configureExecutor() {
        int threadPoolSize = ServerConfig.getThreadPoolSize();
        try {
            executorService = Executors.newFixedThreadPool(threadPoolSize);
        } catch (IllegalArgumentException e) {
            executorService = null;
        } finally {
            server.setExecutor(executorService);
        }
    }

    private void startServer() {
        server.start();
        System.out.println("✅ Server started successfully on port: " + ServerConfig.getPort());
    }

    private void shutdownServer() {
        if (server != null) {
            server.stop(ServerConfig.getGraceTime());
            System.out.println("🔴 Server stopped.");
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            System.out.println("🔴 Executor service shut down.");
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
