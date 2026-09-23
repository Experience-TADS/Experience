package com.senai.experience.DTO.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EnderecoRequest {

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "O CEP deve conter exatamente 8 dígitos numéricos")
    private String cep;

    @NotBlank(message = "O logradouro é obrigatório")
    @Size(max = 200, message = "O logradouro deve ter no máximo 200 caracteres")
    private String logradouro;

    @Min(value = 1, message = "O número deve ser maior que zero")
    @Max(value = 99999, message = "Número de endereço inválido")
    private int numero;

    @NotBlank(message = "O bairro é obrigatório")
    @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório")
    @Pattern(regexp = "[A-Za-z]{2}", message = "O estado deve ser a sigla de 2 letras (ex: SP)")
    private String estado;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long idUsuario;
}
