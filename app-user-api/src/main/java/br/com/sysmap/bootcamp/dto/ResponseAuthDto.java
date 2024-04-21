package br.com.sysmap.bootcamp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResponseAuthDto {
    private String email;
    private Long id;
    private String token;
}
