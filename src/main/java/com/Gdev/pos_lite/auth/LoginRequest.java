package com.Gdev.pos_lite.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para LoginRequest")
public class LoginRequest {

    @Schema(description = "Campo email", example = "ejemplo")
    private String email;

    @Schema(description = "Campo password", example = "ejemplo")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
