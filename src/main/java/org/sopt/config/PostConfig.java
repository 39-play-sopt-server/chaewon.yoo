package org.sopt.config;

import org.sopt.client.PostClient;
import org.sopt.server.controller.PostController;
import org.sopt.server.exception.ExceptionHandler;
import org.sopt.server.repository.PostRepository;
import org.sopt.server.service.PostService;
import org.sopt.client.view.InputView;
import org.sopt.client.view.OutputView;

public class PostConfig {
    public static PostClient createClient() {
        PostRepository repository = new PostRepository();
        PostService service = new PostService(repository);
        ExceptionHandler exceptionHandler = new ExceptionHandler();
        PostController controller = new PostController(service, exceptionHandler);
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        return new PostClient(controller, inputView, outputView);
    }
}
