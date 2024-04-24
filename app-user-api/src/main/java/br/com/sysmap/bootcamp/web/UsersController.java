package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "user created"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "422",description = "invalid fields in request body"),
            @ApiResponse(responseCode = "500",description = "internal failure to save user")
    })
    public ResponseEntity<ResponseUserDto> save(@ParameterObject @Valid @RequestBody UserRequestDto user, HttpServletRequest http) {
        ResponseUserDto createdUser = usersService.create(user,walletService);
        URI uri = UriComponentsBuilder.fromUriString(http.getRequestURI()).path("/{id}").buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @Operation(summary = "auth user")
    @PostMapping("/auth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "user authenticated"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "422",description = "invalid fields in request body"),
            @ApiResponse(responseCode = "500",description = "internal failure to auth user")
    })
    public ResponseEntity<ResponseAuthDto> auth(@ParameterObject @Valid @RequestBody RequestAuthDto user) {
        return ResponseEntity.ok(usersService.auth(user));
    }

    @Operation(summary = "update user")
    @PutMapping("/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "user updated"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "422",description = "invalid fields in request body"),
            @ApiResponse(responseCode = "404",description = "user to update not found"),
            @ApiResponse(responseCode = "500",description = "internal failure update user")
    })
    public ResponseEntity<ResponseUserDto> update(@ParameterObject @Valid @RequestBody UserUpdateRequestDto user) {
        return ResponseEntity.ok(usersService.update(user));
    }

    @Operation(summary = "list users")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "all users returned"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "500",description = "internal failure to get users")
    })
    public ResponseEntity<Page<ResponseUserDto>> getAll(@ParameterObject Pageable pg){
        return ResponseEntity.ok(usersService.findAll(pg));
    }

    @Operation(summary = "get user by id")
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "returned user successful"),
            @ApiResponse(responseCode = "400",description = "invalid parameter"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "404",description = "user not found"),
            @ApiResponse(responseCode = "500",description = "internal failure to get user")
    })
    public ResponseEntity<ResponseUserDto> getById(@Parameter(description = "user id") @PathVariable Long id) {
        return ResponseEntity.ok(usersService.findById(id));
    }
}
