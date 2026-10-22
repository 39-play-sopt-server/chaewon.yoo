package org.sopt.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.sopt.global.response.ApiResponse;
import org.sopt.post.dto.request.CreatePostRequest;
import org.sopt.post.dto.request.UpdatePostRequest;
import org.sopt.post.dto.response.PostResponse;
import org.sopt.post.dto.response.PostPageResponse;

@Tag(name = "게시글", description = "게시글 작성, 조회, 수정, 삭제")
public interface PostApi {
    @Operation(summary = "게시글 작성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 작성 완료")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "제목, 본문, 카테고리 등 요청 입력값이 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ResponseEntity<ApiResponse<PostResponse>> createPost(CreatePostRequest request);

    @Operation(summary = "게시글 목록 조회", description = "카테고리 필터를 적용한 뒤 저장 순서대로 페이지를 나눕니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "페이지 번호, 페이지 크기 또는 카테고리가 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ResponseEntity<ApiResponse<PostPageResponse>> readPosts(
            @Parameter(description = "페이지 번호. 0부터 시작합니다.", example = "0") int page,
            @Parameter(description = "페이지당 게시글 수. 1 이상입니다.", example = "10") int size,
            @Parameter(description = "1: FREE, 2: QUESTION, 3: INFO. 생략하면 전체 카테고리를 조회합니다.") Integer category);

    @Operation(summary = "게시글 상세 조회", description = "상세 조회 시 조회수가 1 증가합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 형식이 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ResponseEntity<ApiResponse<PostResponse>> readPost(long id);

    @Operation(summary = "게시글 수정", description = "제목, 본문, 카테고리, 태그를 교체합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "제목, 본문, 카테고리 등 요청 입력값이 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ResponseEntity<ApiResponse<PostResponse>> updatePost(long id, UpdatePostRequest request);

    @Operation(summary = "게시글 삭제")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 형식이 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ResponseEntity<ApiResponse<Void>> deletePost(long id);
}
