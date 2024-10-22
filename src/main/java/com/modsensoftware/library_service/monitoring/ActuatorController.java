package com.modsensoftware.library_service.monitoring;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actuator")
public class ActuatorController {
    @Operation(summary = "Проверка состояния сервиса. Поднят он или нет.",
            description = "в случае если сервис поднят, будет возвращен ответ **UP**")
    @ApiResponse(
            responseCode = "200", description = "сервис поднят"
    )
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }
}
