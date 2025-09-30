package org.mrp.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class JSONUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T fromJson(InputStream input, Class<T> clazz) throws IOException {
        return mapper.readValue(input, clazz);
    }

    public static void sendJson(HttpExchange exchange, int status, Object response) throws IOException {
        String json = mapper.writeValueAsString(response);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
