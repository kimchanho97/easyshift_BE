package com.burntoburn.easyshift.login;

import com.burntoburn.easyshift.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String accessToken;

    public Token(User user, String accessToken) {
        this.user = user;
        this.accessToken = accessToken;
    }

    public void updateToken(String accessToken) {
        this.accessToken = accessToken;
    }
}