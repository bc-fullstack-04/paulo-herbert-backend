package br.com.sysmap.bootcamp.domain.service;


import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.dto.RequestAuthDto;
import br.com.sysmap.bootcamp.dto.ResponseAuthDto;
import br.com.sysmap.bootcamp.dto.ResponseUserDto;
import br.com.sysmap.bootcamp.dto.UserRequestDto;
import br.com.sysmap.bootcamp.exceptions.IllegalArgsRequestException;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            throw new IllegalArgsRequestException("User with email " + userDto.getEmail() + " already exists");
        }
        Users userEntity = Users.builder().name(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword())).email(userDto.getEmail()).build();
        userEntity = usersRepository.save(userEntity);
        log.info("Saving user: {}",userEntity );
        Wallet wallet = walletService.saveWallet(Wallet.builder().balance(BigDecimal.valueOf(100)).points(0L).users(userEntity).build());
        log.info("Creating wallet: {}",wallet);
        return new ResponseUserDto(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseUserDto update(UserRequestDto userDto) {
        Users loggedUser = getLoggedUser();
        if(isEmailAlreadyRegistered(loggedUser,userDto)){
            throw new IllegalArgsRequestException("User with email " + userDto.getEmail() + " already exists");
        }
        log.info("Updating user: {}", loggedUser);
        return new ResponseUserDto(usersRepository.save(loggedUser.toBuilder()
                .name(userDto.getName()).email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword())).build()));
    }

    private boolean isEmailAlreadyRegistered(Users loggedUser, UserRequestDto userDto){
        Optional<Users> usersOptional = usersRepository.findByEmail(userDto.getEmail());
        if(usersOptional.isEmpty())
            return false;
        return !usersOptional.get().getEmail().equals(loggedUser.getEmail());
    }

    public Users getLoggedUser() {
        String loggedUserName = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.usersRepository.findByEmail(loggedUserName).orElseThrow();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = findByEmail(username);
        return new User(user.getEmail(), user.getPassword(), new ArrayList<GrantedAuthority>());
    }

    public Users findByEmail(String email) {
        return this.usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public ResponseAuthDto auth(RequestAuthDto requestAuthDto) {
        Users users = this.findByEmail(requestAuthDto.getEmail());

        if (!this.passwordEncoder.matches(requestAuthDto.getPassword(), users.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String password = users.getEmail().concat(":").concat(users.getPassword());

        return ResponseAuthDto.builder().email(users.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.getBytes())
        ).id(users.getId()).build();
    }

    public Page<ResponseUserDto> findAll(Pageable pg) {
        return (usersRepository.findAll(pg).map(ResponseUserDto::new));
    }

    public ResponseUserDto findById(Long id) {
        return new ResponseUserDto(usersRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("User not found")));
    }
}
