package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.exceptions.customs.InvalidCredentials;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UsersService userService;

    @Mock
    private UsersRepository usersRepository;

    @Test
    @DisplayName("should return a user")
    public void shouldReturnUserByUsername() {
        String username = "user@example.com";
        Users user = Users.builder().email("user@example.com").password("password123").build();
        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("user@example.com");

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        String username = "nonexistent@example.com";
        when(usersRepository.findByEmail(username)).thenReturn(Optional.empty());
        assertThrows(InvalidCredentials.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    @DisplayName("should return current authenticated user")
    void shouldReturnCurrentUser(){
        String loggedUserName = "paulo@mail.com";
        Users user = Users.builder().id(1L).password("Herbert").email("paulo@mail.com").build();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(loggedUserName);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(usersRepository.findByEmail(loggedUserName)).thenReturn(Optional.of(user));

        Users result = userService.getAuthenticatedUser();
        assertNotNull(result);
        assertEquals(loggedUserName, result.getEmail());
    }
}
