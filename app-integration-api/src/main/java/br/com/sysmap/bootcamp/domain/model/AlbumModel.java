package br.com.sysmap.bootcamp.domain.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.michaelthelin.spotify.enums.AlbumType;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;
import se.michaelthelin.spotify.model_objects.specification.Image;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumModel {

    private AlbumType albumType;
    private ArtistSimplified[] artists;
    private ExternalUrl externalUrls;
    private String id;
    private Image[] images;
    private String name;
    private String releaseDate;
    private ModelObjectType type;
    private BigDecimal value;

}
