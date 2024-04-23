package br.com.sysmap.bootcamp.dto;

import br.com.sysmap.bootcamp.domain.entities.Album;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RequestAlbumDto(
        @NotBlank
        String name,
        @NotBlank
        String idSpotify,
        @NotBlank
        String artistName,
        String imageUrl,
        @Positive
        BigDecimal value

) {
    public RequestAlbumDto(Album album) {
        this(album.getName(),album.getIdSpotify(),album.getArtistName(),album.getImageUrl(),album.getValue());
    }
}
