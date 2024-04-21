package br.com.sysmap.bootcamp.dto;

import br.com.sysmap.bootcamp.domain.entities.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class ResponseWalletDto {

    private Long id;
    private BigDecimal balance;
    private Long points;
    private LocalDateTime lastUpdate;
    private ResponseUserDto user;

    public ResponseWalletDto(Wallet wallet) {
        this.id = wallet.getId();
        this.balance = wallet.getBalance();
        this.points = wallet.getPoints();
        this.lastUpdate = wallet.getLastUpdate();
        this.user = new ResponseUserDto(wallet.getUsers());
    }
}
