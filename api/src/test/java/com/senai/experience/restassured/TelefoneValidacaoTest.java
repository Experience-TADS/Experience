package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

/**
 * Testes de validação de campos para {@code /api/telefones}.
 *
 * <p>Cobre os critérios de aceite: número em branco, número com letras,
 * número curto demais e número longo demais devem retornar
 * {@code 400 Bad Request} com detalhe da violação no corpo da resposta.
 * Números válidos de 10 e 11 dígitos devem ser aceitos com {@code 201}.</p>
 */
@DisplayName("Telefone — validação de campos (REST Assured)")
class TelefoneValidacaoTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/telefones";

    private String corpo(String numero, int idUsuario) {
        return """
                { "numero": "%s", "idUsuario": %d }
                """.formatted(numero, idUsuario);
    }

    private Integer criarUsuario(String email) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Dono Telefone Validacao",
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

    // ── Número em branco / nulo ───────────────────────────────────────────────

    @Test
    @DisplayName("POST com número em branco retorna 400")
    void criar_numeroEmBranco_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("", 1))
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("numero"));
    }

    @Test
    @DisplayName("POST sem o campo número retorna 400")
    void criar_semNumero_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{ \"idUsuario\": 1 }")
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("numero"));
    }

    // ── Formato inválido ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST com número contendo letras retorna 400")
    void criar_numeroComLetras_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("1199ABCD000", 1))
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("numero"));
    }

    @Test
    @DisplayName("POST com número curto demais (menos de 10 dígitos) retorna 400")
    void criar_numeroCurto_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("119999", 1))
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("numero"));
    }

    @Test
    @DisplayName("POST com número longo demais (mais de 11 dígitos) retorna 400")
    void criar_numeroLongo_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("119999988881234", 1))
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("numero"));
    }

    @Test
    @DisplayName("POST com número com hífen/parênteses retorna 400 (apenas dígitos são aceitos)")
    void criar_numeroComMascara_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("(11) 99999-8888", 1))
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("numero"));
    }

    // ── Formatos válidos ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST com celular de 11 dígitos (com nono dígito) retorna 201")
    void criar_celular11Digitos_retorna201() {
        String token = obterTokenAdmin();
        Integer idUsuario = criarUsuario("valida.tel11@teste.com");

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("11999998888", idUsuario))
        .when()
                .post(BASE)
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("POST com telefone fixo de 10 dígitos retorna 201")
    void criar_telefoneFixo10Digitos_retorna201() {
        String token = obterTokenAdmin();
        Integer idUsuario = criarUsuario("valida.tel10@teste.com");

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("1133334444", idUsuario))
        .when()
                .post(BASE)
        .then()
                .statusCode(201);
    }

    // ── PUT também valida ─────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT com número inválido retorna 400")
    void atualizar_numeroInvalido_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("123", 1))
        .when()
                .put(BASE + "/1")
        .then()
                .statusCode(400)
                .body("detalhes", containsString("numero"));
    }
}
