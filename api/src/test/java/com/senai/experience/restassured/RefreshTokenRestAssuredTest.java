package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fluxo completo do refresh token com rotação (S-Card 1):
 * login → refresh (rotação) → logout → reuso → 401.
 *
 * <p>Monta as requisições diretamente (não usa {@code obterToken}, que só devolve o
 * campo {@code token}), pois precisa extrair também o {@code refreshToken}.</p>
 */
@DisplayName("Refresh token com rotação — testes de integração (REST Assured)")
class RefreshTokenRestAssuredTest extends RestAssuredBaseTest {

    private io.restassured.response.Response login(String email, String senha) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        { "email": "%s", "senha": "%s" }
                        """.formatted(email, senha))
        .when()
                .post("/api/usuario/login");
    }

    private io.restassured.response.Response refresh(String refreshToken) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        { "refreshToken": "%s" }
                        """.formatted(refreshToken))
        .when()
                .post("/auth/refresh");
    }

    @Test
    @DisplayName("Login retorna access token e refresh token")
    void loginRetornaAccessERefresh() {
        var resp = login(ADMIN_EMAIL, ADMIN_SENHA).then().statusCode(200).extract();
        assertThat(resp.jsonPath().getString("token")).isNotBlank();
        assertThat(resp.jsonPath().getString("refreshToken")).isNotBlank();
    }

    @Test
    @DisplayName("Fluxo completo: login → refresh (rotação) → reuso 401 → logout → reuso 401")
    void fluxoCompletoLoginRefreshLogoutReuso() {
        // 1. Login
        var loginResp = login(ADMIN_EMAIL, ADMIN_SENHA).then().statusCode(200).extract();
        String r1 = loginResp.jsonPath().getString("refreshToken");
        assertThat(r1).isNotBlank();

        // 2. Refresh com r1 → 200, novo par; rotação emite refresh diferente
        var refreshResp = refresh(r1).then().statusCode(200).extract();
        String token2 = refreshResp.jsonPath().getString("token");
        String r2 = refreshResp.jsonPath().getString("refreshToken");
        assertThat(token2).isNotBlank();
        assertThat(r2).isNotBlank();
        assertThat(r2).isNotEqualTo(r1);

        // 3. Reusar r1 (já rotacionado/revogado) → 401
        refresh(r1).then().statusCode(401);

        // 4. Logout com o access token atual e o refresh atual (r2) → 200
        given()
                .header("Authorization", "Bearer " + token2)
                .contentType(ContentType.JSON)
                .body("""
                        { "refreshToken": "%s" }
                        """.formatted(r2))
        .when()
                .post("/api/usuario/logout")
        .then()
                .statusCode(200);

        // 5. Usar r2 após o logout (revogado) → 401
        refresh(r2).then().statusCode(401);
    }

    @Test
    @DisplayName("Refresh com token inexistente retorna 401")
    void refreshTokenInexistenteRetorna401() {
        refresh("token-que-nunca-foi-emitido").then().statusCode(401);
    }
}
