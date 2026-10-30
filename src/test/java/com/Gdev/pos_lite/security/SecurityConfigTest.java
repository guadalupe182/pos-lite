package com.Gdev.pos_lite.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(mock(JwtService.class));

    @Test
    void convierteFeaturesYRolesDelJwtEnAuthorities() {
        Jwt jwt = jwt(Map.of(
                "roles", List.of("ADMIN"),
                "features", List.of("LOYALTY")));

        AbstractAuthenticationToken authentication = securityConfig.jwtAuthenticationConverter().convert(jwt);

        assertThat(authentication).isNotNull();
        assertThat(authorities(authentication)).containsExactlyInAnyOrder("ROLE_ADMIN", "LOYALTY");
    }

    @Test
    void reconoceElClaimDirectoLoyalty() {
        Jwt jwt = jwt(Map.of("LOYALTY", true));

        AbstractAuthenticationToken authentication = securityConfig.jwtAuthenticationConverter().convert(jwt);

        assertThat(authentication).isNotNull();
        assertThat(authorities(authentication)).containsExactly("LOYALTY");
    }

    @Test
    void noConcedeLoyaltyCuandoElClaimEstaInactivo() {
        Jwt jwt = jwt(Map.of("LOYALTY", false, "roles", List.of("USER")));

        AbstractAuthenticationToken authentication = securityConfig.jwtAuthenticationConverter().convert(jwt);

        assertThat(authentication).isNotNull();
        assertThat(authorities(authentication)).containsExactly("ROLE_USER");
    }

    private Jwt jwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("user@example.com")
                .claims(existingClaims -> existingClaims.putAll(claims))
                .build();
    }

    private Set<String> authorities(AbstractAuthenticationToken authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
