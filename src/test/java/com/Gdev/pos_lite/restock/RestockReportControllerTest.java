package com.Gdev.pos_lite.restock;

import com.Gdev.pos_lite.security.JwtService;
import com.Gdev.pos_lite.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestockReportController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:3000")
class RestockReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestockReportService restockReportService;

    @MockBean
    private JwtService jwtService;

    @Test
    void registraUnReporteValido() throws Exception {
        RestockReport saved = RestockReport.builder()
                .id(1L)
                .reportDate(LocalDate.of(2026, 11, 7))
                .productName("Aguacate Hass")
                .quantity(new BigDecimal("10"))
                .unit("caja")
                .build();
        when(restockReportService.create(any(RestockReport.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/restock/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reportDate\":\"2026-11-07\",\"productName\":\"Aguacate Hass\",\"quantity\":10,\"unit\":\"caja\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Aguacate Hass"));

        verify(restockReportService).create(any(RestockReport.class));
    }

    @Test
    void rechazaUnReporteInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/restock/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reportDate\":\"2026-11-07\",\"quantity\":10,\"unit\":\"caja\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(restockReportService);
    }
}
