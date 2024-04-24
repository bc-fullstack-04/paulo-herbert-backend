package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.dto.RequestAlbumDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
@CrossOrigin("*")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/all")
    @Operation(summary = "get all from spotify by text parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "obtained albums sucessful"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "500",description = "internal failure to get albums")
    })
    public ResponseEntity<Page<AlbumModel>> getAlbums(@RequestParam("search") String search, Pageable pg) throws IOException, ParseException, SpotifyWebApiException {
        return ResponseEntity.ok(this.albumService.getAlbums(search,pg));
    }

    @GetMapping("/my-collection")
    @Operation(summary = "get all from my collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "obtained my albums successful"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "500",description = "internal failure to get albums")
    })
    public ResponseEntity<Page<Album>> getByUser(Pageable pg){
        return ResponseEntity.ok(albumService.getUserAlbums(pg));
    }

    @PostMapping("/sale")
    @Operation(summary = "buy an album")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "album saved successful"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "422",description = "invalid fields on the request"),
            @ApiResponse(responseCode = "500",description = "internal failure to save album")
    })
    public ResponseEntity<Album> saveAlbum(@Valid @ParameterObject @RequestBody RequestAlbumDto album, HttpServletRequest http) {
        Album albumSaved = albumService.saveAlbum(album);
        URI uri = UriComponentsBuilder.fromUriString(http.getRequestURI()).path("/{id}").buildAndExpand(albumSaved.getId()).toUri();
        return ResponseEntity.created(uri).body(albumSaved);
    }

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "remove an album by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "no content"),
            @ApiResponse(responseCode = "404",description = "album not found"),
            @ApiResponse(responseCode = "401",description = "not authenticated user"),
            @ApiResponse(responseCode = "500",description = "internal failure to get albums")
    })
    public ResponseEntity<Void> removeAlbum(@PathVariable Long id) {
        albumService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
