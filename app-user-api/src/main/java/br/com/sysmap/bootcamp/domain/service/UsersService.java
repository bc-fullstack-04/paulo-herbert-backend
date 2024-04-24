package br.com.sysmap.bootcamp.domain.service;


import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.dto.*;
import br.com.sysmap.bootcamp.exceptions.customs.IllegalArgsRequestException;
import br.com.sysmap.bootcamp.exceptions.customs.InvalidCredentials;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseUserDto create(UserRequestDto userDto, @Autowired WalletService walletService) {
        Optional<Users> usersOptional = usersRepository.findByEmail(userDto.getEmail());
        if (usersOptional.isPresent()) {
            throw new IllegalArgsRequestException("email already registered");
        }
        Users userEntity = usersRepository.save(Users.builder().name(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword())).email(userDto.getEmail()).build());
        log.info("Saving user: {}",userEntity );
        Wallet wallet = walletService.saveWallet(Wallet.builder().balance(BigDecimal.valueOf(100)).points(0L).users(userEntity).build());
        log.info("Creating wallet: {}",wallet);
        return new ResponseUserDto(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseUserDto update(UserUpdateRequestDto userDto) {
        Users userEntity = usersRepository.findById(userDto.getId())
                .orElseThrow(()-> new EntityNotFoundException("User with id " + userDto.getId() + " not found"));
        if(!isEmailAlreadyRegistered(userEntity,userDto)){
            log.info("Updating user: {}", userEntity);
            copyDtoToEntity(userEntity,userDto);
        }
        return new ResponseUserDto(usersRepository.save(userEntity));
    }
    public ResponseAuthDto auth(RequestAuthDto requestAuthDto) {
        Users users = this.findByEmail(requestAuthDto.getEmail());

        if (!this.passwordEncoder.matches(requestAuthDto.getPassword(), users.getPassword())) {
            throw new InvalidCredentials("Invalid password");
        }
        log.info("Authenticating user: {}",users );
        String password = users.getEmail().concat(":").concat(users.getPassword());

        return ResponseAuthDto.builder().email(users.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.getBytes())
        ).id(users.getId()).build();
    }

    public Page<ResponseUserDto> findAll(Pageable pg) {
        log.info("Getting all users");
        return (usersRepository.findAll(pg).map(ResponseUserDto::new));
    }

    public ResponseUserDto findById(Long id) {
        return new ResponseUserDto(usersRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("User not found")));
    }

    private boolean isEmailAlreadyRegistered(Users existentUser, UserUpdateRequestDto userDto){
        Optional<Users> usersOptional = usersRepository.findByEmail(userDto.getEmail());
        if(usersOptional.isPresent()){
            if(!usersOptional.get().getId().equals(existentUser.getId()))
                throw new IllegalArgsRequestException("User with email " + userDto.getEmail() + " already exists");
        }
        return false;
    }

    public Users getLoggedUser() {
        String loggedUserName = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.usersRepository.findByEmail(loggedUserName).orElseThrow(()->new EntityNotFoundException("user not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Users user = usersRepository.findByEmail(username).orElseThrow(()->new InvalidCredentials("User not found"));
        return new User(user.getEmail(), user.getPassword(), new ArrayList<GrantedAuthority>());
    }

    public Users findByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found "+email));
    }

    private void copyDtoToEntity(Users userEntity,UserUpdateRequestDto userDto){
        //validation
        log.info("Validating userDto: {}",userDto );
        if(userDto.getName()!=null&&!userDto.getName().isBlank())
            userEntity.setName(userDto.getName());
        if(userDto.getEmail()!=null&&!userDto.getEmail().isBlank())
            userEntity.setEmail(userDto.getEmail());
        if(userDto.getPassword()!=null&&!userDto.getPassword().isBlank())
            userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
    }
}
