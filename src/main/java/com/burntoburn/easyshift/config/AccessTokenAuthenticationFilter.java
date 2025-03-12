package com.burntoburn.easyshift.config;

import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.login.Token;
import com.burntoburn.easyshift.login.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 Authorization 값 가져오기
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 2. 헤더가 존재하고 Bearer 토큰인지 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 이후의 토큰 값 추출
        String accessToken = authorizationHeader.substring(7);

        // 4. 토큰 저장소에서 userId 조회
        Token tokenEntity = tokenRepository.findByAccessToken(accessToken)
                .orElse(null);

        if (tokenEntity == null) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = tokenEntity.getUser();
        Long userId = user.getId();
        String role = user.getRole().name(); // 예: "ROLE_USER", "ROLE_ADMIN"

        // 5. SecurityContextHolder에 인증 객체 저장
        Authentication authentication = createAuthentication(userId, role);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private Authentication createAuthentication(Long userId, String role) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }
}
