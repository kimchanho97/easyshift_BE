package com.burntoburn.easyshift.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger는 지금 당장은 사용하지 않습니다.
 * 소통은 노션 페이지를 통해 이루어집니다.
 */

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "테스트 API 문서",
                version = "1.0",
                description = "API 문서입니다.",
                contact = @Contact(
                        name = "이열치열",
                        url = "https://github.com/BurnToBurn-Devs ")),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class SwaggerConfig {

}
