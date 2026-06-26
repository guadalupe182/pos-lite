package com.Gdev.pos_lite.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "DTO para UserInfoResponse")
public class UserInfoResponse {

    @Schema(description = "Campo email", example = "ejemplo")
    private final String email;

    @Schema(description = "Campo roles", example = "ejemplo")
    private final Set<String> roles;

    public UserInfoResponse(String email, Set<String> roles) {
        this.email = email;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
