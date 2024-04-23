package br.com.sysmap.bootcamp.dto;

import br.com.sysmap.bootcamp.domain.entities.Users;
import lombok.*;

@AllArgsConstructor
@Getter
public class ResponseUserDto {

    private Long id;
    private String name;
    private String email;

    public ResponseUserDto(Users user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
