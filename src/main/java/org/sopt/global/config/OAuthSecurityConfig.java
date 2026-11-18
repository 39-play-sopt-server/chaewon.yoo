package org.sopt.global.config;

import org.sopt.auth.oauth.OAuthLoginHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
public class OAuthSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain oauthFilterChain(HttpSecurity http, OAuthLoginHandler handler) throws Exception {
        return http
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .securityContext(context -> context.securityContextRepository(new NullSecurityContextRepository()))
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))
                .oauth2Login(oauth -> oauth
                        .authorizedClientRepository(new HttpSessionOAuth2AuthorizedClientRepository())
                        .successHandler(handler)
                        .failureHandler(handler))
                .build();
    }
}
