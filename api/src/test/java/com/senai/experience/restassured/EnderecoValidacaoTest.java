package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

/**
 * Testes de validação de campos para {@code /api/endereco}.
 *
 * <p>Cobre os critérios de aceite: CEP inválido, campos obrigatórios em branco
 * e estado fora do padrão devem retornar {@code 400 Bad Request} com detalhe
 * da violação no corpo da resposta.</p>
 */
@DisplayName("Endereco — validação de campos (REST Assured)")
class EnderecoValidacaoTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/endereco";

    // ── CEP ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST com CEP em branco retorna 400")
    void criar_cepEmBranco_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "cep": "",
                          "logradouro": "Rua das Flores",
                          "numero": 10,
                          "bairro": "Centro",
                          "cidade": "São Paulo",
                          "estado": "SP",
                          "idUsuario": 1
                        }
                        """)
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("cep"));
    }

    @Test
    @DisplayName("POST com CEP fora do padrão retorna 400")
    void criar_cepFormatoInvalido_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "cep": "1234",
                          "logradouro": "Rua das Flores",
                          "numero": 10,
                          "bairro": "Centro",
                          "cidade": "São Paulo",
                          "estado": "SP",
                          "idUsuario": 1
                        }
                        """)
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("cep"));
    }

    @Test
    @DisplayName("POST com CEP no formato com hífen (00000-000) retorna 201")
    void criar_cepComHifen_retorna201() {
        String token = obterTokenAdmin();
        Integer idUsuario = criarUsuarioEObterToken(
                "valida.cep.hifen@teste.com", "senha123", "CLIENTE") != null
                ? extrairIdUsuario("valida.cep.hifen@teste.com")
                : 1;

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "cep": "01001-000",
                          "logradouro": "Praça da Sé",
                          "numero": 1,
                          "bairro": "Sé",
                          "cidade": "São Paulo",
                          "estado": "SP",
                          "idUsuario": %d
                        }
                        """.formatted(idUsuario))
        .when()
                .post(BASE)
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("POST com CEP no formato sem hífen (00000000) retorna 201")
    void criar_cepSemHifen_retorna201() {
        String token = obterTokenAdmin();
        Integer idUsuario = extrairIdUsuario("valida.cep.semhifen@teste.com");

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "cep": "01001000",
                          "logradouro": "Praça da Sé",
                          "numero": 1,
                          "bairro": "Sé",
                          "cidade": "São Paulo",
                          "estado": "SP",
                          "idUsuario": %d
                        }
                        """.formatted(idUsuario))
        .when()
                .post(BASE)
        .then()
                .statusCode(201);
    }

    // ── Campos obrigatórios ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST com logradouro em branco retorna 400")
    void criar_logradouroEmBranco_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "cep": "01001-000",
                          "logradouro": "",
                          "numero": 10,
                          "bairro": "Centro",
                          "cidade": "São Paulo",
                          "estado": "SP",
                          "idUsuario": 1
                        }
                        """)
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("logradouro"));
    }

    @Test
    @DisplayName("POST com bairro em branco retorna 400")
    void criar_bairroEmBranco_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "cep": "01001-000",
                          "logradouro": "Rua das Flores",
                          "numero": 10,
                          "bairro": "",
                          "cidade": "São Paulo",
                          "estado": "SP",
                          "idUsuario": 1
                        }
                        """)
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("bairro"));
    }

    @Test
    @DisplayName("POST com cidade em branco retorna 400")
    void criar_cidadeEmBranco_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "cep": "01001-000",
                          "logradouro": "Rua das Flores",
                          "numero": 10,
                          "bairro": "Centro",
                          "cidade": "",
                          "estado": "SP",
                          "idUsuario": 1
                        }
                        """)
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("cidade"));
    }

    // ── Estado ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST com estado inválido (mais de 2 letras) retorna 400")
    void criar_estadoInvalido_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "cep": "01001-000",
                          "logradouro": "Rua das Flores",
                          "numero": 10,
                          "bairro": "Centro",
                          "cidade": "São Paulo",
                          "estado": "SPA",
                          "idUsuario": 1
                        }
                        """)
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("estado"));
    }

    // ── PUT também valida ─────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT com CEP inválido retorna 400")
    void atualizar_cepInvalido_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "cep": "ABCDE",
                          "logradouro": "Rua das Flores",
                          "numero": 10,
                          "bairro": "Centro",
                          "cidade": "São Paulo",
                          "estado": "SP",
                          "idUsuario": 1
                        }
                        """)
        .when()
                .put(BASE + "/1")
        .then()
                .statusCode(400)
                .body("detalhes", containsString("cep"));
    }

    // ── Utilitário ───────────────────────────────────────────────────────────

    /**
     * Cria um usuário via endpoint público e retorna seu {@code id}.
     * Ignora conflito (e-mail duplicado) e busca o token apenas para reutilizar
     * o fluxo já existente na base.
     */
    private Integer extrairIdUsuario(String email) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Usuario Validacao",
                          "email": "%s",
                          "senha": "senha123",
                          "dataNascimento": "1990-01-01",
                          "role": "CLIENTE"
                        }
                        """.formatted(email))
        .when()
                .post("/api/usuario")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
