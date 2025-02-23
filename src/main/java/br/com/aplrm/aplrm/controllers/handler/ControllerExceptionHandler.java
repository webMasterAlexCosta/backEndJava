package br.com.aplrm.aplrm.controllers.handler;

import br.com.aplrm.aplrm.dto.CustonError;
import br.com.aplrm.aplrm.services.exceptions.DataBaseException;
import br.com.aplrm.aplrm.services.exceptions.ResourceNotFoundExceptions;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@ControllerAdvice
@RestController
public class ControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundExceptions.class)
    public ResponseEntity<CustonError> custonName(ResourceNotFoundExceptions e, HttpServletRequest request){
    HttpStatus status= HttpStatus.NOT_FOUND;
    CustonError err = new CustonError(Instant.now(), status.value(),e.getMessage(),request.getRequestURI(),e.getLocalizedMessage());
    return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<CustonError> dataBase(DataBaseException e, HttpServletRequest request){
        HttpStatus status= HttpStatus.BAD_REQUEST;
        CustonError err = new CustonError(Instant.now(), status.value(),e.getMessage(),request.getRequestURI(),e.getLocalizedMessage());
        return ResponseEntity.status(status).body(err);
    }
}
