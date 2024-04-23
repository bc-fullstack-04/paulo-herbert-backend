package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.RequestAuthDto;
import br.com.sysmap.bootcamp.dto.ResponseAuthDto;
import br.com.sysmap.bootcamp.dto.ResponseUserDto;
import br.com.sysmap.bootcamp.dto.UserRequestDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() throws Exception {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();

        when(usersService.create(new UserRequestDto(users),walletService)).thenReturn(new ResponseUserDto(users));

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(users)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("auth should return responseAuthDto")
    public void authShouldReturnResponseAuth() throws Exception {
        RequestAuthDto requestAuthDto = new RequestAuthDto("email", "password");
        ResponseAuthDto responseAuthDto = new ResponseAuthDto("email",1L,"tokenexample");
        when(usersService.auth(requestAuthDto)).thenReturn(responseAuthDto);
        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAuthDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("update should return ok")
    public void updateShouldReturnOk() throws Exception {
        UserRequestDto userRequestDto = new UserRequestDto("John","teste@exampl.ecom","pass");
        ResponseUserDto responseUserDto = new ResponseUserDto(1L,"John","jhonupdated.com");
        when(usersService.update(userRequestDto)).thenReturn(responseUserDto);
        mockMvc.perform(put("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)));
    }

    @Test
    @DisplayName("find all should return ok")
    public void findAllShouldReturnOk() throws Exception {
        Pageable pageable = PageRequest.of(0,10);
        ResponseUserDto userDto = new ResponseUserDto(1L,"John","emailteste@");
        when(usersService.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(userDto)));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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

