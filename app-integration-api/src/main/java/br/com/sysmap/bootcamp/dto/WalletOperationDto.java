package br.com.sysmap.bootcamp.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class WalletOperationDto implements Serializable {

    private String userEmail;
    private BigDecimal price;

    public WalletOperationDto(String email, BigDecimal price) {
        this.userEmail = email;
        this.price = price;
    }

}
