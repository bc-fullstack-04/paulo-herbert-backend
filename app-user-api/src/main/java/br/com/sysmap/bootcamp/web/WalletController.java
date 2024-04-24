package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.ResponseWalletDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "get my wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "user wallet retrieved"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "500",description = "internal failure to get user wallet")
    })
    public ResponseEntity<ResponseWalletDto> getWallet() {
        return ResponseEntity.ok(walletService.getByUser());
    }

    @PostMapping("/credit/{value}")
    @Operation(summary = "credit value in wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "insert credits successfully"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "422",description = "invalid parameter"),
            @ApiResponse(responseCode = "500",description = "internal failure updating user wallet")
    })
    public ResponseEntity<ResponseWalletDto> insertCredit(@PathVariable("value") BigDecimal value) {
        return  ResponseEntity.ok(walletService.insertCredit(value));
    }
}
