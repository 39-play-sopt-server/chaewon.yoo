package org.sopt.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.sopt.global.response.ApiResponse;
import org.sopt.post.dto.request.CreatePostRequest;
import org.sopt.post.dto.request.LikePostRequest;
import org.sopt.post.dto.request.UpdatePostRequest;
import org.sopt.post.dto.response.PostResponse;
import org.sopt.post.dto.response.PostPageResponse;

@Tag(name = "게시글", description = "게시글 작성, 조회, 수정, 삭제")
public interface PostApi {
    @Operation(summary = "게시글 작성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 작성 완료")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "제목, 본문, 카테고리 등 요청 입력값이 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 작성자 회원")
    ResponseEntity<ApiResponse<PostResponse>> createPost(CreatePostRequest request);

    @Operation(summary = "게시글 목록 조회", description = "작성자 ID와 카테고리를 선택적으로 필터링합니다. 생성일 최신순이며, 생성일이 같으면 ID 내림차순입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "페이지 번호, 페이지 크기, 작성자 ID 또는 카테고리가 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ResponseEntity<ApiResponse<PostPageResponse>> readPosts(
            @Parameter(description = "페이지 번호. 0부터 시작합니다.", example = "0") int page,
            @Parameter(description = "페이지당 게시글 수. 1 이상입니다.", example = "10") int size,
            @Parameter(description = "작성자 회원 ID. 생략하면 모든 작성자를 조회합니다.") Long memberId,
            @Parameter(description = "1: FREE, 2: QUESTION, 3: INFO. 생략하면 전체 카테고리를 조회합니다.") Integer category);

    @Operation(summary = "게시글 제목 검색", description = "제목을 FULLTEXT 전문 검색하고 생성일 최신순으로 조회합니다. 부분 문자열 검색과 결과가 다를 수 있습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "페이지 번호, 페이지 크기 또는 카테고리가 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ResponseEntity<ApiResponse<PostPageResponse>> searchPosts(
            @Parameter(description = "페이지 번호. 0부터 시작합니다.", example = "0") int page,
            @Parameter(description = "페이지당 게시글 수. 1 이상입니다.", example = "10") int size,
            @Parameter(description = "1: FREE, 2: QUESTION, 3: INFO. 생략하면 전체 카테고리를 조회합니다.") Integer category,
            @Parameter(description = "2자 이상 검색어. ngram 전문 검색을 사용합니다. 생략하거나 공백이면 제목 검색을 적용하지 않습니다.") String title);

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

    @Operation(summary = "게시글 좋아요")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 ID가 올바르지 않음")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 게시글")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 좋아요를 눌렀거나 DB 잠금 충돌이 발생함. 잠금 충돌 시 다시 요청해주세요.")
    ResponseEntity<ApiResponse<Void>> likePost(long id, LikePostRequest request);

    @Operation(summary = "게시글 좋아요 취소", description = "좋아요가 없으면 변경 없이 성공 응답을 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 ID가 올바르지 않음")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 게시글")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 잠금 충돌. 잠시 후 다시 요청해주세요.")
    ResponseEntity<ApiResponse<Void>> cancelLike(long id, Long memberId);

    @Operation(summary = "게시글 삭제")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 형식이 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 잠금 충돌. 잠시 후 다시 요청해주세요.")
    ResponseEntity<ApiResponse<Void>> deletePost(long id);
}
