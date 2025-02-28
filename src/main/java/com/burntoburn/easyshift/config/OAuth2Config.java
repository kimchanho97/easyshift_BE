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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {

    private TokenProvider tokenProvider;
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain oAuth2FilterChain(HttpSecurity http) throws Exception {


        http.securityMatcher("/oauth2/**", "/login/**") // OAuth2 관련 URL만 매칭
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> {
                    oauth2.authorizationEndpoint(
                                    authEndPoint ->
                                            authEndPoint.authorizationRequestRepository(
                                                    oAuth2AuthorizationRequestRepository))
                            .userInfoEndpoint(
                                    userInfoEndpointConfig ->
                                            userInfoEndpointConfig.userService(
                                                    oAuth2UserCustomService));
                    // 인증 성공 시 실행할 핸들러
                    oauth2.successHandler(oAuth2SuccessHanlder());
                    oauth2.failureHandler(oAuth2FailureHandler());
                }
        );
        // token filter를 가장 먼저 실행함
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHanlder() {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository, oAuth2AuthorizationRequestRepository, userService);
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
