package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.dto.RequestAlbumDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
@CrossOrigin("*")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/all")
    @Operation(summary = "get all from spotify by text parameter")
    public ResponseEntity<Page<AlbumModel>> getAlbums(@RequestParam("search") String search, Pageable pg) throws IOException, ParseException, SpotifyWebApiException {
        return ResponseEntity.ok(this.albumService.getAlbums(search,pg));
    }

    @GetMapping("/my-collection")
    @Operation(summary = "get all from my collection")
    public ResponseEntity<Page<Album>> getByUser(Pageable pg){
        return ResponseEntity.ok(albumService.getUserAlbums(pg));
    }

    @PostMapping("/sale")
    @Operation(summary = "buy an album")
    public ResponseEntity<Album> saveAlbum(@Valid @ParameterObject @RequestBody RequestAlbumDto album, HttpServletRequest http) {
        Album albumSaved = albumService.saveAlbum(album);
        URI uri = UriComponentsBuilder.fromUriString(http.getRequestURI()).path("/{id}").buildAndExpand(albumSaved.getId()).toUri();
        return ResponseEntity.created(uri).body(albumSaved);
    }

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "remove an album by id")
    public ResponseEntity<Void> removeAlbum(@PathVariable Long id) {
        albumService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
