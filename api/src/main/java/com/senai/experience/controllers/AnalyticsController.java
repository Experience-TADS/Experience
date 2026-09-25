package com.senai.experience.controllers;

import com.senai.experience.DTO.request.SessaoAnalyticsRequest;
import com.senai.experience.DTO.response.SessaoAnalyticsResponse;
import com.senai.experience.DTO.response.SessaoAnalyticsResumoResponse;
import com.senai.experience.services.SessaoAnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final SessaoAnalyticsService sessaoAnalyticsService;

    @PostMapping("/sessao")
    public ResponseEntity<SessaoAnalyticsResponse> registrar(@RequestBody @Valid SessaoAnalyticsRequest dto) {
        return ResponseEntity.status(201).body(sessaoAnalyticsService.registrar(dto));
    }

    @GetMapping("/sessao/resumo")
    public ResponseEntity<List<SessaoAnalyticsResumoResponse>> resumo() {
        return ResponseEntity.ok(sessaoAnalyticsService.resumo());
    }
}
