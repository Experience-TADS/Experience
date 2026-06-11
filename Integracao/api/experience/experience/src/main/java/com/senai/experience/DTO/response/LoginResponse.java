package com.senai.experience.DTO.response;

import com.senai.experience.entities.role.UserRole;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private UserRole role;
}
