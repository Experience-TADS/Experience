package com.senai.experience.DTO.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Requisição para renovar o access token a partir de um refresh token válido")
@Data
public class RefreshRequest {

    @Schema(description = "Refresh token opaco emitido no login ou na última renovação")
    @NotBlank
    // S-011: token opaco fora da sanitização XSS global — precisa bater exatamente com o hash.
    @JsonDeserialize(using = StringDeserializer.class)
    private String refreshToken;
}
