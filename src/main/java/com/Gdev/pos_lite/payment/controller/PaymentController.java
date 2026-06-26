package com.Gdev.pos_lite.payment.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment", description = "Endpoints para la gestión de payment")
public class PaymentController {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostMapping("/create-preference")
    @Operation(summary = "createPreference", description = "Endpoint para createpreference")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Preferencia creada"), @ApiResponse(responseCode = "400", description = "Solicitud inválida"), @ApiResponse(responseCode = "500", description = "Error interno") })
    public ResponseEntity<Map<String, String>> createPreference(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsList = (List<Map<String, Object>>) payload.get("items");
            String customerEmail = (String) payload.get("customerEmail");
            MercadoPagoConfig.setAccessToken(accessToken);
            PreferenceItemRequest item = PreferenceItemRequest.builder().title((String) itemsList.get(0).get("name")).quantity((Integer) itemsList.get(0).get("quantity")).unitPrice(new java.math.BigDecimal(itemsList.get(0).get("price").toString())).currencyId("MXN").build();
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder().success("https://pos-lite-front.vercel.app/payment/success").failure("https://pos-lite-front.vercel.app/payment/failure").pending("https://pos-lite-front.vercel.app/payment/pending").build();
            PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder().items(List.of(item)).backUrls(backUrls).autoReturn("approved");
            // Agregar email del comprador si existe
            if (customerEmail != null && !customerEmail.isBlank()) {
                PreferencePayerRequest payer = PreferencePayerRequest.builder().email(customerEmail).build();
                builder.payer(payer);
            }
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(builder.build());
            return ResponseEntity.ok(Map.of("id", preference.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
