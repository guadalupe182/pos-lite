package com.Gdev.pos_lite.fit;

import com.Gdev.pos_lite.security.JwtService;
import com.Gdev.pos_lite.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MembershipController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:3000")
class MembershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MembershipService membershipService;

    @MockBean
    private JwtService jwtService;

    @Test
    void filtraLasMembresiasPorTenantDelJwt() throws Exception {
        when(membershipService.findAll("tenant-fit")).thenReturn(List.of());

        mockMvc.perform(get("/api/memberships")
                        .with(jwt().authorities(new SimpleGrantedAuthority("FIT"))
                                .jwt(token -> token.claim("tenantId", "tenant-fit"))))
                .andExpect(status().isOk());

        verify(membershipService).findAll("tenant-fit");
    }

    @Test
    void rechazaLaConsultaSiElJwtNoTieneTenant() throws Exception {
        mockMvc.perform(get("/api/memberships")
                        .with(jwt().authorities(new SimpleGrantedAuthority("FIT"))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(membershipService);
    }
}
