package com.burntoburn.easyshift.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. CSRF 보호 비활성화 (세션 기반에서는 보안적으로 필요)
        // 2. iframe 정책 설정 유지
        // 3. CORS 설정 유지
        // 4. 세션 정책 변경: 세션 기반 인증이므로 `IF_REQUIRED`
        // 5. form 로긴 해제 (UsernamePasswordAuthenticationFilter 비활성화)
        // 6. 로그인 인증창이 뜨지 않게 비활성화

        http
                .csrf(CsrfConfigurer::disable)
                .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .cors((cors) -> cors.configurationSource(configurationSource()))
                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(FormLoginConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable);

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));  // 모든 Origin 허용
        configuration.setAllowCredentials(true);  // 쿠키 포함 요청 허용 (세션 기반 인증 필요)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}