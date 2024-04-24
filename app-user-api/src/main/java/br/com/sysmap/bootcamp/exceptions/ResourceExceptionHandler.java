package br.com.sysmap.bootcamp.exceptions;

import br.com.sysmap.bootcamp.exceptions.customs.IllegalArgsRequestException;
import br.com.sysmap.bootcamp.exceptions.customs.InvalidCredentials;
import br.com.sysmap.bootcamp.exceptions.customs.StandardError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    private final HttpServletRequest http;

    public ResourceExceptionHandler(HttpServletRequest http) {
        this.http = http;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new StandardError(Instant.now(),HttpStatus.NOT_FOUND.value()
                        ,"Entity Not Found", e.getMessage(), http.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgsRequestException.class)
    public ResponseEntity<StandardError> handleIllegalArgsRequestException(IllegalArgsRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new StandardError(Instant.now(),HttpStatus.BAD_REQUEST.value(), "Illegal Argument in Request", e.getMessage(), http.getRequestURI())
        );
    }

    @ExceptionHandler(InvalidCredentials.class)
    public ResponseEntity<StandardError> handleBadCredentialsException(InvalidCredentials e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new StandardError(Instant.now(),HttpStatus.UNAUTHORIZED.value(), "Invalid credentials", e.getMessage(), http.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validationException(MethodArgumentNotValidException exc){
        StringBuilder sb = new StringBuilder();
        exc.getBindingResult().getFieldErrors().
                forEach(x->sb.append(String.format("[%s: %s], ",x.getField(),x.getDefaultMessage())));

        StandardError error = new StandardError(Instant.now(),HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation Exception",sb.toString(),http.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }
}
