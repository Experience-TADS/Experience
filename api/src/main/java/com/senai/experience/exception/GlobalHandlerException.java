package com.senai.experience.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handlerRuntimeException(RuntimeException ex) {
        ErrorResponse e = new ErrorResponse(
            404,
            "Cliente não encontrado",
            ex.getMessage()
        );
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("transição inválida")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse erro = new ErrorResponse(
            400,
            "Requisição inválida",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .findFirst()
            .orElse("Erro de validação");

        ErrorResponse erro = new ErrorResponse(400, "Dados inválidos", mensagem);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /**
     * Trata violações de constraints de unicidade (UNIQUE) no banco de dados.
     *
     * <p>Identifica o campo violado a partir da mensagem da causa raiz e retorna
     * uma mensagem amigável específica para {@code email} e {@code chassi}.
     * Para outras constraints, retorna uma mensagem genérica.</p>
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        String causa = ex.getMostSpecificCause().getMessage();
        String detalhe;

        if (causa != null && causa.toLowerCase().contains("email")) {
            detalhe = "Já existe um usuário cadastrado com este e-mail.";
        } else if (causa != null && causa.toLowerCase().contains("chassi")) {
            detalhe = "Já existe um veículo cadastrado com este chassi.";
        } else {
            detalhe = "Violação de integridade: valor duplicado ou restrição violada.";
        }

        ErrorResponse erro = new ErrorResponse(409, "Conflito de dados", detalhe);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse erro = new ErrorResponse(
            403,
            "Acesso negado",
            "Você não tem permissão para acessar este recurso."
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse erro = new ErrorResponse(
            500,
            "Erro interno",
            "Ocorreu um erro inesperado. Tente novamente."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
