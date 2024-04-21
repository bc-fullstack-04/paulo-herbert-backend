package br.com.sysmap.bootcamp.domain.service.integration;


import br.com.sysmap.bootcamp.domain.mapper.AlbumMapper;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SpotifyApiService {

    private final SpotifyApi spotifyApi;

    public SpotifyApiService(Environment env) {
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(env.getProperty("CLIENT_ID","68ba5a78a85b48b9856d1d2c3deccc89"))
                .setClientSecret(env.getProperty("CLIENT_SECRET","e404aac43bbe42bca91d9664d2c1ba35"))
                .build();
    }

    public List<AlbumModel> getAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {
        spotifyApi.setAccessToken(getToken());
        return AlbumMapper.INSTANCE.toModel(spotifyApi.searchAlbums(search).market(CountryCode.BR)
                        .limit(30)
                        .build().execute().getItems()).stream()
                .peek(album -> album.setValue(BigDecimal.valueOf((Math.random() * (89) + 12.00))
                        .setScale(2,RoundingMode.HALF_UP))).toList();
    }

    public String getToken() throws IOException, ParseException, SpotifyWebApiException {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        return clientCredentialsRequest.execute().getAccessToken();
    }

}
