package com.burntoburn.easyshift.login;

import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    private String redirectUri = "https://locahost:3000/callback";

    @Transactional
    public UserLoginResult login(String code) {
        // 1. 카카오에 액세스 토큰 요청
        String accessToken = getAccessToken(code);
        // 2. 카카오에서 사용자 정보 조회
        KakaoUserResponse kakaoUser = getKakaoUser(accessToken);
        // 3. 이메일이 DB에 존재하는지 확인
        Optional<User> optionalUser = userRepository.findByEmail(kakaoUser.getEmail());

        // 3-1. 기존 유저가 존재하면 토큰 갱신 후 로그인 성공 응답 반환
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            tokenRepository.findByUser(existingUser).ifPresent(token -> token.updateToken(accessToken));
            return new UserLoginResult(UserLoginResponse.fromEntity(existingUser, false), accessToken); // ✅ 액세스 토큰 별도 반환
        }

        // 3-2. 신규 유저면 최소 정보로 회원 가입 후 응답 반환
        User newUser = User.builder()
                .email(kakaoUser.getEmail())
                .name(kakaoUser.getNickname())
                .avatarUrl(kakaoUser.getProfileImage())
                .build();
        userRepository.save(newUser);
        Token token = new Token(newUser, accessToken);
        tokenRepository.save(token);
        return new UserLoginResult(UserLoginResponse.fromEntity(newUser, true), accessToken); // ✅ 액세스 토큰 별도 반환
    }

    private String getAccessToken(String code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(tokenUri, request, KakaoTokenResponse.class);
        return response.getBody().getAccessToken();
    }

    private KakaoUserResponse getKakaoUser(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, KakaoUserResponse.class);

        // 응답 확인용 로그
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        if (response.getBody() == null) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: 응답 바디가 null입니다.");
        }
        return response.getBody();
    }

    @Transactional
    public void signup(UserSignupRequest userSignupRequest, Long userId) {
        // 1. Role 검증
        Role role = Role.fromString(userSignupRequest.getRole());
        if (role == null) {
            throw new IllegalArgumentException("잘못된 역할(role) 값입니다.");
        }

        // 2. 유저 조회
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.updateProfile(userSignupRequest.getName(), userSignupRequest.getPhoneNumber(), role);
        }
    }
}
