package com.senai.experience.restassured;

import com.senai.experience.ExperienceApplication;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;

/**
 * Classe base para testes de integração com REST Assured.
 *
 * <p>Diferente da suíte MockMvc existente (que testa a camada web isoladamente),
 * estes testes sobem um servidor HTTP real em uma porta aleatória e fazem
 * requisições de verdade pela rede — cobrindo o stack completo, incluindo os
 * filtros de segurança e a serialização JSON.</p>
 *
 * <p>Roda sob o perfil {@code test} (H2 em memória, ver
 * {@code application-test.properties}). O {@code DataSeeder} popula o banco no
 * startup, então o usuário admin ({@code admin@experience.com} / {@code admin123})
 * já existe para autenticação.</p>
 */
@SpringBootTest(
        classes = ExperienceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public abstract class RestAssuredBaseTest {

    /** Credenciais do admin criado pelo DataSeeder no perfil de teste. */
    protected static final String ADMIN_EMAIL = "admin@experience.com";
    protected static final String ADMIN_SENHA = "admin123";

    @LocalServerPort
    private int port;

    @BeforeEach
    void configurarRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "/";
    }

    /**
     * Autentica no endpoint de login e devolve o JWT.
     *
     * @param email e-mail do usuário
     * @param senha senha em texto plano
     * @return o token JWT retornado pela API
     */
    protected String obterToken(String email, String senha) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        { "email": "%s", "senha": "%s" }
                        """.formatted(email, senha))
        .when()
                .post("/api/usuario/login")
        .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }

    /** Atalho para obter um token com privilégios de ADMIN. */
    protected String obterTokenAdmin() {
        return obterToken(ADMIN_EMAIL, ADMIN_SENHA);
    }

    /**
     * Cria um usuário via endpoint público e retorna o token dele já autenticado.
     * Útil quando o teste precisa de um role específico (CLIENTE, VENDEDOR, etc.).
     *
     * @param email e-mail único do novo usuário
     * @param senha senha (mínimo 6 caracteres)
     * @param role  papel: ADMIN, VENDEDOR, CLIENTE ou IOT
     * @return token JWT do usuário recém-criado
     */
    protected String criarUsuarioEObterToken(String email, String senha, String role) {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Usuario Teste",
                          "email": "%s",
                          "senha": "%s",
                          "dataNascimento": "1990-01-01",
                          "role": "%s"
                        }
                        """.formatted(email, senha, role))
        .when()
                .post("/api/usuario")
        .then()
                .statusCode(201);

        return obterToken(email, senha);
    }
}
