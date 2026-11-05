package com.senai.experience.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Dados de um endereço retornados pela API")
@Data
public class EnderecoResponse {

    @Schema(description = "Identificador único do endereço", example = "1")
    private Long id;

    @Schema(description = "CEP no formato informado no cadastro", example = "01001-000")
    private String cep;

    @Schema(description = "Logradouro (rua, avenida, etc.)", example = "Praça da Sé")
    private String logradouro;

    @Schema(description = "Número do imóvel", example = "100")
    private int numero;

    @Schema(description = "Bairro", example = "Sé")
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Schema(description = "Sigla do estado", example = "SP")
    private String estado;

    @Schema(description = "ID do usuário proprietário do endereço", example = "1")
    private Long idUsuario;
}
