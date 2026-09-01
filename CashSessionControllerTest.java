package com.Gdev.pos_lite.cash;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.Gdev.pos_lite.cash.dto.OpenCashRequestDto;
import com.Gdev.pos_lite.cash.service.CashSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CashSessionController.class)
public class CashSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CashSessionService cashSessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Configuraciones adicionales si es necesario
    }

    @Test
    public void testOpenCashSession() throws Exception {
        OpenCashRequestDto requestDto = new OpenCashRequestDto();
        requestDto.setInitialCash(100.0);
        requestDto.setOpenedBy("user1");

        when(cashSessionService.openSession(any(OpenCashRequestDto.class), anyString())).thenReturn(new CashSession());

        mockMvc.perform(post("/api/v1/cash-sessions/open")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCloseCashSession() throws Exception {
        Long sessionId = 1L;
        when(cashSessionService.closeSession(sessionId, anyString())).thenReturn(new CashSession());

        mockMvc.perform(post("/api/v1/cash-sessions/{id}/close", sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
