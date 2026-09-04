package com.baseapplication.core.service.impl;

import com.baseapplication.core.enums.Tonalidade;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.RepertorioBanda;
import com.baseapplication.core.model.embedded.Musica;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.service.RepertorioBandaService;
import com.baseapplication.core.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final BandaService bandaService;
    private final RepertorioBandaService repertorioBandaService;

    public String adicionarMusicasDaPlaylistAoRepertorioDaBanda(String url, Long idBanda) {

        Integer ultimoIndice = repertorioBandaService.buscarUltimoIndice(idBanda);

        // 1 - Extrair o ID
        String playlistId = extractPlaylistId(url);
        if (playlistId == null) {
            throw new InternalException("URL inválida.");
        }

        // 2 - Obter token de acesso
        String accessToken = getAccessToken();

        // 3 - Buscar playlist inteira (nome + tracks)
        String apiUrl = "https://api.spotify.com/v1/playlists/" + playlistId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

        // Nome da playlist
        String playlistName = (String) response.getBody().get("name");

        // Pegar tracks
        Map<String, Object> tracksObj = (Map<String, Object>) response.getBody().get("tracks");
        List<Map<String, Object>> items = (List<Map<String, Object>>) tracksObj.get("items");

        Banda banda = bandaService.buscarPorId(idBanda);

        List<RepertorioBanda> musicasNovas = items.stream().map(i -> {
            Map<String, Object> trackData = (Map<String, Object>) i.get("track");
            List<Map<String, Object>> artists = (List<Map<String, Object>>) trackData.get("artists");

            Map<String, Object> externalUrls = (Map<String, Object>) trackData.get("external_urls");
            String urlSpotify = externalUrls != null ? (String) externalUrls.get("spotify") : null;

            Integer durationMs = (Integer) trackData.get("duration_ms");

// Calcula horas, minutos e segundos da duração
            long totalSeconds = durationMs / 1000;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;

// Formata como hh:mm:ss artificial
            LocalTime localTime = LocalTime.of(0, (int) minutes, (int) seconds);
            Time duracao = Time.valueOf(localTime);

            RepertorioBanda repertorioBanda = new RepertorioBanda();
            repertorioBanda.setBanda(banda);

            Musica musica = new Musica(
                    trackData.get("name").toString(),
                    artists.stream().map(a -> (String) a.get("name")).collect(Collectors.joining(", ")),
                    null,
                    null,
                    duracao,
                    Tonalidade.ORIGINAL,
                    null, //urlYoutube
                    urlSpotify, //urlSpotify
                    null, //letra
                    null //bpm
            );
            repertorioBanda.setMusica(musica);
            return repertorioBanda;
        }).toList();

        for (RepertorioBanda musicaNova : musicasNovas) {
            boolean exists = banda.getRepertorio().stream()
                    .anyMatch(r -> r.getMusica().getTitulo().equalsIgnoreCase(musicaNova.getMusica().getTitulo()));
            if (!exists) {
                musicaNova.setIndice(++ultimoIndice);
                repertorioBandaService.salvar(musicaNova);
            }
        }


        return playlistName;
    }


    // Função para extrair ID da URL
    private String extractPlaylistId(String url) {
        if (url.contains("playlist/")) {
            return url.split("playlist/")[1].split("\\?")[0];
        }
        return null;
    }

    // Função para obter token de acesso (Client Credentials Flow)
    private String getAccessToken() {
        String auth = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + auth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://accounts.spotify.com/api/token",
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }
}
