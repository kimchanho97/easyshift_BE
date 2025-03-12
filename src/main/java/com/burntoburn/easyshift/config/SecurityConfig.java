package com.burntoburn.easyshift.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    //    private final TokenProvider tokenProvider;
//    private final OAuth2UserCustomService oAuth2UserCustomService;
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final UserService userService;
//    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;
    private final AccessTokenAuthenticationFilter accessTokenAuthenticationFilter;

    private static final String[] PUBLIC_STATIC_RESOURCES = {
            "/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**"
    };
    private static final String[] PUBLIC_API_ENDPOINTS = {
            "/api/user/login", "/api/public/**", "/api/auth/**", "/oauth2/**", "/login/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        validateDependencies();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // Authorization rules
                .authorizeHttpRequests(this::configureAuthorization)
                // OAuth2 login configuration
//                .oauth2Login(this::configureOAuth2Login)
                .addFilterBefore(accessTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // JWT token filter
//                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // Logout configuration
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login")
//                        .clearAuthentication(true)
//                        .invalidateHttpSession(true))
                // Exception handling
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED),
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

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");  // 모든 헤더 허용
        configuration.addAllowedMethod("*");  // 모든 HTTP 메서드 허용
        configuration.setAllowedOriginPatterns(Collections.singletonList("*")); // 모든 Origin 허용
        configuration.setAllowCredentials(true);  // 인증 정보 포함 가능 (쿠키 등)
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));  // 🔹 응답 헤더에서 Authorization 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // OAuth2 login configuration
//    private void configureOAuth2Login(OAuth2LoginConfigurer<HttpSecurity> oauth2) {
//        oauth2
//                .authorizationEndpoint(authEndPoint ->
//                        authEndPoint.authorizationRequestRepository(oAuth2AuthorizationRequestRepository))
//                .userInfoEndpoint(userInfoEndpoint ->
//                        userInfoEndpoint.userService(oAuth2UserCustomService))
//                .successHandler(oAuth2SuccessHandler())
//                .failureHandler(oAuth2FailureHandler());
//    }
//
//    private void validateDependencies() {
//        Objects.requireNonNull(tokenProvider, "TokenProvider must not be null");
//        Objects.requireNonNull(oAuth2UserCustomService, "OAuth2UserCustomService must not be null");
//        Objects.requireNonNull(refreshTokenRepository, "RefreshTokenRepository must not be null");
//        Objects.requireNonNull(userService, "UserService must not be null");
//        Objects.requireNonNull(oAuth2AuthorizationRequestRepository,
//                "OAuth2AuthorizationRequestRepository must not be null");
//
//        log.debug("Security configuration dependencies validated successfully");
//    }
//
//    @Bean
//    public TokenAuthenticationFilter tokenAuthenticationFilter() {
//        return new TokenAuthenticationFilter(tokenProvider);
//    }
//
//    @Bean
//    public OAuth2SuccessHandler oAuth2SuccessHandler() {
//        return new OAuth2SuccessHandler(
//                tokenProvider,
//                refreshTokenRepository,
//                oAuth2AuthorizationRequestRepository,
//                userService);
//    }
//
//    @Bean
//    public OAuth2FailureHandler oAuth2FailureHandler() {
//        return new OAuth2FailureHandler();
//    }
}