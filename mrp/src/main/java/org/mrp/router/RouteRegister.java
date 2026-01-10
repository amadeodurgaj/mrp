package org.mrp.router;


import com.sun.net.httpserver.HttpServer;
import org.mrp.controller.MediaController;
import org.mrp.controller.RatingController;
import org.mrp.controller.UserController;
import org.mrp.repository.*;
import org.mrp.service.FavoriteService;
import org.mrp.service.MediaService;
import org.mrp.service.RatingService;
import org.mrp.service.UserService;
import org.mrp.util.AuthUtil;
import org.mrp.util.HttpMethodValidatorUtil;
import org.mrp.util.JSONUtil;


public class RouteRegister {

    UserRepository userRepository = new UserRepositoryImpl();
    UserService userService = new UserService(userRepository);

    RatingRepository ratingRepository = new RatingRepositoryImpl();
    RatingService ratingService = new RatingService(ratingRepository);

    MediaRepository mediaRepository = new MediaRepositoryImpl();
    MediaService mediaService = new MediaService(mediaRepository, ratingRepository);

    FavoriteRepository favoriteRepository = new FavoriteRepositoryImpl();
    FavoriteService favoriteService = new FavoriteService(favoriteRepository, mediaRepository);



    JSONUtil jsonUtil = new JSONUtil();
    HttpMethodValidatorUtil validatorUtil = new HttpMethodValidatorUtil();

    AuthUtil authUtil = new AuthUtil(userService, jsonUtil);

    UserController userController =
            new UserController(userService, ratingService, favoriteService,authUtil, jsonUtil, validatorUtil);

    MediaController mediaController =
            new MediaController(mediaService, favoriteService, authUtil, jsonUtil, validatorUtil);

    RatingController ratingController =
            new RatingController(ratingService, jsonUtil, validatorUtil);


    private final UserRouter userRouter = new UserRouter(userController);
    private final MediaRouter mediaRouter = new MediaRouter(mediaController);
    private final RatingRouter ratingRouter = new RatingRouter(ratingController, authUtil);

    public void registerRoutes(HttpServer server) {
        userRouter.register(server);
        mediaRouter.register(server);
        ratingRouter.register(server);
    }
}
