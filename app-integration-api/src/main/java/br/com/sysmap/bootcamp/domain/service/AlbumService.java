package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApiService;
import br.com.sysmap.bootcamp.dto.WalletOperationDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class AlbumService {

    private final Queue queue;
    private final RabbitTemplate template;
    private final SpotifyApiService spotifyApiService;
    private final AlbumRepository albumRepository;
    private final UsersService usersService;


    public Page<AlbumModel> getAlbums(String search,Pageable pg) throws IOException, ParseException, SpotifyWebApiException {
        List<AlbumModel> albumList = spotifyApiService.getAlbums(search);
        return new PageImpl<>(albumList,pg,albumList.size());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Album saveAlbum(Album album) {
        album.setUsers(getAuthenticatedUser());
        Album albumSaved = albumRepository.save(album);
        WalletOperationDto walletDto = new WalletOperationDto(albumSaved.getUsers().getEmail(), albumSaved.getValue());
        this.template.convertAndSend(queue.getName(), walletDto);
        return albumSaved;
    }

    private Users getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return usersService.findByEmail(username);
    }

    public Page<Album> getUserAlbums(Pageable pg) {
        return albumRepository.findAllByUsers(getAuthenticatedUser(),pg);
    }

    public void delete(Long id) {
        albumRepository.delete(albumRepository.findAlbumByUsersAndId(getAuthenticatedUser(),id)
                .orElseThrow(()-> new EntityNotFoundException("Album Not Found")));
    }
}
