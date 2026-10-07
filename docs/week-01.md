# 1주차 키워드 과제

## 1. final / static / static final

| 구분 | 의미 | 코드 적용 예시 |
| --- | --- | --- |
| final | 초기화 이후 다른 값을 대입할 수 없다. | Post의 ID와 작성일 |
| static | 객체가 아닌 클래스에 속한다. | PostConfig.createClient() |
| static final | 클래스에 속하며 재대입할 수 없다. | 공통 상수 선언에 사용 |

final로 선언한 게시글 ID와 작성일은 수정 시에도 유지된다. 다만 객체를 참조하는 변수에 final을 붙여도 객체 내부까지 불변이 되는 것은 아니다. `final Map`에도 게시글을 추가하거나 삭제할 수 있다. 메서드의 final은 재정의를 막고 클래스의 final은 상속을 막는다.

static 필드는 같은 클래스의 객체들이 공유한다. static 메서드는 객체를 만들지 않고 클래스 이름으로 호출할 수 있다. static 자체가 값 변경을 막는 것은 아니다.

static final은 공통으로 사용하는 고정값을 선언할 때 사용한다. 예를 들어 `static final int EXIT_COMMAND = 6;`처럼 종료 번호를 나타낼 수 있다. 현재 코드에 추가한 것은 아닌 설명용 예시이다.

## 2. 제네릭

제네릭은 클래스나 메서드에서 사용할 자료형을 지정하는 기능이다. `<T>`의 T는 자료형이 들어갈 자리이다.

현재 공통 응답인 `Response<T>`에서 사용한다.

- `Response<PostResponse>`는 게시글 한 개의 정보를 담는다.
- `Response<List<PostResponse>>`는 게시글 목록을 담는다.
- `Response<Void>`는 별도 데이터가 없어 null을 담는다.

같은 응답 클래스를 여러 자료형에 사용할 수 있어 중복을 줄인다. 자료형 오류를 컴파일 시점에 확인할 수 있고 데이터를 꺼낼 때 직접 형 변환할 필요도 없다.

## 3. 기본형과 래퍼 클래스

기본형은 값을 표현하는 자료형이고 래퍼 클래스는 기본형 값을 객체로 다룰 때 사용하는 클래스이다. 대표적으로 `int`와 `Integer` 또는 `long`과 `Long`이 있다.

| 구분 | 기본형 | 래퍼 클래스 |
| --- | --- | --- |
| null 저장 | 불가능 | 가능 |
| 제네릭 자료형으로 사용 | 불가능 | 가능 |
| 사용 상황 | 계산이나 조건 확인 | 컬렉션 사용 또는 값이 없는 상태 표현 |

```java
private long nextId = 1;
private final Map<Long, Post> posts = new HashMap<>();
```

ID를 증가시키는 변수에는 long을 사용한다. Map의 키 자료형에는 기본형을 넣을 수 없어 Long을 사용한다.

기본형을 래퍼 객체로 바꾸는 것은 박싱이고 반대는 언박싱이다. 자바가 자동으로 변환하기도 하지만 null을 기본형으로 변환하면 `NullPointerException`이 발생한다. 래퍼 객체의 값 비교에는 `==` 대신 null 여부를 고려해 `equals()` 등을 사용한다.

## 4. 아키텍처의 역할과 책임

클라이언트와 서버 역할을 나누고 서버 내부는 Controller → Service → Repository로 구성한 레이어드 구조이다. 실제 네트워크 통신 없이 하나의 자바 프로그램에서 동작한다.

| 클래스 | 역할 |
| --- | --- |
| Main / PostConfig | 프로그램을 시작하고 필요한 객체를 만들어 연결한다. |
| PostClient | 메뉴에 따라 입력과 요청 및 결과 출력의 순서를 진행한다. |
| InputView / OutputView | 사용자 입력과 화면 출력을 각각 담당한다. |
| PostController | 요청을 받아 카테고리를 변환하고 Service 호출 결과를 응답으로 만든다. |
| PostService | ID 발급과 게시글 생성·수정 등 작업 순서를 처리한다. |
| PostRepository | HashMap과 ID를 관리하며 저장·조회·삭제를 수행한다. |
| Post / Category | 게시글 정보를 보관하고 카테고리 종류를 제한한다. |
| PostValidator | 게시글 생성과 수정 전에 입력값을 검증한다. |
| CreatePostRequest / UpdatePostRequest | 작성과 수정에 필요한 입력값을 묶어 전달한다. |
| Response<T> / PostResponse | 공통 처리 결과와 게시글 정보를 전달한다. |
| ExceptionHandler / PostNotFoundException | 정해진 예외를 실패 응답으로 바꾸고 없는 게시글 상황을 표현한다. |

게시글 작성 시 클라이언트가 입력값을 CreatePostRequest로 묶어 Controller에 전달한다. Service는 ID를 받아 Post를 만들고 검증을 통과하면 Repository에 저장한다. Controller가 성공 응답을 반환하면 클라이언트가 화면에 출력한다. 검증에 실패하면 예외를 실패 응답으로 바꿔 안내한다.

역할을 나누면 입력 문구는 View에서 바꾸고 저장 방식은 Repository에서 바꿀 수 있다. 다만 파일 수가 늘어 흐름을 여러 곳에서 따라가야 한다는 단점이 있다.
