package com.senai.experience.DTO.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefoneResponse {
    private Long id;
    private String numero;
    private Long idUsuario;
}
