package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
@CrossOrigin("*")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/all")
    public ResponseEntity<Page<AlbumModel>> getAlbums(@RequestParam("search") String search,Pageable pg) throws IOException, ParseException, SpotifyWebApiException {
        return ResponseEntity.ok(this.albumService.getAlbums(search,pg));
    }

    @GetMapping("/my-collection")
    public ResponseEntity<Page<Album>> getByUser(Pageable pg){
        return ResponseEntity.ok(albumService.getUserAlbums(pg));
    }

    @PostMapping("/sale")
    public ResponseEntity<Album> saveAlbum(@RequestBody Album album) {
        return ResponseEntity.ok(this.albumService.saveAlbum(album));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeAlbum(@PathVariable Long id) {
        albumService.delete(id);
        return ResponseEntity.ok().build();
    }
}