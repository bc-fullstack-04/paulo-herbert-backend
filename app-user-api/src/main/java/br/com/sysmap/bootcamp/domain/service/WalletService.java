package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.util.DateProvider;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.ResponseWalletDto;
import br.com.sysmap.bootcamp.dto.WalletOperationDto;
import br.com.sysmap.bootcamp.exceptions.customs.IllegalArgsRequestException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Log4j2
public class WalletService {

    private final UsersService usersService;
    private final WalletRepository walletRepository;
    private final DateProvider dateProvider;

    protected Wallet saveWallet(Wallet wallet){
        return walletRepository.save(wallet);
    }

    public ResponseWalletDto getByUser() {
        Users currentUser = usersService.getLoggedUser();
        return new ResponseWalletDto(walletRepository.findByUsers(currentUser).orElseThrow(EntityNotFoundException::new));
    }

    public ResponseWalletDto insertCredit(BigDecimal value) {
        Users currentUser = usersService.getLoggedUser();
        Wallet currentWallet = walletRepository.findByUsers(currentUser).orElseThrow(()->new EntityNotFoundException("user not found"));
        if(value.compareTo(BigDecimal.ZERO) > 0)
            currentWallet.setBalance(currentWallet.getBalance().add(value));
        else
            throw new IllegalArgsRequestException("Invalid value: "+value);
        return new ResponseWalletDto(saveWallet(currentWallet));
    }

    public Wallet debit(WalletOperationDto walletDto) {
        Wallet wallet = walletRepository.findByUsersEmail(walletDto.getUserEmail())
                .orElseThrow(()->new EntityNotFoundException("user not found"));
        if(wallet.getBalance().compareTo(walletDto.getPrice())>=0){
            log.info("debiting wallet: {}", walletDto);
            wallet.setBalance(wallet.getBalance().subtract(walletDto.getPrice()));
            wallet.setPoints(wallet.getPoints()+calculatePoints(dateProvider.getCurrentDate().getDayOfWeek()));
        }
        else
            throw new IllegalArgsRequestException("Insuficient balance");
        return walletRepository.save(wallet);
    }

    public Integer calculatePoints(DayOfWeek currentDay) {
        Map<DayOfWeek,Integer> pointsMap = Map.of(DayOfWeek.SUNDAY,25,DayOfWeek.MONDAY,7
                                            ,DayOfWeek.TUESDAY,6,DayOfWeek.WEDNESDAY,2,
                                            DayOfWeek.THURSDAY,10,DayOfWeek.FRIDAY,15,DayOfWeek.SATURDAY,20);
        log.info("Day of week:{} points: {}",currentDay,pointsMap.get(currentDay));
        return pointsMap.get(currentDay);
    }
}
