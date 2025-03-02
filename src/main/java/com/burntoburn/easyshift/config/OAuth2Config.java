package com.burntoburn.easyshift.config;

import com.burntoburn.easyshift.config.jwt.TokenAuthenticationFilter;
import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.oauth2.handler.OAuth2FailureHandler;
import com.burntoburn.easyshift.oauth2.handler.OAuth2SuccessHandler;
import com.burntoburn.easyshift.oauth2.repository.OAuth2AuthorizationRequestRepository;
import com.burntoburn.easyshift.oauth2.service.OAuth2UserCustomService;
import com.burntoburn.easyshift.repository.user.RefreshTokenRepository;
import com.burntoburn.easyshift.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OAuth2Config {

    private static final String[] OAUTH2_ENDPOINTS = {"/oauth2/**", "/login/**"};

    private final TokenProvider tokenProvider;
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    @Bean
    @Order(2)
    public SecurityFilterChain oAuth2FilterChain(HttpSecurity http) throws Exception {
        validateDependencies();

        http
                .securityMatcher(OAUTH2_ENDPOINTS)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll())
                .oauth2Login(this::configureOAuth2Login)
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // OAuth2 로그인 설정
    private void configureOAuth2Login(OAuth2LoginConfigurer<HttpSecurity> oauth2) {
        oauth2
                .authorizationEndpoint(authEndPoint ->
                        authEndPoint.authorizationRequestRepository(oAuth2AuthorizationRequestRepository))
                .userInfoEndpoint(userInfoEndpoint ->
                        userInfoEndpoint.userService(oAuth2UserCustomService))
                .successHandler(oAuth2SuccessHandler())
                .failureHandler(oAuth2FailureHandler());
    }

    private void validateDependencies() {
        Objects.requireNonNull(tokenProvider, "TokenProvider must not be null");
        Objects.requireNonNull(oAuth2UserCustomService, "OAuth2UserCustomService must not be null");
        Objects.requireNonNull(refreshTokenRepository, "RefreshTokenRepository must not be null");
        Objects.requireNonNull(userService, "UserService must not be null");
        Objects.requireNonNull(oAuth2AuthorizationRequestRepository,
                "OAuth2AuthorizationRequestRepository must not be null");

        log.debug("OAuth2Config dependencies validated successfully");
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestRepository,
                userService);
    }

    @Bean
    public OAuth2FailureHandler oAuth2FailureHandler() {
        return new OAuth2FailureHandler();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }
}
