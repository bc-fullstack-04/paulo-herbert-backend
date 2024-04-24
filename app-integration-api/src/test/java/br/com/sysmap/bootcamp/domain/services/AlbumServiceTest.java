package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApiService;
import br.com.sysmap.bootcamp.dto.RequestAlbumDto;
import br.com.sysmap.bootcamp.dto.WalletOperationDto;
import br.com.sysmap.bootcamp.exceptions.customs.IllegalArgsRequestException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AlbumServiceTest {

    @InjectMocks
    private AlbumService albumService;

    @Mock
    private SpotifyApiService spotifyApiService;

    @Mock
    private UsersService usersService;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private RabbitTemplate template;
    @Mock
    private Queue queue;


    @Test
    @DisplayName("Should return paged albums by text parameter")
    void getAllShouldReturnAlbumsPaged() throws IOException, ParseException, SpotifyWebApiException {
        String search = "test";
        Pageable pg = Pageable.ofSize(10).withPage(0);
        List<AlbumModel> albumList = Arrays.asList(new AlbumModel(), new AlbumModel());
        when(spotifyApiService.getAlbums(any())).thenReturn(albumList);

        Page<AlbumModel> result = albumService.getAlbums(search, pg);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertNotNull(result.getContent().get(0));
        assertNotNull(result.getContent().get(1));
    }

    @Test
    @DisplayName("Should return saved album")
    void saveShouldReturnSavedAlbum() {
        RequestAlbumDto reqAlbum = new RequestAlbumDto("Jose","id","artist","image", BigDecimal.TEN);
        Users user = Users.builder().id(1L).email("teste@email.com").build();
        Album album = Album.builder().name("Jose").id(1L).idSpotify("id").value(BigDecimal.TEN).build();
        album.setUsers(user);

        when(usersService.getAuthenticatedUser()).thenReturn(user);
        when(albumRepository.findAlbumByUsersAndIdSpotify(user,"id")).thenReturn(Optional.empty());
        when(albumRepository.save(any())).thenReturn(album);
        when(queue.getName()).thenReturn("queuename");
        doNothing().when(template).convertAndSend(eq("queuename"), any(WalletOperationDto.class));

        Album resp = assertDoesNotThrow(()->albumService.saveAlbum(reqAlbum));
        assertNotNull(resp);
        assertEquals(reqAlbum.idSpotify(), resp.getIdSpotify());
        assertEquals(reqAlbum.value(), resp.getValue());
        assertEquals(user, resp.getUsers());
    }

    @Test
    @DisplayName("Should throw exception when album exists")
    public void saveAlbumShouldThrowExceptionWhenAlbumAlreadyExists() {
        RequestAlbumDto albumDto = new RequestAlbumDto("Jose","id","artist","image", BigDecimal.TEN);
        Users user = Users.builder().id(1L).email("teste@mail.com").build();
        when(usersService.getAuthenticatedUser()).thenReturn(user);
        when(albumRepository.findAlbumByUsersAndIdSpotify(user, albumDto.idSpotify())).thenReturn(Optional.of(Album.builder().build()));

        assertThrows(IllegalArgsRequestException.class, () -> albumService.saveAlbum(albumDto));
    }

    @Test
    @DisplayName("Should return saved album")
    public void getUserAlbumsShouldReturnAlbumsPaged() {
        Users user = Users.builder().id(1L).build();
        Album album = Album.builder().id(1L).users(user).build();
        Pageable pg = Pageable.ofSize(10).withPage(0);

        when(usersService.getAuthenticatedUser()).thenReturn(user);
        when(albumRepository.findAllByUsers(user, pg)).thenReturn(new PageImpl<>(Collections.singletonList(album)));

        Page<Album> result = albumService.getUserAlbums(pg);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(album, result.getContent().get(0));
    }

    @Test
    @DisplayName("Should delete sucessful")
    public void deleteShouldReturnNothing() {
        Long id = 1L;
        Users user = Users.builder().id(id).build();
        Album album = Album.builder().id(id).users(user).build();

        when(usersService.getAuthenticatedUser()).thenReturn(user);
        when(albumRepository.findAlbumByUsersAndId(user, id)).thenReturn(Optional.of(album));

        assertDoesNotThrow(()->albumService.delete(id));
        verify(albumRepository, times(1)).delete(album);
        verify(usersService, times(1)).getAuthenticatedUser();
    }

    @Test
    @DisplayName("delete should throws NotFound")
    public void deleteShouldThrowNotFound() {
        Long id = 1L;
        Users user = Users.builder().id(1L).build();

        when(usersService.getAuthenticatedUser()).thenReturn(user);
        when(albumRepository.findAlbumByUsersAndId(user, id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.delete(id));
    }
}
