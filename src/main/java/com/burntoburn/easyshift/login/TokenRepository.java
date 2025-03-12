package com.burntoburn.easyshift.login;

import com.burntoburn.easyshift.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByUser(User user);

    Optional<Token> findByAccessToken(String accessToken);
}
