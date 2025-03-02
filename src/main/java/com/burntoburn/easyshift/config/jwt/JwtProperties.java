package com.burntoburn.easyshift.config.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String issuer;

    @NotBlank(message = "JWT secret key must not be blank")
    @Size(min = 32, message = "JWT secret key must be at least 32 characters")
    private String secretKey;
}