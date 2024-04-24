package br.com.sysmap.bootcamp.domain.services.integration;

import br.com.sysmap.bootcamp.domain.mapper.AlbumMapper;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApiService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

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
