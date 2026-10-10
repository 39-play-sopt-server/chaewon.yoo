package org.sopt.client;

import org.sopt.server.controller.PostController;
import org.sopt.server.dto.response.PostResponse;
import org.sopt.server.dto.request.CreatePostRequest;
import org.sopt.server.dto.request.UpdatePostRequest;
import org.sopt.common.response.Response;
import org.sopt.client.view.InputView;
import org.sopt.client.view.OutputView;
import java.util.List;
import java.util.NoSuchElementException;

public class PostClient {
    private final PostController controller;
    private final InputView inputView;
    private final OutputView outputView;

    public PostClient(PostController controller, InputView inputView, OutputView outputView) {
        this.controller = controller;
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void run() {
        while (true) {
            try {
                outputView.printMenu();
                int command = inputView.readCommand();
                switch (command) {
                    case 1:
                        createPostMenu();
                        break;
                    case 2:
                        readPostsMenu();
                        break;
                    case 3:
                        readPostMenu();
                        break;
                    case 4:
                        updatePostMenu();
                        break;
                    case 5:
                        deletePostMenu();
                        break;
                    case 6:
                        outputView.printMessage("프로그램을 종료합니다.");
                        return;
                    default:
                        outputView.printMessage("잘못된 입력입니다.");
                }
            } catch (NumberFormatException e) {
                outputView.printMessage("숫자를 입력해주세요.");

            } catch (IllegalArgumentException e) {
                outputView.printMessage(e.getMessage());

            } catch (NoSuchElementException e) {
                outputView.printMessage("입력이 종료되었습니다.");
                return;
            }
        }
    }

    private void createPostMenu() {
        String title = inputView.readTitle();
        String content = inputView.readContent();
        int category = inputView.readCategory();
        String tag = inputView.readTag();
        CreatePostRequest request = new CreatePostRequest(title, content, category, tag);
        Response<PostResponse> response = controller.createPost(request);
        outputView.printResponse(response);
    }

    private void readPostsMenu() {
        Response<List<PostResponse>> response = controller.readPosts();
        outputView.printResponse(response);
        if (response.isSuccess()) {
            outputView.printPosts(response.getData());
        }
    }

    private void readPostMenu() {
        long id = inputView.readPostId("조회할 게시글 ID: ");
        Response<PostResponse> response = controller.readPost(id);
        outputView.printResponse(response);
        if (response.isSuccess()) {
            outputView.printPost(response.getData());
        }
    }

    private void updatePostMenu() {
        long id = inputView.readPostId("수정할 게시글 ID: ");
        Response<PostResponse> found = controller.readPost(id);
        if (!found.isSuccess()) {
            outputView.printResponse(found);
            return;
        }
        String title = inputView.readTitle();
        String content = inputView.readContent();
        int category = inputView.readCategory();
        String tag = inputView.readTag();
        UpdatePostRequest request = new UpdatePostRequest(title, content, category, tag);
        Response<PostResponse> response = controller.updatePost(id, request);
        outputView.printResponse(response);
    }

    private void deletePostMenu() {
        long id = inputView.readPostId("삭제할 게시글 ID: ");
        outputView.printResponse(controller.deletePost(id));
    }

}
