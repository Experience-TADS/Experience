package com.senai.experience.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.senai.experience.entities.Endereco;
import com.senai.experience.services.EnderecoService;

@RestController //Transforma a classe em um controlador REST, permitindo que ela responda a requisições HTTP
@RequestMapping("/api/endereco") //Define o caminho base para as rotas deste controlador, ou seja, todas as rotas definidas aqui começarão com "/api/endereco"

public class EnderecoController {
    @Autowired //Injeta a dependência do serviço de endereço, permitindo que o controlador utilize os métodos definidos no serviço para manipular os dados de endereço
    private EnderecoService enderecoService;

    @GetMapping
    public ResponseEntity<List<Endereco>> getAllEnderecos() {
        return ResponseEntity.ok(enderecoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Endereco> getEnderecoById(@PathVariable Long id) {
        Endereco endereco = enderecoService.findById(id);
        if (endereco != null) {
            return ResponseEntity.ok(endereco);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Endereco> createEndereco(@RequestBody Endereco endereco) {
        return ResponseEntity.ok(enderecoService.save(endereco));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Endereco> updateEndereco(@PathVariable Long id, @RequestBody Endereco endereco) {
        Endereco atualizado = enderecoService.update(id, endereco);
        if (atualizado != null) {
            return ResponseEntity.ok(atualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEndereco(@PathVariable Long id) {
        enderecoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}