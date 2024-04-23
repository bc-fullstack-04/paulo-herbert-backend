package br.com.sysmap.bootcamp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class DateProviderTest {

    private DateProvider dateProvider;
    @BeforeEach
    void setup(){
        dateProvider  = new DateProvider();
    }

    @Test
    @DisplayName("Should get current date")
    void dateProviderShouldGetCurrentDate(){
        assertEquals(LocalDate.now(),dateProvider.getCurrentDate());
    }
}
