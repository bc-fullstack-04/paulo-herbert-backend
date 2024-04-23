package br.com.sysmap.bootcamp.config;

import br.com.sysmap.bootcamp.exceptions.customs.StandardError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.time.Instant;

@ControllerAdvice
@AllArgsConstructor
public class SecurityExceptionHandler implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().print(objectMapper.writeValueAsString(new StandardError(Instant.now(), HttpStatus.UNAUTHORIZED.value(),
                "User Not Found",authException.getMessage(),request.getRequestURI())));
    }
}
