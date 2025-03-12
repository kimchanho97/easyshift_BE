package com.burntoburn.easyshift.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.getEmail() : null;
    }

    public String getNickname() {
        return kakaoAccount != null ? kakaoAccount.getProfile().getNickname() : null;
    }

    public String getProfileImage() {
        return kakaoAccount != null ? kakaoAccount.getProfile().getProfileImageUrl() : null;
    }

    public String getGender() {
        return kakaoAccount != null ? kakaoAccount.getGender() : null;
    }

    public String getAgeRange() {
        return kakaoAccount != null ? kakaoAccount.getAgeRange() : null;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {

        @JsonProperty("email")
        private String email;

        @JsonProperty("name")
        private String name;

        @JsonProperty("age_range")
        private String ageRange;

        @JsonProperty("birthday")
        private String birthday;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("profile")
        private Profile profile;
    }

    @Getter
    @NoArgsConstructor
    public static class Profile {

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;

        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;
    }
}
