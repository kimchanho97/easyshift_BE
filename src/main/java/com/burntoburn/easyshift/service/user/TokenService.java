package com.burntoburn.easyshift.service.user;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public String createNewAccessTokenByRefreshToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰 입니다.");
        }

        User user = refreshTokenService.findByRefreshToken(refreshToken).getUser();


        return tokenProvider.generateAccessToken(user, Duration.ofHours(2));
    }

    public String createNewAccessToken(User user) {

        return tokenProvider.generateAccessToken(user, Duration.ofHours(2));
    }


}
