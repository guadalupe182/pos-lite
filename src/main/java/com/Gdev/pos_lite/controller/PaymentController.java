package com.Gdev.pos_lite.payment.controller;

import com.Gdev.pos_lite.payment.dto.PreferenceRequest;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.BackUrlsRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    @PostMapping("/create-preference")
    public ResponseEntity<?> createPreference(@RequestBody Map<String, Object> request) {
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
            
            List<PreferenceItemRequest> preferenceItems = new ArrayList<>();
            for (Map<String, Object> item : items) {
                Integer productId = (Integer) item.get("productId");
                Integer quantity = (Integer) item.get("quantity");
                Double price = item.get("price") != null ? (Double) item.get("price") : 0.0;
                String name = (String) item.get("name");
                
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                        .id(productId.toString())
                        .title(name != null ? name : "Producto")
                        .quantity(quantity)
                        .unitPrice(new BigDecimal(price))
                        .currencyId("MXN")
                        .build();
                preferenceItems.add(itemRequest);
            }

            BackUrlsRequest backUrls = BackUrlsRequest.builder()
                .success("https://pos-lite-front.vercel.app/payment/success")
                .failure("https://pos-lite-front.vercel.app/payment/failure")
                .pending("https://pos-lite-front.vercel.app/payment/pending")
                .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(preferenceItems)
                    .backUrls(backUrls)
                    .paymentMethods(PaymentMethodsRequest.builder()
                                .installments(1) //forzar el 1 pago (sin MSI)
                                .defaultInstallments(1)
                                .build())
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return ResponseEntity.ok(Map.of("id", preference.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
