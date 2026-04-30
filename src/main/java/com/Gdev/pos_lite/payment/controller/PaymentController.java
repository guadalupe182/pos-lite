package com.Gdev.pos_lite.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/create-preference")
    public ResponseEntity<?> createPreference(@RequestBody Map<String, Object> request) {
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

            // Transformar los items al formato que espera Mercado Pago
            List<Map<String, Object>> mpItems = new ArrayList<>();
            for (Map<String, Object> item : items) {
                Map<String, Object> mpItem = new HashMap<>();
                mpItem.put("title", item.get("name") != null ? item.get("name") : "Producto");
                mpItem.put("quantity", item.get("quantity"));

                // Aceptar tanto "price" como "unit_price"
                Object price = item.get("unit_price");
                if (price == null) {
                    price = item.get("price");
                }
                mpItem.put("unit_price", price);
                mpItem.put("currency_id", "MXN");
                mpItems.add(mpItem);
            }

            Map<String, Object> payload = Map.of("items", mpItems);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mercadopago.com/checkout/preferences"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                return ResponseEntity.ok(Map.of("id", responseMap.get("id")));
            } else {
                return ResponseEntity.status(response.statusCode()).body(Map.of("error", response.body()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}