package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para {@code /api/itens-pedido}.
 *
 * <p>Todos os métodos exigem autenticação ({@code .anyRequest().authenticated()}).
 * Um item de pedido referencia um pedido ({@code idPedido}) e um produto
 * ({@code idProduto}). O produto id 1 é criado pelo {@code DataSeeder}; o pedido
 * é criado no próprio teste.</p>
 */
@DisplayName("ItemPedido — testes de integração (REST Assured)")
class ItemPedidoRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/itens-pedido";

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

    /** Cria um pedido (usando token ADMIN) e devolve o id. */
    private Integer criarPedido(String token, String sufixo) {
        Integer idCliente = criarUsuario("cliente.item." + sufixo + "@teste.com", "CLIENTE");
        Integer idVendedor = criarUsuario("vendedor.item." + sufixo + "@teste.com", "VENDEDOR");

        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "idCliente": %d,
                          "idVendedor": %d,
                          "dataPedido": "2025-01-15T10:00:00",
                          "valorTotal": 150000.00
                        }
                        """.formatted(idCliente, idVendedor))
        .when()
                .post("/api/pedido")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String corpoItem(int idPedido, int quantidade) {
        return """
                { "idPedido": %d, "idProduto": 1, "quantidade": %d }
                """.formatted(idPedido, quantidade);
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
    @DisplayName("POST sem token retorna 403")
    void criar_semToken_retorna403() {
        given()
                .contentType(ContentType.JSON)
                .body(corpoItem(1, 1))
        .when()
                .post(BASE)
        .then()
                .statusCode(403);
    }

    // ── Leitura ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET lista com token retorna 200 e página")
    void listar_comToken_retorna200() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE)
        .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Fluxo CRUD completo: criar, buscar, atualizar e deletar")
    void crudCompleto() {
        String token = obterTokenAdmin();
        Integer idPedido = criarPedido(token, "crud");

        // CREATE → 201
        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpoItem(idPedido, 2))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("idItemPedido", notNullValue())
                .extract()
                .path("idItemPedido");

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
                .body(corpoItem(idPedido, 5))
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
