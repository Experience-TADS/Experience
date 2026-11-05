package com.senai.experience.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(description = "Dados para registrar uma sessão de navegação de um cliente no aplicativo")
@Data
public class SessaoAnalyticsRequest {

    @Schema(description = "ID do cliente que navegou na seção", example = "3")
    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clienteId;

    @Schema(description = "Nome da seção do aplicativo visitada", example = "catalogo-produtos")
    @NotBlank(message = "A seção é obrigatória")
    private String secao;

    @Schema(description = "Tempo de permanência na seção em segundos", example = "120", minimum = "1")
    @NotNull(message = "A duração é obrigatória")
    @Positive(message = "A duração deve ser maior que zero")
    private Integer duracaoSegundos;
}
