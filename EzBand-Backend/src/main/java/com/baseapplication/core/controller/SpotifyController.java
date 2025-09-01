package com.baseapplication.core.controller;

import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/spotify")
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;

    @PostMapping("/adicinarMusicasDaPlaylistAoRepertorioDaBanda")
    public ResponseEntity<?> getPlaylistInfo(@RequestParam String url, @RequestParam Long idBanda) {
        try {
            return ResponseEntity.ok(spotifyService.adicionarMusicasDaPlaylistAoRepertorioDaBanda(url, idBanda));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar dados da playlist.");
        }
    }


}
