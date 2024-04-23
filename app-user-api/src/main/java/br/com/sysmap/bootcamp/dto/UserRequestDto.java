package br.com.sysmap.bootcamp.dto;

import br.com.sysmap.bootcamp.domain.entities.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRequestDto{

    @NotBlank
    private String name;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;

    public UserRequestDto(Users user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
}
