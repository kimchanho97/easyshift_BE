package com.burntoburn.easyshift.config;

import com.burntoburn.easyshift.config.jwt.TokenAuthenticationFilter;
import com.burntoburn.easyshift.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final TokenAuthenticationFilter tokenAuthenticationFilter; // OAuth2Config에서 주입
    private static final String[] PUBLIC_STATIC_RESOURCES = {
        "/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**"
    };
    private static final String[] PUBLIC_API_ENDPOINTS = {
        "/api/user/info", "/api/user/login", "/api/public/**"
    };

    @Bean
    @Order(1) // OAuth2 설정보다 먼저 적용
    public SecurityFilterChain defalutSecurityFilterChain(HttpSecurity http, TokenAuthenticationFilter tokenAuthenticationFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable) // form 로그인 비활성화
            .httpBasic(AbstractHttpConfigurer::disable) // http Basic 비활성화
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            // 요청 경로에 따라 권한 설정
            .securityMatcher("/**")
            .authorizeHttpRequests(this::configureAuthorization)
            // JWT 토큰 필터 설정
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .clearAuthentication(true)
                .invalidateHttpSession(true))
            // 예외 처리 설정
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new AntPathRequestMatcher("/api/**")
                ));

        return http.build();
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
            .requestMatchers(PUBLIC_STATIC_RESOURCES).permitAll()
            .requestMatchers(PUBLIC_API_ENDPOINTS).permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().permitAll();
    }

}
