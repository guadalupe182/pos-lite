package com.Gdev.pos_lite.payment.controller;

import com.Gdev.pos_lite.payment.dto.PaymentRequestDTO;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostMapping("/create-preference")
    public ResponseEntity<Map<String, String>> createPreference(
            @RequestBody PaymentRequestDTO request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        try {
            // Opcional: usar el userId del token si lo necesitas
            // String userId = jwt.getSubject();

            MercadoPagoConfig.setAccessToken(accessToken);

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title(request.description())
                    .quantity(request.quantity())
                    .unitPrice(request.amount())
                    .currencyId("MXN")
                    .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://pos-lite-front.vercel.app/success")
                    .failure("https://pos-lite-front.vercel.app/failure")
                    .pending("https://pos-lite-front.vercel.app/pending")
                    .build();

            PreferenceRequest prefRequest = PreferenceRequest.builder()
                    .items(List.of(item))
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(prefRequest);

            return ResponseEntity.ok(Map.of("id", preference.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}