package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UsersRepository usersRepository;

    @Test
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("test@mail.com").password("teste").build();
        when(usersService.create(any(),any())).thenReturn(new ResponseUserDto(users));
        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequestDto(users))).characterEncoding(Charset.defaultCharset()))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/users/create/"+users.getId()));
    }

    @Test
    @DisplayName("auth should return responseAuthDto")
    public void authShouldReturnResponseAuth() throws Exception {
        RequestAuthDto requestAuthDto = new RequestAuthDto("email@teste.com", "password");
        ResponseAuthDto responseAuthDto = new ResponseAuthDto("email@teste.com",1L,"tokenexample");
        when(usersService.auth(any())).thenReturn(responseAuthDto);
        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAuthDto)).characterEncoding(Charset.defaultCharset()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("tokenexample"))
                .andExpect(jsonPath("$.email").value("email@teste.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("update should return ok")
    public void updateShouldReturnOk() throws Exception {
        ResponseUserDto responseUserDto = new ResponseUserDto(1L,"John","jhon@updated.com");
        when(usersService.update(any())).thenReturn(responseUserDto);
        mockMvc.perform(put("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserUpdateRequestDto(1L,"John","jhon@updated.com","pass"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("jhon@updated.com"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    @DisplayName("find all should return ok")
    public void findAllShouldReturnOk() throws Exception {
        ResponseUserDto userDto = new ResponseUserDto(1L,"John","emailteste@");
        when(usersService.findAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(userDto)));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(userDto.getId()));
    }

    @Test
    @DisplayName("find by id should return ok")
    public void findByIdShouldReturnOk() throws Exception {
        Long userId = 1L;
        ResponseUserDto userDto = new ResponseUserDto(1L,"John","emailteste@");
        when(usersService.findById(userId)).thenReturn(userDto);
        mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("emailteste@"));
    }
}

