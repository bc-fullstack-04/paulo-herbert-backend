package br.com.sysmap.bootcamp.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateProvider {

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

}
