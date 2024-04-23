package br.com.sysmap.bootcamp.exceptions.customs;

public class IllegalArgsRequestException extends RuntimeException{
    public IllegalArgsRequestException(String message) {
        super(message);
    }
}
