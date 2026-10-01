package com.senai.experience.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Schema(description = "Dados para cadastro ou atualização de um endereço")
@Data
public class EnderecoRequest {

    @Schema(description = "CEP no formato 00000-000 ou 00000000", example = "01001-000")
    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 00000-000 ou 00000000")
    private String cep;

    @Schema(description = "Logradouro (rua, avenida, etc.)", example = "Praça da Sé", maxLength = 200)
    @NotBlank(message = "O logradouro é obrigatório")
    @Size(max = 200, message = "O logradouro deve ter no máximo 200 caracteres")
    private String logradouro;

    @Schema(description = "Número do imóvel", example = "100", minimum = "1", maximum = "99999")
    @Min(value = 1, message = "O número deve ser maior que zero")
    @Max(value = 99999, message = "Número de endereço inválido")
    private int numero;

    @Schema(description = "Bairro", example = "Sé", maxLength = 100)
    @NotBlank(message = "O bairro é obrigatório")
    @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo", maxLength = 100)
    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @Schema(description = "Sigla do estado com 2 letras", example = "SP")
    @NotBlank(message = "O estado é obrigatório")
    @Pattern(regexp = "[A-Za-z]{2}", message = "O estado deve ser a sigla de 2 letras (ex: SP)")
    private String estado;

    @Schema(description = "ID do usuário proprietário do endereço", example = "1")
    @NotNull(message = "O ID do usuário é obrigatório")
    private Long idUsuario;
}
