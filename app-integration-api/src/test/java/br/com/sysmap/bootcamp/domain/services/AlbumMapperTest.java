package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.mapper.AlbumMapper;
import br.com.sysmap.bootcamp.domain.mapper.AlbumMapperImpl;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class AlbumMapperTest {

    private AlbumMapper albumMapper;

    @BeforeEach
    void setUp() {
        albumMapper = new AlbumMapperImpl();
    }
    @Test
    void shouldReturnAlbumModel(){
        AlbumSimplified[] albumSimplified = new AlbumSimplified[]{};
        List<AlbumModel> albumModel = albumMapper.toModel(albumSimplified);
        assertNotNull(albumModel);
        Assertions.assertTrue(albumModel.isEmpty());
    }
}
