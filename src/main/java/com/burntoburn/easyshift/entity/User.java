package com.burntoburn.easyshift.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users") // user는 일부 DB에서 예약어이므로 users로 사용
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // WORKER, ADMINISTRATOR

    private String avatarUrl;

    // JPA용 기본 생성자
    protected User() {
    }

    // Builder를 통한 생성자
    private User(Builder builder) {
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.role = builder.role;
        this.avatarUrl = builder.avatarUrl;
    }

    // Builder 생성 메서드
    public static Builder builder() {
        return new Builder();
    }

    // Builder 클래스
    public static class Builder {
        private String email;
        private String phoneNumber;
        private Role role;
        private String avatarUrl;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

}
