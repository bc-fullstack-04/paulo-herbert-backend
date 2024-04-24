package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.exceptions.customs.IllegalArgsRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AlbumControllerTest{

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AlbumService albumService;

    @Test
    @DisplayName("should list all albums by text parameter")
    public void shouldListAllAlbumsByTextParameter() throws Exception {
        Page<AlbumModel> page = new PageImpl<>(Collections.singletonList(new AlbumModel()));
        when(albumService.getAlbums(any(),any())).thenReturn(page);

        mockMvc.perform(get("/albums/all")
                        .param("search", "test")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

    }

    @Test
    @DisplayName("Should return my albums")
    public void shouldListMyAlbums() throws Exception {
        Page<Album> page = new PageImpl<>(Collections.singletonList(Album.builder().id(1L).name("Album Test").build()));
        when(albumService.getUserAlbums(any())).thenReturn(page);
        mockMvc.perform(get("/albums/my-collection")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Album Test"));
    }

    @Test
    @DisplayName("should save an album")
    void shouldSaveAlbum() throws Exception {
        Album album = Album.builder().id(1L).idSpotify("idspotify").artistName("capuano").name("Album Test").build();
        when(albumService.saveAlbum(any())).thenReturn(album);
        mockMvc.perform(post("/albums/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Album Test"))
                .andExpect(jsonPath("$.idSpotify").value(album.getIdSpotify()));
    }

    @Test
    @DisplayName("delete should return no content")
    void shouldDeleteAlbum() throws Exception {

        doNothing().when(albumService).delete(anyLong());
        mockMvc.perform(delete("/albums/remove/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("delete should throws not found ")
    void deleteShouldThrowsNotFound() throws Exception {
        doThrow(EntityNotFoundException.class).when(albumService).delete(anyLong());
        mockMvc.perform(delete("/albums/remove/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Entity Not Found"));
    }

    @Test
    @DisplayName("should return 422 status code ")
    void shouldReturnUnprocessableEntity() throws Exception {
        Album album = Album.builder().id(1L).build();
        when(albumService.saveAlbum(any())).thenReturn(album);
        mockMvc.perform(post("/albums/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Validation Exception"));
    }

    @Test
    @DisplayName("should return BadRequest ")
    void shouldReturnBadRequest() throws Exception {
        Album album = Album.builder().id(1L).idSpotify("idspotify").artistName("capuano").name("Album Test").build();
        when(albumService.saveAlbum(any())).thenThrow(IllegalArgsRequestException.class);

        mockMvc.perform(post("/albums/sale", 1L).content(objectMapper.writeValueAsString(album))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Illegal Argument in Request"));
    }
}
