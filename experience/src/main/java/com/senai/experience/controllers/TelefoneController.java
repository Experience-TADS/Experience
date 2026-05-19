package com.senai.experience.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.TelefoneRequest;
import com.senai.experience.DTO.response.TelefoneResponse;
import com.senai.experience.entities.Telefone;
import com.senai.experience.entities.Usuario;
import com.senai.experience.mappers.TelefoneMapper;
import com.senai.experience.services.TelefoneService;
import com.senai.experience.services.UsuarioService;

@RestController
@RequestMapping("/api/telefones")
public class TelefoneController {

    @Autowired
    private TelefoneService telefoneService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Page<Telefone>> getAllTelefones(Pageable pageable) {
        return ResponseEntity.ok(telefoneService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TelefoneResponse> getTelefoneById(@PathVariable Long id) {
        Telefone telefone = telefoneService.findById(id);
        if (telefone != null) {
            return ResponseEntity.ok(TelefoneMapper.toResponse(telefone));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TelefoneResponse> createTelefone(@RequestBody TelefoneRequest dto) {
        Usuario usuario = usuarioService.findById(dto.getIdUsuario());
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }
        Telefone telefone = TelefoneMapper.toEntity(dto, usuario);
        Telefone novoTelefone = telefoneService.save(telefone);
        return ResponseEntity.status(201).body(TelefoneMapper.toResponse(novoTelefone));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TelefoneResponse> updateTelefone(@PathVariable Long id, @RequestBody TelefoneRequest dto) {
        Usuario usuario = usuarioService.findById(dto.getIdUsuario());
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }
        Telefone telefone = TelefoneMapper.toEntity(dto, usuario);
        Telefone atualizado = telefoneService.update(id, telefone);
        if (atualizado != null) {
            return ResponseEntity.ok(TelefoneMapper.toResponse(atualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTelefone(@PathVariable Long id) {
        telefoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
