package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.RequestAuthDto;
import br.com.sysmap.bootcamp.dto.ResponseAuthDto;
import br.com.sysmap.bootcamp.dto.ResponseUserDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    private final WalletService walletService;

    @Operation(summary = "Save user")
    @PostMapping("/create")
    public ResponseEntity<ResponseUserDto> save(@RequestBody Users user) {
        return ResponseEntity.ok(this.usersService.create(user,walletService));
    }

    @PostMapping("/auth")
    public ResponseEntity<ResponseAuthDto> auth(@RequestBody RequestAuthDto user) {
        return ResponseEntity.ok(this.usersService.auth(user));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseUserDto> update(@RequestBody Users user) {
        return ResponseEntity.ok(usersService.update(user));
    }


    @GetMapping
    public ResponseEntity<Page<ResponseUserDto>> getAll(Pageable pg){
        return ResponseEntity.ok(usersService.findAll(pg));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(usersService.findById(id));
    }
}
