![banner_server.png](banner_server.png)

# 게시글 API

Spring Boot 4와 Java로 만든 게시판 API입니다. 패키지는 게시글 기능인 `post`와 공통 기능인 `global`으로 나눕니다.

## 실행과 검증

```bash
./gradlew bootRun
./gradlew build
```

- Swagger: http://localhost:8080/swagger-ui/index.html
- API 기본 경로: `/api/v1/posts`
- 저장소는 메모리에서 동작합니다. 서버를 재시작하면 게시글이 초기화됩니다.

## 파일 구조

```text
src/main/java/org/sopt/
├── Main.java
├── global/
│   ├── code/
│   │   ├── ErrorCode.java
│   │   ├── SuccessCode.java
│   │   └── GlobalErrorCode.java
│   ├── response/ApiResponse.java
│   └── exception/
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
└── post/
    ├── code/
    │   ├── PostErrorCode.java
    │   └── PostSuccessCode.java
    ├── controller/
    │   ├── PostController.java
    │   └── PostApi.java
    ├── dto/
    │   ├── request/
    │   │   ├── CreatePostRequest.java
    │   │   └── UpdatePostRequest.java
    │   └── response/PostResponse.java
    ├── service/PostService.java
    ├── domain/
    │   ├── Post.java
    │   ├── PostCategory.java
    │   ├── PostLimits.java
    │   └── PostValidator.java
    ├── repository/PostRepository.java
    └── exception/PostException.java

src/main/resources/
└── application.properties

src/test/java/org/sopt/post/
├── controller/PostApiTest.java
└── service/PostConcurrencyTest.java

docs/
├── week-01.md
└── week-02.md
```

## 역할과 요청 흐름

| 위치 | 역할 |
| --- | --- |
| Main | Spring 애플리케이션 실행 |
| post/controller | HTTP 요청을 받아 Service를 호출하고 응답 생성 |
| post/controller의 PostApi | Swagger의 API 설명과 입력값 설명 |
| post/dto | 클라이언트의 요청과 응답 데이터 |
| post/service | 게시글 작업의 순서를 조율하고 동시성 처리 |
| post/domain | 게시글 상태와 입력 규칙 |
| post/repository | 메모리 저장소와 ID 관리 |
| global/code | 오류·성공 코드의 공통 규칙과 요청 처리 오류 코드 |
| global/response | 공통 응답 형식 |
| post/code | 게시글 오류 코드와 성공 코드 |
| global/exception | 오류 코드를 담는 예외와 HTTP 예외 처리 |
| post/exception | 게시글 없음과 게시글 입력 검증 예외 |

요청은 Controller → Service → Repository 순서로 전달됩니다. Service는 공유 락 안에서 게시글을 처리하고 `PostResponse`를 만들어 반환합니다. Controller는 반환된 데이터를 `ApiResponse`에 담습니다.

게시글이 없거나 입력 검증에 실패하면 `PostException`이 발생합니다. `PostException`은 공통 `BusinessException`을 상속하고 상황에 맞는 `PostErrorCode`를 전달합니다. `GlobalExceptionHandler`는 예외에 담긴 `ErrorCode`를 사용해 HTTP 상태와 실패 응답을 만듭니다. JSON 문법 등 Spring 요청 오류도 공통 형식으로 처리합니다.

## API

| 메서드 | 경로 | 기능 |
| --- | --- | --- |
| POST | /api/v1/posts | 게시글 생성. 성공 시 201 |
| GET | /api/v1/posts | 목록 조회와 카테고리 필터, 페이지네이션 |
| GET | /api/v1/posts/{id} | 상세 조회와 조회수 증가 |
| PUT | /api/v1/posts/{id} | 제목·본문·카테고리·태그 교체 |
| DELETE | /api/v1/posts/{id} | 게시글 삭제 |

목록 조회의 기본값은 `page=0`, `size=10`입니다. `category`는 `FREE`, `QUESTION`, `INFO`를 받으며 생략하면 전체 카테고리를 조회합니다. 카테고리 필터를 먼저 적용하고 저장 순서대로 페이지를 나눕니다.

```text
GET /api/v1/posts?category=FREE&page=0&size=5
```

작성과 수정의 요청 예시는 다음과 같습니다. 카테고리 번호는 1=FREE, 2=QUESTION, 3=INFO입니다.

```json
{
  "title": "제목",
  "content": "본문",
  "category": 1,
  "tag": "태그"
}
```

제목은 100자, 본문은 5000자까지 입력할 수 있습니다. 제한값은 PostLimits에서 관리합니다. 태그는 선택 입력입니다. 제목·본문·태그는 문자열로 보내야 하며 카테고리는 정수로 보내야 합니다. 성공과 실패 모두 `success`, `code`, `message`, `data` 필드를 반환합니다.

## 테스트

`PostApiTest`는 CRUD, 조회수, 입력 검증, 공통 응답, 페이지네이션, 카테고리 필터, Swagger 문서를 확인합니다.

`PostConcurrencyTest`는 동시 게시글 생성, 상세 조회수 증가, 수정과 조회가 함께 실행될 때 결과 필드의 일관성을 확인합니다.
