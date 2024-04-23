package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    private final WalletService walletService;

    @Operation(summary = "save user")
    @PostMapping("/create")
    public ResponseEntity<ResponseUserDto> save(@ParameterObject @Valid @RequestBody UserRequestDto user, HttpServletRequest http) {
        ResponseUserDto createdUser = usersService.create(user,walletService);
        URI uri = UriComponentsBuilder.fromUriString(http.getRequestURI()).path("/{id}").buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }
    @Operation(summary = "auth user")
    @PostMapping("/auth")
    public ResponseEntity<ResponseAuthDto> auth(@ParameterObject @Valid @RequestBody RequestAuthDto user) {
        return ResponseEntity.ok(usersService.auth(user));
    }
    @Operation(summary = "update user")
    @PutMapping("/update")
    public ResponseEntity<ResponseUserDto> update(@ParameterObject @Valid @RequestBody UserUpdateRequestDto user) {
        return ResponseEntity.ok(usersService.update(user));
    }

    @Operation(summary = "list users")
    @GetMapping
    public ResponseEntity<Page<ResponseUserDto>> getAll(@ParameterObject Pageable pg){
        return ResponseEntity.ok(usersService.findAll(pg));
    }

    @Operation(summary = "get user by id")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(usersService.findById(id));
    }
}
