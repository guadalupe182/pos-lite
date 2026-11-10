package com.Gdev.pos_lite.loyalty;

import com.Gdev.pos_lite.loyalty.dto.RedeemRequest;
import com.Gdev.pos_lite.loyalty.dto.RedeemResponse;
import com.Gdev.pos_lite.sale.SaleService;
import com.Gdev.pos_lite.security.JwtService;
import com.Gdev.pos_lite.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoyaltyController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:3000")
class LoyaltyControllerTest {

    private static final String REDEEM_BODY = """
            {
              "customerEmail": "customer@example.com",
              "points": 20,
              "saleTotal": 100.0
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleService saleService;

    @MockBean
    private JwtService jwtService;

    @Test
    void devuelveForbiddenSinAuthorityLoyalty() throws Exception {
        mockMvc.perform(post("/api/loyalty/redeem")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REDEEM_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void calculaElDescuentoConAuthorityLoyalty() throws Exception {
        when(saleService.previewRedemption(any(RedeemRequest.class)))
                .thenReturn(new RedeemResponse("customer@example.com", 20, 20.0, 80.0, 10));

        mockMvc.perform(post("/api/loyalty/redeem")
                        .with(jwt().authorities(new SimpleGrantedAuthority("LOYALTY")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REDEEM_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discount").value(20.0))
                .andExpect(jsonPath("$.finalTotal").value(80.0))
                .andExpect(jsonPath("$.remainingPoints").value(10));
    }
}
