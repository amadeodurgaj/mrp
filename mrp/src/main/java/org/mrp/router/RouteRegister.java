package org.mrp.router;


import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.MediaController;
import org.mrp.controller.UserController;
import org.mrp.service.MediaService;
import org.mrp.service.UserService;
import org.mrp.util.HttpMethodValidatorUtil;
import org.mrp.util.JSONUtil;


public class RouteRegister {

    private final UserRouter userRouter = new UserRouter(
            new UserController(new UserService(), new JSONUtil(), new HttpMethodValidatorUtil())
    );
    private final MediaRouter mediaRouter = new MediaRouter(
            new MediaController(new MediaService(), new JSONUtil(), new HttpMethodValidatorUtil())
    );

    public void registerRoutes(HttpServer server) {
        userRouter.register(server);
        mediaRouter.register(server);
    }
}
