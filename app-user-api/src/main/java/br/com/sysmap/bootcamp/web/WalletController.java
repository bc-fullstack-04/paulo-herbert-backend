package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.ResponseWalletDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<ResponseWalletDto> getWallet() {
        return ResponseEntity.ok(walletService.getByUser());
    }

    @PostMapping("/credit/{value}")
    public ResponseEntity<ResponseWalletDto> insertCredit(@PathVariable("value") BigDecimal value) {
        return  ResponseEntity.ok(walletService.insertCredit(value));
    }
}
