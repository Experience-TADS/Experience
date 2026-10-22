package com.senai.experience.DTO.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Corpo opcional do logout para revogar o refresh token atual")
@Data
public class LogoutRequest {

    @Schema(description = "Refresh token a revogar (opcional). Ausência não altera o status do logout.")
    // S-011: token opaco fora da sanitização XSS global.
    @JsonDeserialize(using = StringDeserializer.class)
    private String refreshToken;
}
