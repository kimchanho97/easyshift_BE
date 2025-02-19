package com.burntoburn.easyshift.config.jwt;

import com.burntoburn.easyshift.entity.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    
    private final JwtProperties jwtproperties;
    private String secretKey;
    
    @PostConstruct
    protected void init() {
        String jwtSecret = jwtproperties.getSecretKey();
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            log.error("JWT secret key is missing. Please add application-oauth.yml to src/main/resources.");
            throw new IllegalStateException("Missing JWT secret key in application-oauth.yml");
        }
        secretKey = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }
    
    // 토큰 생성시 만료기간을 설정하여 발급
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }
    
    // Jwts 라이브러리로 token 생성
    private String makeToken(Date expiry, User user) {
        Date now = new Date();
        
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtproperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    
    //토큰 유효성 검증
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtproperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) { // 복호화 과정에서 에러가 난다면 유효하지 않은 토큰
            return false;
        }
    }
    
    // 사용자의 이메일과 토큰 인증정보를 반환
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token,
                authorities
        );
    }
    
    // token을 복호화 한후 body의 데이터를 반환
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtproperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
    
}
