package br.com.sysmap.bootcamp.dto;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RequestAuthDto {

    private String email;
    private String password;

}
