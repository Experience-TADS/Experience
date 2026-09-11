package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para {@code /api/pedido}.
 *
 * <p>Regras de segurança:</p>
 * <ul>
 *   <li>{@code /api/pedido/meus-pedidos/**} → CLIENTE, VENDEDOR ou ADMIN</li>
 *   <li>{@code /api/pedido/**} (demais) → VENDEDOR ou ADMIN</li>
 * </ul>
 *
 * <p>Um pedido referencia um cliente ({@code idCliente}) e um vendedor
 * ({@code idVendedor}). Estes são criados via endpoint público de usuário.</p>
 */
@DisplayName("Pedido — testes de integração (REST Assured)")
class PedidoRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/pedido";

    private Integer criarUsuario(String email, String role) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Usuario %s",
                          "email": "%s",
                          "senha": "senha123",
                          "dataNascimento": "1990-01-01",
                          "role": "%s"
                        }
                        """.formatted(role, email, role))
        .when()
                .post("/api/usuario")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String corpoPedido(int idCliente, int idVendedor) {
        return """
                {
                  "idCliente": %d,
                  "idVendedor": %d,
                  "dataPedido": "2025-01-15T10:00:00",
                  "valorTotal": 150000.00
                }
                """.formatted(idCliente, idVendedor);
    }

    // ── Autorização ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET sem token retorna 403")
    void listar_semToken_retorna403() {
        given()
        .when()
                .get(BASE)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("CLIENTE não pode acessar a lista geral de pedidos → 403")
    void listar_comCliente_retorna403() {
        String tokenCliente = criarUsuarioEObterToken(
                "cliente.pedido@teste.com", "senha123", "CLIENTE");

        given()
                .header("Authorization", "Bearer " + tokenCliente)
        .when()
                .get(BASE)
        .then()
                .statusCode(403);
    }

    // ── Leitura (VENDEDOR/ADMIN) ──────────────────────────────────────────────

    @Test
    @DisplayName("ADMIN acessa a lista geral de pedidos → 200")
    void listar_comAdmin_retorna200() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE)
        .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    @Test
    @DisplayName("CLIENTE acessa /meus-pedidos → 200")
    void meusPedidos_comCliente_retorna200() {
        String tokenCliente = criarUsuarioEObterToken(
                "cliente.meus@teste.com", "senha123", "CLIENTE");

        given()
                .header("Authorization", "Bearer " + tokenCliente)
        .when()
                .get(BASE + "/meus-pedidos")
        .then()
                .statusCode(200);
    }

    // ── CRUD (ADMIN) ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Fluxo CRUD completo como ADMIN: criar, buscar, atualizar e deletar")
    void crudCompleto_comAdmin() {
        String token = obterTokenAdmin();
        Integer idCliente = criarUsuario("cliente.crud.pedido@teste.com", "CLIENTE");
        Integer idVendedor = criarUsuario("vendedor.crud.pedido@teste.com", "VENDEDOR");

        // CREATE → 201
        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpoPedido(idCliente, idVendedor))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .path("id");

        // READ → 200
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/" + id)
        .then()
                .statusCode(200);

        // UPDATE → 200
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpoPedido(idCliente, idVendedor))
        .when()
                .put(BASE + "/" + id)
        .then()
                .statusCode(200);

        // DELETE → 204
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete(BASE + "/" + id)
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("GET por id inexistente retorna 404")
    void buscarInexistente_retorna404() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/99999999")
        .then()
                .statusCode(404);
    }
}
