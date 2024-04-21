package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.dto.ResponseUserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();
        when(usersRepository.save(any(Users.class))).thenReturn(users);
        when(passwordEncoder.encode(any())).thenReturn("encodedMockpassword");
        ResponseUserDto savedUsers = usersService.create(users,walletService);
        assertEquals(savedUsers.getId(),users.getId());
        assertEquals(savedUsers.getName(),users.getName());
        assertEquals(savedUsers.getEmail(),users.getEmail());
    }





}
