package com.Gdev.pos_lite.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "POS-lite API",
        version = "0.3.0",
        description = "API del backend POS-lite: autenticación, POS Fit y POS Style."))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenAPIConfig {

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi fitApi() {
        return GroupedOpenApi.builder()
                .group("fit")
                .pathsToMatch("/api/memberships/**")
                .build();
    }

    @Bean
    public GroupedOpenApi styleApi() {
        return GroupedOpenApi.builder()
                .group("style")
                .pathsToMatch("/api/boutique-items/**")
                .build();
    }
}
