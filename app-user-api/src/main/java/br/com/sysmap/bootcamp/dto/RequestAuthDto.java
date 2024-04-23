package br.com.sysmap.bootcamp.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RequestAuthDto {

    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;

}
