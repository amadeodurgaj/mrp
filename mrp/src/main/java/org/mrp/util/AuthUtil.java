package org.mrp.util;

import com.sun.net.httpserver.HttpExchange;
import org.mrp.exception.ApiException;
import org.mrp.model.User;
import org.mrp.service.UserService;

import java.io.IOException;
import java.util.Map;

public class AuthUtil {

    private final UserService userService;
    private final JSONUtil jsonUtil;

    public AuthUtil(UserService userService, JSONUtil jsonUtil) {
        this.userService = userService;
        this.jsonUtil = jsonUtil;
    }

    public User requireUser(HttpExchange exchange) throws IOException, ApiException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            jsonUtil.sendJson(exchange, 401, Map.of("error", "Missing or invalid Authorization header"));
            return null;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        User user = userService.getUserByToken(token);

        if (user == null) {
            jsonUtil.sendJson(exchange, 401, Map.of("error", "Invalid or expired token"));
            return null;
        }

        return user;
    }
}
