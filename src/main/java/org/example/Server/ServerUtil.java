package org.example.Server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ServerUtil {
    private static final Gson gson = new Gson();
    private static String createJSONResponse(String key, String value){
        return gson.toJson(Map.of(key,value));
    }
//    public static void sendResponse(HttpExchange exchange, int statusCode, String responseKey, String responseValue) throws IOException {
//        String response = createJSONResponse(responseKey,responseValue);
//        exchange.getResponseHeaders().set("Content-Type", "application/json");
//        exchange.sendResponseHeaders(statusCode,response.getBytes().length);
//        try (OutputStream os = exchange.getResponseBody()) {
//            os.write(response.getBytes());
//        }
//    }
public static void sendResponse(HttpExchange exchange, int statusCode, Map<String, String> responseMap) {
    try (OutputStream os = exchange.getResponseBody()) {
        String jsonResponse = gson.toJson(responseMap);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);
        os.write(jsonResponse.getBytes());
    } catch (IOException e) {
        // Fallback in case of IOException
        try {
            Map<String, String> errorResponse = Map.of("error", "Internal Server Error");
            String errorJson = gson.toJson(errorResponse);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(500, errorJson.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(errorJson.getBytes());
            }
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }
}

}
