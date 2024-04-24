package br.com.sysmap.bootcamp.util;

import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.listeners.WalletListener;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.WalletOperationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class RabbitReceiverTest {

    @InjectMocks
    private WalletListener walletListener;
    @Mock
    private WalletService walletService;

    @Test
    void listenerShouldDebit(){
        WalletOperationDto dto = new WalletOperationDto("teste@example.com", BigDecimal.valueOf(50));
        when(walletService.debit(dto)).thenReturn(new Wallet());
        assertDoesNotThrow(()->walletListener.receive(dto));
    }

}
