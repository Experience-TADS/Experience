package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração REST Assured para {@code /api/agenda}.
 *
 * <p>Regras de segurança:</p>
 * <ul>
 *   <li>POST, GET, PUT, PATCH, DELETE → VENDEDOR ou ADMIN</li>
 *   <li>GET /{id} → VENDEDOR, ADMIN ou CLIENTE (com restrição de dono)</li>
 *   <li>GET /colaborador/{id}/disponibilidade → qualquer autenticado</li>
 * </ul>
 *
 * <p>O {@code DataSeeder} cria o admin ({@code admin@experience.com}) e o
 * vendedor ({@code vendedor.toyota@experience.com}) no startup. Os testes
 * criam usuários adicionais conforme necessário.</p>
 */
@DisplayName("Agenda — testes de integração (REST Assured)")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AgendaRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/agenda";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Email único por execução evita colisão com outros testes que rodam no mesmo contexto
    private final String emailCliente  = "agenda.cliente."  + System.currentTimeMillis() + "@teste.com";
    private final String emailVendedor = "vendedor.toyota@experience.com"; // criado pelo DataSeeder

    private String tokenAdmin;
    private String tokenVendedor;
    private String tokenCliente;
    private Long idCliente;
    private Long idVendedor;

    @BeforeAll
    void criarUsuarios() {
        // O RestAssured ainda não tem porta configurada aqui — configuramos na primeira chamada
        // através do @BeforeEach da classe base. Por isso criamos no primeiro @BeforeEach.
    }

    @BeforeEach
    void setup() {
        tokenAdmin    = obterTokenAdmin();
        tokenVendedor = obterToken(emailVendedor, "vendedor123");

        // Cria o cliente uma vez — na segunda execução o POST retorna 409, então fazemos login direto
        io.restassured.response.Response resCliente = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Cliente Agenda",
                          "email": "%s",
                          "senha": "senha123",
                          "dataNascimento": "1990-01-01",
                          "role": "CLIENTE"
                        }
                        """.formatted(emailCliente))
                .when().post("/api/usuario");

        if (resCliente.statusCode() == 201) {
            idCliente = resCliente.jsonPath().getLong("id");
        } else {
            String tk = obterToken(emailCliente, "senha123");
            idCliente = given()
                    .header("Authorization", "Bearer " + tk)
                    .when().get("/api/usuario/me")
                    .then().statusCode(200)
                    .extract().jsonPath().getLong("id");
        }

        tokenCliente = obterToken(emailCliente, "senha123");

        idVendedor = given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .when().get("/api/usuario/me")
                .then().statusCode(200)
                .extract().jsonPath().getLong("id");
    }

    // ── Autorização ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST sem token retorna 403")
    void criar_semToken_retorna403() {
        given()
                .contentType(ContentType.JSON)
                .body(corpoAgenda(idCliente, "TEST_DRIVE"))
        .when()
                .post(BASE)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("POST com CLIENTE retorna 403")
    void criar_comCliente_retorna403() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body(corpoAgenda(idCliente, "TEST_DRIVE"))
        .when()
                .post(BASE)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("GET sem token retorna 403")
    void listar_semToken_retorna403() {
        given()
        .when()
                .get(BASE)
        .then()
                .statusCode(403);
    }

    // ── POST ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("VENDEDOR cria agendamento → 201")
    void criar_comVendedor_retorna201() {
        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body(corpoAgenda(idCliente, "TEST_DRIVE"))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("tipo", equalTo("TEST_DRIVE"))
                .body("status", equalTo("PENDENTE"))
                .body("colaborador.id", equalTo(idVendedor.intValue()))
                .body("cliente.id", equalTo(idCliente.intValue()));
    }

    @Test
    @DisplayName("ADMIN cria agendamento → 201")
    void criar_comAdmin_retorna201() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body(corpoAgenda(idCliente, "VISITA"))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("tipo", equalTo("VISITA"))
                .body("status", equalTo("PENDENTE"));
    }

    @Test
    @DisplayName("POST sem clienteId retorna 400")
    void criar_semClienteId_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "dataHora": "%s",
                          "tipo": "VISITA"
                        }
                        """.formatted(dataFutura()))
        .when()
                .post(BASE)
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST sem dataHora retorna 400")
    void criar_semDataHora_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "clienteId": %d,
                          "tipo": "VISITA"
                        }
                        """.formatted(idCliente))
        .when()
                .post(BASE)
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST com data passada retorna 400")
    void criar_comDataPassada_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "clienteId": %d,
                          "dataHora": "2020-01-01T10:00:00",
                          "tipo": "VISITA"
                        }
                        """.formatted(idCliente))
        .when()
                .post(BASE)
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST sem tipo retorna 400")
    void criar_semTipo_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "clienteId": %d,
                          "dataHora": "%s"
                        }
                        """.formatted(idCliente, dataFutura()))
        .when()
                .post(BASE)
        .then()
                .statusCode(400);
    }

    // ── GET ───────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("VENDEDOR lista seus agendamentos → 200")
    void listar_comVendedor_retorna200() {
        // Cria um agendamento primeiro
        criarAgendamento(tokenVendedor, idCliente, "VISITA");

        given()
                .header("Authorization", "Bearer " + tokenVendedor)
        .when()
                .get(BASE)
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    @DisplayName("ADMIN lista agendamentos → 200")
    void listar_comAdmin_retorna200() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
        .when()
                .get(BASE)
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    // ── GET /{id} ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("VENDEDOR busca agendamento criado por ele → 200")
    void buscarPorId_comVendedor_retorna200() {
        Integer id = criarAgendamento(tokenVendedor, idCliente, "TEST_DRIVE");

        given()
                .header("Authorization", "Bearer " + tokenVendedor)
        .when()
                .get(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id));
    }

    @Test
    @DisplayName("CLIENTE busca agendamento do qual é o cliente → 200")
    void buscarPorId_comCliente_retorna200() {
        Integer id = criarAgendamento(tokenVendedor, idCliente, "ENTREGA");

        given()
                .header("Authorization", "Bearer " + tokenCliente)
        .when()
                .get(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id));
    }

    @Test
    @DisplayName("GET /{id} inexistente retorna 404")
    void buscarPorId_inexistente_retorna404() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
        .when()
                .get(BASE + "/99999999")
        .then()
                .statusCode(404);
    }

    // ── PUT ───────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("VENDEDOR atualiza agendamento → 200")
    void atualizar_comVendedor_retorna200() {
        Integer id = criarAgendamento(tokenVendedor, idCliente, "VISITA");

        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body(corpoAgendaComObservacao(idCliente, "TEST_DRIVE", "Atualizado pelo vendedor"))
        .when()
                .put(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("tipo", equalTo("TEST_DRIVE"))
                .body("observacao", equalTo("Atualizado pelo vendedor"));
    }

    @Test
    @DisplayName("PUT inexistente retorna 404")
    void atualizar_inexistente_retorna404() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body(corpoAgenda(idCliente, "VISITA"))
        .when()
                .put(BASE + "/99999999")
        .then()
                .statusCode(404);
    }

    // ── PATCH /{id}/status ────────────────────────────────────────────────────

    @Test
    @DisplayName("VENDEDOR confirma agendamento → 200 com status CONFIRMADO")
    void atualizarStatus_confirmar_retorna200() {
        Integer id = criarAgendamento(tokenVendedor, idCliente, "TEST_DRIVE");

        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body("""
                        { "status": "CONFIRMADO" }
                        """)
        .when()
                .patch(BASE + "/" + id + "/status")
        .then()
                .statusCode(200)
                .body("status", equalTo("CONFIRMADO"));
    }

    @Test
    @DisplayName("VENDEDOR cancela agendamento → 200 com status CANCELADO")
    void atualizarStatus_cancelar_retorna200() {
        Integer id = criarAgendamento(tokenVendedor, idCliente, "VISITA");

        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body("""
                        { "status": "CANCELADO" }
                        """)
        .when()
                .patch(BASE + "/" + id + "/status")
        .then()
                .statusCode(200)
                .body("status", equalTo("CANCELADO"));
    }

    @Test
    @DisplayName("CLIENTE não pode alterar status → 403")
    void atualizarStatus_comCliente_retorna403() {
        Integer id = criarAgendamento(tokenVendedor, idCliente, "VISITA");

        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body("""
                        { "status": "CONFIRMADO" }
                        """)
        .when()
                .patch(BASE + "/" + id + "/status")
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("PATCH sem status retorna 400")
    void atualizarStatus_semStatus_retorna400() {
        Integer id = criarAgendamento(tokenVendedor, idCliente, "VISITA");

        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body("{}")
        .when()
                .patch(BASE + "/" + id + "/status")
        .then()
                .statusCode(400);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("VENDEDOR deleta agendamento criado por ele → 204")
    void deletar_comVendedor_retorna204() {
        Integer id = criarAgendamento(tokenVendedor, idCliente, "VISITA");

        given()
                .header("Authorization", "Bearer " + tokenVendedor)
        .when()
                .delete(BASE + "/" + id)
        .then()
                .statusCode(204);

        // Confirma que não existe mais
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
        .when()
                .get(BASE + "/" + id)
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("CLIENTE não pode deletar agendamento → 403")
    void deletar_comCliente_retorna403() {
        Integer id = criarAgendamento(tokenVendedor, idCliente, "VISITA");

        given()
                .header("Authorization", "Bearer " + tokenCliente)
        .when()
                .delete(BASE + "/" + id)
        .then()
                .statusCode(403);
    }

    // ── GET /colaborador/{id}/disponibilidade ─────────────────────────────────

    @Test
    @DisplayName("Consulta disponibilidade com token autenticado → 200 e lista")
    void disponibilidade_comToken_retorna200() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
        .when()
                .get(BASE + "/colaborador/" + idVendedor + "/disponibilidade")
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    @DisplayName("Consulta disponibilidade sem token → 403")
    void disponibilidade_semToken_retorna403() {
        given()
        .when()
                .get(BASE + "/colaborador/" + idVendedor + "/disponibilidade")
        .then()
                .statusCode(403);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private String corpoAgenda(Long clienteId, String tipo) {
        return """
                {
                  "clienteId": %d,
                  "dataHora": "%s",
                  "tipo": "%s"
                }
                """.formatted(clienteId, dataFutura(), tipo);
    }

    private String corpoAgendaComObservacao(Long clienteId, String tipo, String observacao) {
        return """
                {
                  "clienteId": %d,
                  "dataHora": "%s",
                  "tipo": "%s",
                  "observacao": "%s"
                }
                """.formatted(clienteId, dataFutura(), tipo, observacao);
    }

    private Integer criarAgendamento(String token, Long clienteId, String tipo) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpoAgenda(clienteId, tipo))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    /** Retorna uma data/hora futura única para evitar conflito de horário no colaborador. */
    private String dataFutura() {
        // Offset aleatório entre 1 e 999 dias para evitar colisão entre testes paralelos
        long diasOffset = 30L + (long)(Math.random() * 970);
        return LocalDateTime.now().plusDays(diasOffset).withNano(0).format(FMT);
    }
}
