package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.dto.*;
import br.com.sysmap.bootcamp.exceptions.customs.IllegalArgsRequestException;
import br.com.sysmap.bootcamp.exceptions.customs.InvalidCredentials;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UsersServiceTest {

    @InjectMocks
    private UsersService usersService;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private WalletService walletService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Create Should return users when valid users is saved")
    public void createShouldReturnUsersWhenValidUsersIsSaved() {
        Users users = Users.builder().id(1L).name("teste").email("test@mail.com").password("teste").build();
        when(usersRepository.save(any(Users.class))).thenReturn(users);
        when(passwordEncoder.encode(any())).thenReturn("encodedMockpassword");
        ResponseUserDto savedUsers = usersService.create(new UserRequestDto(users), walletService);
        assertEquals(savedUsers.getId(), users.getId());
        assertEquals(savedUsers.getName(), users.getName());
        assertEquals(savedUsers.getEmail(), users.getEmail());
        verify(walletService).saveWallet(any());
    }

    @Test
    @DisplayName("create Should Throw Exception when user email already exists")
    public void createShouldThrowExceptionWhenInValidUsers() {
        Users invalidUsers = Users.builder().id(1L).name("teste").email("test@mail.com").password("teste").build();
        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(invalidUsers));
        when(usersRepository.save(any(Users.class))).thenReturn(invalidUsers);
        when(passwordEncoder.encode(any())).thenReturn("encodedMockpassword");
        assertThrows(IllegalArgsRequestException.class, () -> usersService.create(new UserRequestDto(invalidUsers), walletService));
    }

    @Test
    @DisplayName("update Should Throw Exception when user email already exists")
    public void updateShouldThrowExceptionWhenInValidUsers() {
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(1L,"loggedUser", "test123@mail.com", "teste");
        Users userToUpdate = Users.builder().id(1L).email("test@mail.com").password("test123").build();
        Users existentUser = Users.builder().id(2L).name("existent user").email("test123@mail.com").password("teste").build();

        when(usersRepository.findById(userUpdateRequestDto.getId())).thenReturn(Optional.of(userToUpdate));
        when(usersRepository.findByEmail(userUpdateRequestDto.getEmail())).thenReturn(Optional.of(existentUser));

        verify(usersRepository,never()).save(any());
        verify(passwordEncoder,never()).encode(any());
        assertThrows(IllegalArgsRequestException.class, () -> usersService.update(userUpdateRequestDto));
    }

    @Test
    @DisplayName("update Should Return Updated userDto when user is valid")
    public void updateShouldReturnUserDtoWhenValidUsers() {
        Users userRequest = Users.builder().id(1L).name("user").email("test123@mail.com").password("131718").build();
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(1L,"user", "test123@mail.com", "teste");
        when(usersRepository.findById(userUpdateRequestDto.getId())).thenReturn(Optional.of(userRequest));
        when(usersRepository.findByEmail(userUpdateRequestDto.getEmail())).thenReturn(Optional.empty());
        when(usersRepository.save(any())).thenReturn(userRequest);

        ResponseUserDto resp = usersService.update(userUpdateRequestDto);
        assertEquals(userUpdateRequestDto.getName(), resp.getName());
        assertEquals(userUpdateRequestDto.getEmail(), resp.getEmail());
        assertEquals(userUpdateRequestDto.getId(), resp.getId());
        verify(usersRepository).save(any());
    }

    @Test
    @DisplayName("find all should return all users paged")
    public void findAllShouldReturnPagedUsers() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        List<Users> usersList = Arrays.asList(
                new Users(1L, "bacurun", "tt@example.com", "senaha"),
                new Users(2L, "ampaboru", "thismy@example.com", "sebga")
        );
        Page<Users> usersPage = new PageImpl<>(usersList);
        when(usersRepository.findAll(pageable)).thenReturn(usersPage);
        Page<ResponseUserDto> responsePage = usersService.findAll(pageable);

        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals("bacurun", responsePage.getContent().get(0).getName());
        assertEquals("thismy@example.com", responsePage.getContent().get(1).getEmail());
    }

    @Test
    @DisplayName("find by id should return a user")
    public void findByIdShouldReturnUser() {
        Long userId = 1L;
        Users user = new Users(userId, "John Doe", "john.doe@example.com", "PASS");
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        ResponseUserDto responseUserDto = usersService.findById(userId);
        assertNotNull(responseUserDto);
        assertEquals(userId, responseUserDto.getId());
        assertEquals("John Doe", responseUserDto.getName());
        assertEquals("john.doe@example.com", responseUserDto.getEmail());
    }

    @Test
    @DisplayName("find by id should throw Entity Not Found Exception")
    public void testFindByIdShouldThrowEntityNotfound() {
        Long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> usersService.findById(userId));
    }

    @Test
    @DisplayName("loadUserByUsernameShouldReturnUserDetails")
    public void loadUserByUsernameShouldReturnUserDetails() {
        Users user = new Users(null, "teste", "test@example.com", "password");
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserDetails userDetails = usersService.loadUserByUsername("test@example.com");
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("loadUserByUsernameShould Throw InvalidCredentials")
    public void testLoadUserByUsernameUserNotFound() {
        when(usersRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(InvalidCredentials.class, () -> {
            usersService.loadUserByUsername("notfound@example.com");
        });
    }

    @Test
    @DisplayName("find by email Should Return User")
    public void findByEmailShouldReturnUser() {
        Users user = new Users(null, "teste", "test@example.com", "password");
        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Users foundUser = usersService.findByEmail(user.getEmail());
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        assertEquals("password", foundUser.getPassword());
    }

    @Test
    @DisplayName("find By Email Should Throw EntityNotFound")
    public void findByEmailShouldReturnEntityNotFound() {
        when(usersRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            usersService.findByEmail("notfound@example.com");
        });
    }

    @Test
    @DisplayName("Auth should Return ResponseAuthDto")
    public void authShouldReturnResponseAuthDtoWhenRightCredentials() {
        RequestAuthDto req = RequestAuthDto.builder().email("teste@example.com").password("password").build();
        Users existentUser = new Users(1L, "teste", "teste@example.com", "password");
        when(usersRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(existentUser));
        when(passwordEncoder.matches(req.getPassword(), existentUser.getPassword())).thenReturn(true);
        String password = "teste@example.com:password";
        String expectedToken = Base64.getEncoder().withoutPadding().encodeToString(password.getBytes());

        ResponseAuthDto resp = usersService.auth(req);
        assertNotNull(resp);
        assertEquals(resp.getId(), existentUser.getId());
        assertEquals(resp.getEmail(), req.getEmail());
        assertEquals(expectedToken, resp.getToken());
    }

    @Test
    @DisplayName("Auth should throw InvalidCredentials ")
    public void authShouldThrowBadCredentialsWhenInvalidCredentials() {
        RequestAuthDto req = new RequestAuthDto("teste@example.com", "password");
        Users existentUser = new Users(1L, "teste", "teste@example.com", "password");
        when(usersRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(existentUser));
        when(passwordEncoder.matches(req.getPassword(), existentUser.getPassword())).thenReturn(false);
        String password = "teste@example.com:password";
        String expectedToken = Base64.getEncoder().withoutPadding().encodeToString(password.getBytes());
        assertThrows(InvalidCredentials.class, () -> {
            usersService.auth(req);
        });
    }

    @Test
    void shouldReturnLoggedUser(){
        String loggedUserName = "paulo@mail.com";
        Users user = Users.builder().id(1L).name("Paulo").password("Herbert").email("paulo@mail.com").build();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(loggedUserName);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(usersRepository.findByEmail(loggedUserName)).thenReturn(Optional.of(user));
        Users result = usersService.getLoggedUser();
        assertNotNull(result);
        assertEquals(loggedUserName, result.getEmail());
    }
}