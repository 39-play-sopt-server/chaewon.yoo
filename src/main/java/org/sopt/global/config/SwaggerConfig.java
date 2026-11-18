package org.sopt.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@OpenAPIDefinition(info = @Info(title = "게시판 API", version = "1.0",
        description = "로그인·재발급 전 GET /auth/csrf를 호출하고 응답의 token을 X-XSRF-TOKEN 헤더에 넣어주세요. "
                + "브라우저는 쿠키를 유지해야 합니다. 로그인 후 Authorize에 Access Token 값만 입력해주세요. "
                + "Refresh Token은 HttpOnly 쿠키로 전달됩니다. 구글 로그인은 브라우저에서 /oauth2/authorization/google로 접속합니다."))
public class SwaggerConfig {}
