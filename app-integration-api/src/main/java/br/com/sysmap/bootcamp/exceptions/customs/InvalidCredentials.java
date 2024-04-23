package br.com.sysmap.bootcamp.exceptions.customs;

import org.springframework.security.core.AuthenticationException;

public class InvalidCredentials extends AuthenticationException {
    public InvalidCredentials(String message) {
        super(message);
    }
}
