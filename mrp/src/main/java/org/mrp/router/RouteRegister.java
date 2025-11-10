package org.mrp.router;


import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.MediaController;
import org.mrp.service.MediaService;
import org.mrp.util.HttpMethodValidatorUtil;
import org.mrp.util.JSONUtil;


public class RouteRegister {

    private final UserRouter userRouter = new UserRouter();
    private final MediaRouter mediaRouter = new MediaRouter(
            new MediaController(new MediaService(), new JSONUtil(), new HttpMethodValidatorUtil())
    );

    public void registerRoutes(HttpServer server) {
        userRouter.register(server);
        mediaRouter.register(server);
    }
}
