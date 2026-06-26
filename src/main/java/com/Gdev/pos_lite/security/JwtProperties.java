package com.Gdev.pos_lite.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;

    private String issuer;

    private long expMinutes;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getExpMinutes() {
        return expMinutes;
    }

    public void setExpMinutes(long expMinutes) {
        this.expMinutes = expMinutes;
    }
}
