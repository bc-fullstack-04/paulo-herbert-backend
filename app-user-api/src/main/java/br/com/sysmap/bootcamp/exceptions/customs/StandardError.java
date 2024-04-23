package br.com.sysmap.bootcamp.exceptions.customs;

import java.time.Instant;

public record StandardError(Instant instant, Integer status, String error, String message, String path) {
}
