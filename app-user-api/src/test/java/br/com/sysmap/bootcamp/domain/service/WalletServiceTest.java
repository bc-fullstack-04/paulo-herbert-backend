package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.util.DateProvider;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.ResponseWalletDto;
import br.com.sysmap.bootcamp.dto.WalletOperationDto;
import br.com.sysmap.bootcamp.exceptions.customs.IllegalArgsRequestException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UsersService usersService;
    @Mock
    private DateProvider dateProvider;

    @Test
    @DisplayName("calculate points should return right points")
    public void calculatePointsShouldReturnRightPoints() {
        assertEquals(7,walletService.calculatePoints(DayOfWeek.MONDAY));
        assertEquals(6,walletService.calculatePoints(DayOfWeek.TUESDAY));
        assertEquals(2,walletService.calculatePoints(DayOfWeek.WEDNESDAY));
        assertEquals(10,walletService.calculatePoints(DayOfWeek.THURSDAY));
        assertEquals(15,walletService.calculatePoints(DayOfWeek.FRIDAY));
        assertEquals(20,walletService.calculatePoints(DayOfWeek.SATURDAY));
        assertEquals(25,walletService.calculatePoints(DayOfWeek.SUNDAY));
    }

    @Test
    @DisplayName("Should return user")
    public void getByUserShouldReturnUser() {
        Users user = new Users(1L,"teste","teste@mail.ocm","password");
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUsers(user);

        when(usersService.getLoggedUser()).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));

        ResponseWalletDto responseWalletDto = walletService.getByUser();
        assertNotNull(responseWalletDto);
        assertEquals(1L, responseWalletDto.getId());
        assertEquals(user.getEmail(),responseWalletDto.getUser().getEmail());
        assertEquals(user.getName(),responseWalletDto.getUser().getName());
    }

    @Test
    @DisplayName("Should throw entity not found")
    public void testGetByUserWalletNotFound() {
        Users user = new Users(1L,"teste","teste@mail.ocm","password");
        when(usersService.getLoggedUser()).thenReturn(user);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            walletService.getByUser();
        });
    }

    @Test
    @DisplayName("Should insert credits in wallet")
    public void insertCreditsShouldInsertCredits() {
        Users user = new Users(1L,"teste","teste@mail.ocm","password");
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setUsers(user);
        BigDecimal expected = BigDecimal.valueOf(100+100);
        when(usersService.getLoggedUser()).thenReturn(user);
        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));
        ResponseWalletDto resp = walletService.insertCredit(BigDecimal.valueOf(100));
        assertEquals(expected,resp.getBalance());

    }

    @Test
    @DisplayName("Should throw error when  try to insert invalid credits in wallet")
    public void insertCreditsShouldThrowErrorWhenInvalidCredits() {
        Users user = new Users(1L,"teste","teste@mail.ocm","password");
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setUsers(user);
        when(usersService.getLoggedUser()).thenReturn(user);
        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));
        assertThrows(IllegalArgsRequestException.class,()->walletService.insertCredit(BigDecimal.valueOf(0)));
    }

    @Test
    @DisplayName("Debit Should debit credits from wallet")
    public void debitShouldDebitCreditsFromWallet() {
        WalletOperationDto DTO = new WalletOperationDto("teste@mail.ocm",BigDecimal.valueOf(100));
        Users user = new Users(1L,"teste","teste@mail.ocm","password");
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setUsers(user);
        wallet.setPoints(0L);
        when(walletRepository.findByUsersEmail(user.getEmail())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);
        //monday
        when(dateProvider.getCurrentDate()).thenReturn(LocalDate.of(2024, 4, 22));

        Wallet walletDebited = walletService.debit(DTO);
        assertEquals(BigDecimal.ZERO,walletDebited.getBalance());
        assertEquals(7,walletDebited.getPoints());
    }

    @Test
    @DisplayName("Debit should throw a exception by insuficient balance")
    public void debitShouldThrowErrorWhenInsufficientBalance() {
        WalletOperationDto DTO = new WalletOperationDto("teste@mail.ocm",BigDecimal.valueOf(100));
        Users user = new Users(1L,"teste","teste@mail.ocm","password");
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(50));
        wallet.setPoints(0L);
        when(walletRepository.findByUsersEmail(user.getEmail())).thenReturn(Optional.of(wallet));
        assertThrows(IllegalArgsRequestException.class,()->walletService.debit(DTO));
    }
}
