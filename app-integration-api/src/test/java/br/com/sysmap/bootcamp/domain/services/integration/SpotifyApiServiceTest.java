package br.com.sysmap.bootcamp.domain.services.integration;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.mapper.AlbumMapper;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApiService;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class SpotifyApiServiceTest {

    @InjectMocks
    private SpotifyApiService spotifyApiService;
    @Mock
    Environment env;

    @Mock
    AlbumMapper albumMapper;

    @Mock
    private ClientCredentialsRequest clientCredentialsRequest;
    @Mock
    private ClientCredentialsRequest.Builder clientCredentialsRequestBuilder;

   /* public void getAlbumsShouldReturnAlbums() throws IOException, ParseException, SpotifyWebApiException {
        String search = "teste";
        String token = "example";
        doNothing().when(spotifyApi).setAccessToken(anyString());
        when(albumMapper.toModel((any(AlbumSimplified[].class)))).thenReturn(List.of(new AlbumModel()));

        ClientCredentialsRequest.Builder clientCredentialsRequestBuilder = new ClientCredentialsRequest.Builder("cara","teste");
        when(spotifyApi.clientCredentials()).thenReturn(clientCredentialsRequestBuilder);

        assertDoesNotThrow(()->spotifyApiService.getAlbums(search));
    }*/

    /*@Test
    public void testGetToken() throws IOException, ParseException, SpotifyWebApiException {
        when(clientCredentialsRequest.execute()).thenReturn();
    }*/
}
