package org.mrp.util;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class HttpMethodValidatorUtil {

    public boolean require(HttpExchange exchange, String expectedMethod) throws IOException {
        if (!expectedMethod.equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return true;
        }
        return false;
    }

    public boolean allow(HttpExchange exchange, String... allowedMethods) throws IOException {
        String method = exchange.getRequestMethod();
        for (String allowed : allowedMethods) {
            if (allowed.equalsIgnoreCase(method)) return true;
        }
        exchange.sendResponseHeaders(405, -1);
        return false;
    }
}
