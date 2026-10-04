package org.sopt;

import org.sopt.controller.PostController;
import org.sopt.view.PostView;

public class Main {
    public static void main(String[] args) {
        PostView view = new PostView();
        PostController controller = new PostController(view);
        controller.run();
    }
}
