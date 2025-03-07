package br.com.alexcosta.alexcosta.controllers.handler;

import br.com.alexcosta.alexcosta.dto.CustonError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
@RestController
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class); // Adicionando logger

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustonError> tratarErro400(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var erros = ex.getFieldErrors();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustonError err = new CustonError(Instant.now(), status.value(), "Bad Request", request.getRequestURI(), ex.getLocalizedMessage());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustonError> tratarErro401(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        CustonError err = new CustonError(Instant.now(), status.value(), "Unauthorized", request.getRequestURI(), e.getLocalizedMessage());
        logger.error("Erro 401: {}", e.getLocalizedMessage()); // Usando logger para erros
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustonError> tratarErro403(AccessDeniedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        CustonError err = new CustonError(Instant.now(), status.value(), "Forbidden", request.getRequestURI(), e.getLocalizedMessage());
        logger.error("Erro 403: {}", e.getLocalizedMessage());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustonError> tratarErro404(HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustonError err = new CustonError(Instant.now(), status.value(), "Resource Not Found", request.getRequestURI(), "");
        logger.error("Erro 404: Recurso não encontrado na URI: {}", request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustonError> tratarErro405(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        CustonError err = new CustonError(Instant.now(), status.value(), "Method Not Allowed", request.getRequestURI(), e.getLocalizedMessage());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<CustonError> tratarErro406(HttpMediaTypeNotAcceptableException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
        CustonError err = new CustonError(Instant.now(), status.value(), "Not Acceptable", request.getRequestURI(), e.getLocalizedMessage());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<CustonError> tratarErroGenerico(HttpServerErrorException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(e.getRawStatusCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String statusText = status.getReasonPhrase();
        String message = e.getLocalizedMessage();

        CustonError err = new CustonError(Instant.now(), status.value(), statusText, request.getRequestURI(), message);
        return ResponseEntity.status(status).body(err);
    }
}
