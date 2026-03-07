package com.senai.experience.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.senai.experience.entities.Endereco;
import com.senai.experience.services.EnderecoService;

@RestController //Transforma a classe em um controlador REST, permitindo que ela responda a requisições HTTP
@RequestMapping("/api/endereco") //Define o caminho base para as rotas deste controlador, ou seja, todas as rotas definidas aqui começarão com "/api/endereco"

public class EnderecoController {
    @Autowired //Injeta a dependência do serviço de endereço, permitindo que o controlador utilize os métodos definidos no serviço para manipular os dados de endereço
    private EnderecoService enderecoService;

    @GetMapping
    public List<Endereco> getAllEnderecos() {
        return enderecoService.findAll();
    }

    @GetMapping("/{id}")
    public Endereco getEnderecoById(@PathVariable Long id) {
        return enderecoService.findById(id);
    }

    @PostMapping
    public Endereco createEndereco(@RequestBody Endereco endereco) {
        return enderecoService.save(endereco);
    }

    @PutMapping("/{id}")
    public Endereco updateEndereco(@PathVariable Long id, @RequestBody Endereco endereco) {
        endereco.setId(id);
        return enderecoService.save(endereco);
    }

    @DeleteMapping("/{id}")
    public void deleteEndereco(@PathVariable Long id) {
        enderecoService.delete(id);
    }
}