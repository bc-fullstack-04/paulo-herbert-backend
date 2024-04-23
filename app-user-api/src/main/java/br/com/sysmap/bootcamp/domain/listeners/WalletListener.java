package br.com.sysmap.bootcamp.domain.listeners;


import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.WalletOperationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = "WalletQueue")
public class WalletListener {

    private final WalletService walletService;

    @RabbitHandler
    public void receive(WalletOperationDto walletDto) {
        log.info("Received message : debit wallet");
        walletService.debit(walletDto);
    }


}
