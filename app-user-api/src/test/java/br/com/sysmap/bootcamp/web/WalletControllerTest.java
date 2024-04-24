package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.dto.ResponseWalletDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    @Test
    @DisplayName("get wallet should return a wallet")
    public void getWalletShouldReturnWallet() throws Exception {
        ResponseWalletDto responseWalletDto = new ResponseWalletDto();
        responseWalletDto.setBalance(new BigDecimal("200"));
        responseWalletDto.setPoints(10L);
        when(walletService.getByUser()).thenReturn(responseWalletDto);

        mockMvc.perform(get("/wallet").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("200"))
                        .andExpect(jsonPath("$.points").value(10));
    }

    @Test
    @DisplayName("insert wallet should return a walletdto")
    public void shouldReturnWalletDto() throws Exception {
        BigDecimal creditValue = new BigDecimal("50.00");
        ResponseWalletDto responseWalletDto = new ResponseWalletDto();
        responseWalletDto.setBalance(new BigDecimal("150"));
        responseWalletDto.setPoints(50L);
        when(walletService.insertCredit(creditValue)).thenReturn(responseWalletDto);
        mockMvc.perform(post(("/wallet/credit/{value}"), creditValue).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("150"))
                .andExpect(jsonPath("$.points").value(50));
    }
}
