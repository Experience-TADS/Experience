package com.senai.experience.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.AgendaRequest;
import com.senai.experience.DTO.request.AtualizarStatusAgendaRequest;
import com.senai.experience.DTO.response.AgendaResponse;
import com.senai.experience.entities.Agenda;
import com.senai.experience.mappers.AgendaMapper;
import com.senai.experience.services.AgendaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<AgendaResponse> criar(
            @Valid @RequestBody AgendaRequest request,
            Authentication auth) {

        Long colaboradorId = resolverIdDoToken(auth);
        Agenda agenda = agendaService.criar(request, colaboradorId);
        return ResponseEntity.status(201).body(AgendaMapper.toResponse(agenda));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public List<AgendaResponse> listar(Authentication auth) {
        Long colaboradorId = resolverIdDoToken(auth);
        return agendaService.listarPorColaborador(colaboradorId)
                .stream()
                .map(AgendaMapper::toResponse)
                .toList();
    }

    /**
     * CLIENTE só pode consultar agendamentos dos quais é o cliente.
     * VENDEDOR só pode consultar os seus próprios.
     * ADMIN pode consultar qualquer um.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN', 'CLIENTE')")
    public ResponseEntity<AgendaResponse> buscarPorId(@PathVariable Long id, Authentication auth) {
        Long usuarioId = resolverIdDoToken(auth);
        boolean isAdmin = isAdmin(auth);
        Agenda agenda = agendaService.buscarPorId(id, usuarioId, isAdmin);
        return ResponseEntity.ok(AgendaMapper.toResponse(agenda));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<AgendaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AgendaRequest request,
            Authentication auth) {

        Long colaboradorId = resolverIdDoToken(auth);
        Agenda agenda = agendaService.atualizar(id, request, colaboradorId);
        return ResponseEntity.ok(AgendaMapper.toResponse(agenda));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<AgendaResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusAgendaRequest request,
            Authentication auth) {

        Long colaboradorId = resolverIdDoToken(auth);
        boolean isAdmin = isAdmin(auth);
        Agenda agenda = agendaService.atualizarStatus(id, request, colaboradorId, isAdmin);
        return ResponseEntity.ok(AgendaMapper.toResponse(agenda));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            Authentication auth) {

        Long colaboradorId = resolverIdDoToken(auth);
        agendaService.deletar(id, colaboradorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/colaborador/{id}/disponibilidade")
    @PreAuthorize("hasAnyRole('CLIENTE', 'VENDEDOR', 'ADMIN')")
    public ResponseEntity<List<LocalDateTime>> disponibilidade(@PathVariable Long id) {
        List<LocalDateTime> horariosOcupados = agendaService.consultarHorariosOcupados(id);
        return ResponseEntity.ok(horariosOcupados);
    }

    // Extrai o userId diretamente do token (gravado em details pelo JwtAuthFilter),
    // eliminando a query extra ao banco por email.
    private Long resolverIdDoToken(Authentication auth) {
        Object details = auth.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }
        throw new RuntimeException("ID do usuário não encontrado no token. Faça login novamente.");
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
