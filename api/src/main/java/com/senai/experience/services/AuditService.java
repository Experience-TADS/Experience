package com.senai.experience.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Serviço de auditoria de acesso — S-010.
 *
 * Usa o logger "AUDIT", mapeado exclusivamente para logs/audit.log
 * via logback-spring.xml.
 *
 * Critérios de aceite atendidos:
 * - Log de tentativas de login (sucesso e falha) com IP e timestamp
 * - Log de acessos negados (403) com usuário e recurso desejado
 * - Log de alterações em dados sensíveis (senha, função)
 * - Logs NÃO contêm dados sensíveis: senhas e tokens NUNCA são registrados
 */
@Service
public class AuditService {

    private static final Logger audit = LoggerFactory.getLogger("AUDIT");

    // ------------------------------------------------------------------ //
    // Autenticação                                                         //
    // ------------------------------------------------------------------ //

    public void logLoginSucesso(String email, String ip) {
        audit.info("LOGIN_SUCESSO | usuario={} | ip={}", email, ip);
    }

    public void logLoginFalha(String email, String ip) {
        audit.info("LOGIN_FALHA | usuario={} | ip={}", email, ip);
    }

    public void logLogout(String email) {
        audit.info("LOGOUT | usuario={}", email);
    }

    // ------------------------------------------------------------------ //
    // Controle de acesso                                                   //
    // ------------------------------------------------------------------ //

    public void logAcessoNegado(String usuario, String metodo, String recurso, String ip) {
        audit.info("ACESSO_NEGADO | usuario={} | metodo={} | recurso={} | ip={}",
                usuario, metodo, recurso, ip);
    }

    // ------------------------------------------------------------------ //
    // Alterações em dados sensíveis                                        //
    // ------------------------------------------------------------------ //

    /** A nova senha NUNCA é registrada — apenas o evento. */
    public void logAlteracaoSenha(String realizadoPor, String alvo) {
        audit.info("ALTERACAO_SENHA | realizadoPor={} | alvo={}", realizadoPor, alvo);
    }

    public void logAlteracaoRole(String realizadoPor, String alvo, String novaRole) {
        audit.info("ALTERACAO_ROLE | realizadoPor={} | alvo={} | novaRole={}",
                realizadoPor, alvo, novaRole);
    }

    public void logAlteracaoSensivel(String evento, String realizadoPor, String detalhe) {
        audit.info("{} | realizadoPor={} | detalhe={}", evento, realizadoPor, detalhe);
    }
}
