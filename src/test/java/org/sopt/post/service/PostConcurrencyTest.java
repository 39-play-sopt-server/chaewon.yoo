package org.sopt.post.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.sopt.post.dto.response.PostResponse;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.repository.PostRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostConcurrencyTest {
    @Autowired
    private PostService service;
    @Autowired
    private PostRepository repository;

    @BeforeEach
    void clearPosts() {
        repository.deleteAll();
    }

    @Test
    void concurrentCreationDoesNotLosePostsOrDuplicateIds() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        int count = 300;
        try {
            List<Future<PostResponse>> futures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String title = "게시글" + i;
                futures.add(executor.submit(() -> {
                    start.await();
                    return service.createPost(title, "본문", PostCategory.FREE, "");
                }));
            }
            start.countDown();
            Set<Long> ids = new HashSet<>();
            for (Future<PostResponse> future : futures) {
                assertTrue(ids.add(future.get(10, TimeUnit.SECONDS).id()));
            }
            List<PostResponse> posts = service.readPosts(0, count, null);
            assertEquals(count, posts.size());
            for (int i = 0; i < count; i++) {
                assertTrue(ids.contains(posts.get(i).id()));
                if (i > 0) {
                    assertTrue(posts.get(i - 1).id() < posts.get(i).id());
                }
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void concurrentDetailReadsKeepEveryIncrementAndReturnOwnSnapshot() throws Exception {
        PostResponse created = service.createPost("제목", "본문", PostCategory.FREE, "");
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        int count = 300;
        try {
            List<Future<PostResponse>> futures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    return service.readPost(created.id());
                }));
            }
            start.countDown();
            Set<Long> viewCounts = new HashSet<>();
            for (Future<PostResponse> future : futures) {
                assertTrue(viewCounts.add(future.get(10, TimeUnit.SECONDS).viewCount()));
            }
            assertEquals(count, service.readPosts(0, 10, null).get(0).viewCount());
            assertTrue(viewCounts.contains(1L));
            assertTrue(viewCounts.contains((long) count));
            assertEquals(0, created.viewCount());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void concurrentUpdatesAndReadsDoNotMixFieldsFromDifferentVersions() throws Exception {
        long id = service.createPost("0", "0", PostCategory.FREE, "0").id();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<PostResponse>> futures = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                int version = i;
                futures.add(executor.submit(() -> {
                    start.await();
                    if (version % 2 == 0) {
                        String value = Integer.toString(version);
                        return service.updatePost(id, value, value, PostCategory.FREE, value);
                    }
                    return service.readPost(id);
                }));
            }
            start.countDown();
            for (Future<PostResponse> future : futures) {
                PostResponse result = future.get(10, TimeUnit.SECONDS);
                assertEquals(result.title(), result.content());
                assertEquals(result.title(), result.tag());
            }
            assertEquals(250, service.readPosts(0, 10, null).get(0).viewCount());
        } finally {
            executor.shutdownNow();
        }
    }
}
