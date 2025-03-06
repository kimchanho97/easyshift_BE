package com.burntoburn.easyshift.oauth2.handler;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.entity.user.RefreshToken;
import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.oauth2.repository.OAuth2AuthorizationRequestRepository;
import com.burntoburn.easyshift.oauth2.user.KakaoOAuth2User;
import com.burntoburn.easyshift.repository.user.RefreshTokenRepository;
import com.burntoburn.easyshift.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.burntoburn.easyshift.util.CookieUtil;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import java.time.Duration;
import java.util.Optional;

import static com.burntoburn.easyshift.oauth2.repository.OAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;
    private final UserService userService;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(2);
    @Value("${app.frontend.redirect-url}")
    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.info("OAuth2 Login 성공!");
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        // Oauth2를 통해 불러온 유저 정보
        KakaoOAuth2User kakaoOAuth2User = new KakaoOAuth2User(oAuth2User.getAttributes());
        User user = userService.findByEmail(kakaoOAuth2User.getEmail());


        // 자체 access token 생성 후 cookie에 추가
        String accessToken = tokenProvider.generateAccessToken(user, ACCESS_TOKEN_DURATION);
        addAccessTokenToCookie(request, response, accessToken);
        // 리프레시 토큰 생성 후 cookie에 추가
        String refreshToken = tokenProvider.generateRefreshToken(REFRESH_TOKEN_DURATION);
        saveToken(user, refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // Cookie에 저장한 frontend_redirect_url 값을 가져옴
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        // 없을 경우 설정 파일의 url을 적용
        String targetUrl = redirectUri.orElse(frontendRedirectUrl);

        String redirectPath;
        if (user.getRole() != Role.GUEST) {
            // 기존 사용자 (회원가입 완료)
            redirectPath = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("needSignUp", false)
                    .build()
                    .toUriString();
        } else {
            // 신규 사용자 (추가 회원가입 필요)
            redirectPath = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("needSignUp", true)
                    .queryParam("userId", user.getId())
                    .queryParam("email", user.getEmail())
                    .queryParam("avatarUrl", user.getAvatarUrl())
                    .build()
                    .toUriString();
        }

        //유저 정보를 리다이렉트
        getRedirectStrategy().sendRedirect(request,response,redirectPath);

        // 인증 설정 값 삭제
        clearAuthenticationAttributes(request, response);
    }

    // 생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 생성된 엑세스 토큰 쿠키에 저장
    private void addAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response,String accessToken) {
        int cookieMaxAge = (int) ACCESS_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, cookieMaxAge);
    }

    // RefreshToken 이 DB에 있는지 확인하고 업데이트 하거나 저장
    private void saveToken(User user, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .map(entity -> entity.update(newRefreshToken))
                .orElse(RefreshToken.builder().refreshToken(newRefreshToken).user(user).build());
        refreshTokenRepository.saveAndFlush(refreshToken);
    }

    // 인증 관련 설정값을 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestRepository.removeAuthorizationRequest(request, response);
    }

}
