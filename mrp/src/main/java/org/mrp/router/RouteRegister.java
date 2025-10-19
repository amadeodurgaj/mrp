package org.mrp.router;


import com.sun.net.httpserver.HttpServer;

public class RouteRegister {

    private final UserRouter userRouter = new UserRouter();

    public void registerRoutes(HttpServer server) {
        userRouter.register(server);
    }
}
