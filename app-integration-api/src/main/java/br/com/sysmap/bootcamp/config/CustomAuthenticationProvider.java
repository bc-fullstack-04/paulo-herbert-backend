package br.com.sysmap.bootcamp.config;

import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.exceptions.customs.InvalidCredentials;
import br.com.sysmap.bootcamp.exceptions.customs.StandardError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UsersService userDetailsService;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest req;
    private final HttpServletResponse res;

    public CustomAuthenticationProvider(UsersService userDetailsService, ObjectMapper objectMapper, HttpServletRequest req, HttpServletResponse res) {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.req = req;
        this.res = res;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),userDetails.getPassword(),userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
