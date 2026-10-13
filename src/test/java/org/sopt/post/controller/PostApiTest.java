package org.sopt.post.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostApiTest {
    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void viewCountIncreasesOnlyOnDetailRead() throws Exception {
        String body = """
                {"title":"제목","content":"본문","category":1,"tag":"태그"}
                """;
        HttpResponse<String> created = request("POST", "/api/v1/posts", body);
        assertEquals(201, created.statusCode());
        JsonNode data = mapper.readTree(created.body()).get("data");
        long id = data.get("id").asLong();
        assertTrue(created.headers().firstValue("Location").isEmpty());
        assertEquals("POST_CREATED", mapper.readTree(created.body()).get("code").asString());
        assertEquals(0, data.get("viewCount").asLong());
        assertSuccess(created);
        assertViewCountInList(id, 0);

        for (int count = 1; count <= 2; count++) {
            HttpResponse<String> detail = request("GET", "/api/v1/posts/" + id, null);
            assertEquals(200, detail.statusCode());
            assertEquals(count, mapper.readTree(detail.body()).get("data").get("viewCount").asLong());
        }
        assertViewCountInList(id, 2);
        HttpResponse<String> updated = request("PUT", "/api/v1/posts/" + id, """
                {"title":"수정 제목","content":"수정 본문","category":2,"tag":""}
                """);
        assertEquals(200, updated.statusCode());
        assertEquals(2, mapper.readTree(updated.body()).get("data").get("viewCount").asLong());
        assertEquals("QUESTION", mapper.readTree(updated.body()).get("data").get("category").asString());
        assertViewCountInList(id, 2);

        HttpResponse<String> deleted = request("DELETE", "/api/v1/posts/" + id, null);
        assertEquals(200, deleted.statusCode());
        assertSuccess(deleted);
        assertTrue(mapper.readTree(deleted.body()).get("data").isNull());
        assertFailure(request("GET", "/api/v1/posts/" + id, null), 404);
        assertFailure(request("PUT", "/api/v1/posts/" + id, body), 404);
        assertFailure(request("DELETE", "/api/v1/posts/" + id, null), 404);
    }

    @Test
    void failuresUseCommonResponseAndCorrectStatus() throws Exception {
        assertFailure(request("POST", "/api/v1/posts", """
                {"title":" ","content":"본문","category":1}
                """), 400);
        assertFailure(request("POST", "/api/v1/posts", """
                {"title":"제목","content":"본문","category":99}
                """), 400);
        assertFailure(request("POST", "/api/v1/posts", "{invalid json"), 400);
        assertFailure(request("POST", "/api/v1/posts", null), 400);
        assertFailure(request("GET", "/api/v1/posts/not-a-number", null), 400);
        HttpResponse<String> missing = request("GET", "/api/v1/posts/9223372036854775807", null);
        assertFailure(missing, 404);
        assertEquals("POST_NOT_FOUND", mapper.readTree(missing.body()).get("code").asString());
        HttpResponse<String> invalidJson = request("POST", "/api/v1/posts", "{invalid json");
        assertEquals("INVALID_REQUEST", mapper.readTree(invalidJson.body()).get("code").asString());
        assertFailure(request("PATCH", "/api/v1/posts", "{}"), 405);
        assertFailure(request("GET", "/unknown-path", null), 404);
        HttpRequest unsupportedType = HttpRequest.newBuilder(URI.create(baseUrl() + "/api/v1/posts"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString("text"))
                .build();
        assertFailure(client.send(unsupportedType, HttpResponse.BodyHandlers.ofString()), 415);
    }

    @Test
    void swaggerDocumentsAllPostEndpoints() throws Exception {
        assertEquals(200, request("GET", "/swagger-ui/index.html", null).statusCode());
        HttpResponse<String> docs = request("GET", "/v3/api-docs", null);
        assertEquals(200, docs.statusCode());
        JsonNode document = mapper.readTree(docs.body());
        JsonNode paths = document.get("paths");
        assertTrue(paths.get("/api/v1/posts").has("get"));
        assertEquals("게시글 목록 조회", paths.get("/api/v1/posts").get("get").get("summary").asString());
        assertEquals(3, paths.get("/api/v1/posts").get("get").get("parameters").size());
        assertTrue(paths.get("/api/v1/posts").get("post").get("responses").has("201"));
        assertTrue(paths.get("/api/v1/posts/{id}").has("get"));
        assertTrue(paths.get("/api/v1/posts/{id}").has("put"));
        assertTrue(paths.get("/api/v1/posts/{id}").has("delete"));

    }

    @ParameterizedTest
    @MethodSource("invalidPosts")
    void validationErrorsHaveSpecificCodes(String body, String expectedCode) throws Exception {
        HttpResponse<String> invalidCreate = request("POST", "/api/v1/posts", body);
        assertFailure(invalidCreate, 400);
        assertEquals(expectedCode, mapper.readTree(invalidCreate.body()).get("code").asString());

        HttpResponse<String> created = request("POST", "/api/v1/posts", """
                {"title":"원래 제목","content":"원래 본문","category":1}
                """);
        assertEquals(201, created.statusCode());
        long id = mapper.readTree(created.body()).get("data").get("id").asLong();
        HttpResponse<String> invalidUpdate = request("PUT", "/api/v1/posts/" + id, body);
        assertFailure(invalidUpdate, 400);
        assertEquals(expectedCode, mapper.readTree(invalidUpdate.body()).get("code").asString());
        JsonNode unchanged = mapper.readTree(request("GET", "/api/v1/posts/" + id, null).body()).get("data");
        assertEquals("원래 제목", unchanged.get("title").asString());
        assertEquals("원래 본문", unchanged.get("content").asString());
        assertEquals("FREE", unchanged.get("category").asString());
        request("DELETE", "/api/v1/posts/" + id, null);
    }

    static Stream<Arguments> invalidPosts() {
        return Stream.of(
                Arguments.of("{\"title\": null, \"content\": \"본문\", \"category\": 1}", "POST_TITLE_REQUIRED"),
                Arguments.of("{\"title\": \" \", \"content\": \"본문\", \"category\": 1}", "POST_TITLE_REQUIRED"),
                Arguments.of("{\"title\": \"" + "t".repeat(101) + "\", \"content\": \"본문\", \"category\": 1}", "POST_TITLE_TOO_LONG"),
                Arguments.of("{\"title\": \"제목\", \"content\": null, \"category\": 1}", "POST_CONTENT_REQUIRED"),
                Arguments.of("{\"title\": \"제목\", \"content\": \" \", \"category\": 1}", "POST_CONTENT_REQUIRED"),
                Arguments.of("{\"title\": \"제목\", \"content\": \"" + "b".repeat(5001) + "\", \"category\": 1}", "POST_CONTENT_TOO_LONG"),
                Arguments.of("{\"title\": \"제목\", \"content\": \"본문\", \"category\": null}", "POST_CATEGORY_REQUIRED"),
                Arguments.of("{\"title\": \"제목\", \"content\": \"본문\", \"category\": 0}", "POST_INVALID_CATEGORY"),
                Arguments.of("{\"title\": \"제목\", \"content\": \"본문\", \"category\": 99}", "POST_INVALID_CATEGORY"),
                Arguments.of("{\"title\": \"제목\", \"content\": \"본문\"}", "POST_CATEGORY_REQUIRED")
        );
    }

    @Test
    void maximumTitleAndContentLengthsAreAllowed() throws Exception {
        String body = "{\"title\":\"" + "t".repeat(100)
                + "\",\"content\":\"" + "b".repeat(5000) + "\",\"category\":1}";
        HttpResponse<String> response = request("POST", "/api/v1/posts", body);
        assertEquals(201, response.statusCode());
        long id = mapper.readTree(response.body()).get("data").get("id").asLong();
        request("DELETE", "/api/v1/posts/" + id, null);
    }

    @Test
    void paginationFiltersBeforeSelectingPage() throws Exception {
        List<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < 6; i++) {
                String body = "{\"title\":\"게시글" + i + "\",\"content\":\"본문\",\"category\":"
                        + (i % 2 + 1) + "}";
                HttpResponse<String> created = request("POST", "/api/v1/posts", body);
                assertEquals(201, created.statusCode());
                ids.add(mapper.readTree(created.body()).get("data").get("id").asLong());
            }
            JsonNode first = listPosts("?page=0&size=2");
            assertEquals(2, first.size());
            assertEquals(ids.get(0).longValue(), first.get(0).get("id").asLong());
            JsonNode second = listPosts("?page=1&size=2");
            assertEquals(ids.get(2).longValue(), second.get(0).get("id").asLong());
            JsonNode filtered = listPosts("?category=FREE&page=1&size=2");
            assertEquals(1, filtered.size());
            assertEquals(ids.get(4).longValue(), filtered.get(0).get("id").asLong());
            assertEquals("FREE", filtered.get(0).get("category").asString());
            assertEquals(0, filtered.get(0).get("viewCount").asLong());
            assertEquals(3, listPosts("?category=question&size=10").size());
            assertEquals(0, listPosts("?category=INFO").size());
            assertEquals(0, listPosts("?category=FREE&page=2&size=2").size());
            assertEquals(0, listPosts("?page=2147483647&size=2147483647").size());
        } finally {
            for (long id : ids) {
                request("DELETE", "/api/v1/posts/" + id, null);
            }
        }
    }

    @Test
    void invalidListQueriesUseCommonErrorResponse() throws Exception {
        String[][] cases = {
                {"?page=-1", "INVALID_PAGE"},
                {"?size=0", "INVALID_PAGE_SIZE"},
                {"?size=-1", "INVALID_PAGE_SIZE"},
                {"?category=UNKNOWN", "POST_INVALID_CATEGORY"},
                {"?category=", "POST_INVALID_CATEGORY"},
                {"?page=abc", "INVALID_REQUEST"}
        };
        for (String[] testCase : cases) {
            HttpResponse<String> response = request("GET", "/api/v1/posts" + testCase[0], null);
            assertFailure(response, 400);
            assertEquals(testCase[1], mapper.readTree(response.body()).get("code").asString());
        }
    }

    private JsonNode listPosts(String query) throws Exception {
        HttpResponse<String> response = request("GET", "/api/v1/posts" + query, null);
        assertEquals(200, response.statusCode());
        assertSuccess(response);
        return mapper.readTree(response.body()).get("data");
    }

    @Test
    void frameworkErrorsKeepHeadersAndReturnJson() throws Exception {
        HttpResponse<String> methodError = request("PATCH", "/api/v1/posts", "{}");
        assertFailure(methodError, 405);
        assertTrue(methodError.headers().firstValue("Allow").orElse("").contains("GET"));

        HttpRequest unsupportedResponseType = HttpRequest.newBuilder(URI.create(baseUrl() + "/api/v1/posts"))
                .header("Accept", "text/plain").GET().build();
        HttpResponse<String> response = client.send(unsupportedResponseType, HttpResponse.BodyHandlers.ofString());
        assertFailure(response, 406);
        assertEquals("NOT_ACCEPTABLE", mapper.readTree(response.body()).get("code").asString());
    }

    @ParameterizedTest
    @MethodSource("unconvertibleJsonTypes")
    void unconvertibleJsonTypesAreRejectedOnCreateAndUpdate(String body) throws Exception {
        HttpResponse<String> invalidCreate = request("POST", "/api/v1/posts", body);
        assertFailure(invalidCreate, 400);
        assertEquals("INVALID_REQUEST", mapper.readTree(invalidCreate.body()).get("code").asString());

        HttpResponse<String> created = request("POST", "/api/v1/posts", """
                {"title":"원래 제목","content":"원래 본문","category":1,"tag":"원래 태그"}
                """);
        assertEquals(201, created.statusCode());
        long id = mapper.readTree(created.body()).get("data").get("id").asLong();
        try {
            HttpResponse<String> invalidUpdate = request("PUT", "/api/v1/posts/" + id, body);
            assertFailure(invalidUpdate, 400);
            assertEquals("INVALID_REQUEST", mapper.readTree(invalidUpdate.body()).get("code").asString());
            JsonNode unchanged = mapper.readTree(request("GET", "/api/v1/posts/" + id, null).body()).get("data");
            assertEquals("원래 제목", unchanged.get("title").asString());
            assertEquals("원래 본문", unchanged.get("content").asString());
            assertEquals("원래 태그", unchanged.get("tag").asString());
            assertEquals("FREE", unchanged.get("category").asString());
        } finally {
            request("DELETE", "/api/v1/posts/" + id, null);
        }
    }

    static Stream<String> unconvertibleJsonTypes() {
        return Stream.of(
                "{\"title\": [], \"content\": \"본문\", \"category\": 1}",
                "{\"title\": \"제목\", \"content\": {}, \"category\": 1}"
        );
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl() + path))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void assertSuccess(HttpResponse<String> response) {
        JsonNode body = mapper.readTree(response.body());
        assertTrue(body.get("success").asBoolean());
        assertTrue(body.get("message").isString());
        assertTrue(body.has("data"));
        assertTrue(body.get("code").isString());
    }

    private void assertFailure(HttpResponse<String> response, int status) {
        assertEquals(status, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").startsWith("application/json"));
        JsonNode body = mapper.readTree(response.body());
        assertEquals(4, body.size());
        assertTrue(body.get("code").isString());
        assertFalse(body.get("success").asBoolean());
        assertTrue(body.get("message").isString());
        assertTrue(body.get("data").isNull());
    }

    private void assertViewCountInList(long id, long expected) throws Exception {
        JsonNode posts = mapper.readTree(request("GET", "/api/v1/posts", null).body()).get("data");
        for (JsonNode post : posts) {
            if (post.get("id").asLong() == id) {
                assertEquals(expected, post.get("viewCount").asLong());
                return;
            }
        }
        fail("목록에서 게시글을 찾을 수 없습니다.");
    }
}
