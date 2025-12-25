package org.mrp.router;


import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.MediaController;
import org.mrp.controller.UserController;
import org.mrp.repository.MediaRepository;
import org.mrp.repository.MediaRepositoryImpl;
import org.mrp.repository.UserRepository;
import org.mrp.repository.UserRepositoryImpl;
import org.mrp.service.MediaService;
import org.mrp.service.UserService;
import org.mrp.util.AuthUtil;
import org.mrp.util.HttpMethodValidatorUtil;
import org.mrp.util.JSONUtil;


public class RouteRegister {

    UserRepository userRepository = new UserRepositoryImpl();
    UserService userService = new UserService(userRepository);

    MediaRepository mediaRepository = new MediaRepositoryImpl();
    MediaService mediaService = new MediaService(mediaRepository);

    JSONUtil jsonUtil = new JSONUtil();
    HttpMethodValidatorUtil validatorUtil = new HttpMethodValidatorUtil();

    AuthUtil authUtil = new AuthUtil(userService, jsonUtil);

    UserController userController =
            new UserController(userService, authUtil, jsonUtil, validatorUtil);

    MediaController mediaController =
            new MediaController(mediaService, authUtil, jsonUtil, validatorUtil);


    private final UserRouter userRouter = new UserRouter(userController);
    private final MediaRouter mediaRouter = new MediaRouter(mediaController);

    public void registerRoutes(HttpServer server) {
        userRouter.register(server);
        mediaRouter.register(server);
    }
}
