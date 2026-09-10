package com.senai.experience.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Utilitário para obter tokens JWT nos testes de integração.
 */
public class AuthHelper {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Cria um usuário e retorna o token JWT obtido via login.
     */
    public static String obterToken(MockMvc mockMvc, String email, String senha, String role) throws Exception {
        // Cria o usuário
        mockMvc.perform(post("/api/usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "Usuário Teste",
                          "email": "%s",
                          "senha": "%s",
                          "dataNascimento": "1990-01-01",
                          "role": "%s"
                        }
                        """.formatted(email, senha, role)));

        // Faz login e extrai o token
        MvcResult result = mockMvc.perform(post("/api/usuario/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email": "%s", "senha": "%s"}
                        """.formatted(email, senha)))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        JsonNode json = mapper.readTree(body);
        return json.get("token").asText();
    }
}
