package com.Gdev.pos_lite.style;

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

@WebMvcTest(controllers = BoutiqueItemController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:3000")
class BoutiqueItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoutiqueItemService boutiqueItemService;

    @MockBean
    private JwtService jwtService;

    @Test
    void filtraLosArticulosPorTenantDelJwt() throws Exception {
        when(boutiqueItemService.findAll("tenant-style")).thenReturn(List.of());

        mockMvc.perform(get("/api/boutique-items")
                        .with(jwt().authorities(new SimpleGrantedAuthority("STYLE"))
                                .jwt(token -> token.claim("tenantId", "tenant-style"))))
                .andExpect(status().isOk());

        verify(boutiqueItemService).findAll("tenant-style");
    }

    @Test
    void rechazaLaConsultaSiElJwtNoTieneTenant() throws Exception {
        mockMvc.perform(get("/api/boutique-items")
                        .with(jwt().authorities(new SimpleGrantedAuthority("STYLE"))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(boutiqueItemService);
    }
}
