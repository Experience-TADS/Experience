package com.senai.experience.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Novo par de tokens emitido pela rotação do refresh token")
@Data
@AllArgsConstructor
public class RefreshResponse {

    @Schema(description = "Novo access token JWT de curta duração")
    private String token;

    @Schema(description = "Novo refresh token opaco (o anterior foi invalidado pela rotação)")
    private String refreshToken;
}
