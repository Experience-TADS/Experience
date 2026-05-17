package com.senai.experience.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.senai.experience.DTO.request.EnderecoRequest;
import com.senai.experience.DTO.response.EnderecoResponse;
import com.senai.experience.mappers.EnderecoMapper;
import com.senai.experience.entities.Endereco;
import com.senai.experience.entities.Usuario;
import com.senai.experience.services.EnderecoService;
import com.senai.experience.services.UsuarioService;

@RestController
@RequestMapping("/api/endereco")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<EnderecoResponse>> getAllEnderecos() {
        return ResponseEntity.ok(
            enderecoService.findAll()
                .stream()
                .map(EnderecoMapper::toResponse)
                .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponse> getEnderecoById(@PathVariable Long id) {
        Endereco endereco = enderecoService.findById(id);
        if (endereco != null) {
            return ResponseEntity.ok(EnderecoMapper.toResponse(endereco));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EnderecoResponse> createEndereco(@RequestBody EnderecoRequest dto) {
        Usuario usuario = usuarioService.findById(dto.getIdUsuario());
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }
        Endereco endereco = EnderecoMapper.toEntity(dto, usuario);
        Endereco salvo = enderecoService.save(endereco);
        return ResponseEntity.status(201).body(EnderecoMapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponse> updateEndereco(@PathVariable Long id, @RequestBody EnderecoRequest dto) {
        Usuario usuario = usuarioService.findById(dto.getIdUsuario());
        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }
        Endereco endereco = EnderecoMapper.toEntity(dto, usuario);
        Endereco atualizado = enderecoService.update(id, endereco);
        if (atualizado != null) {
            return ResponseEntity.ok(EnderecoMapper.toResponse(atualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEndereco(@PathVariable Long id) {
        enderecoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}