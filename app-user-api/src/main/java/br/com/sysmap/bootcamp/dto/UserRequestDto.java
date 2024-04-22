package br.com.sysmap.bootcamp.dto;

import br.com.sysmap.bootcamp.domain.entities.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRequestDto{

    private String name;
    private String email;
    private String password;

    public UserRequestDto(Users user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
}
