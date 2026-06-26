package com.Gdev.pos_lite.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@Tag(name = "Modulo", description = "Endpoints para la gestión de modulo")
public class HomeController {

    @GetMapping("/")
    @Operation(summary = "home", description = "Endpoint para home")
    public Map<String, String> home() {
        return Map.of("message", "API de POS-lite funcionando correctamente");
    }
}
