package com.baseapplication.core.controller;

import com.baseapplication.core.dto.places.BuscaPlacesDTO;
import com.baseapplication.core.dto.places.PlaceDetalheDTO;
import com.baseapplication.core.dto.places.PlaceSugestaoDTO;
import com.baseapplication.core.service.PlacesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Proxy autenticado para o Google Places. A chave fica so no servidor.
 * SecurityConfiguration usa anyRequest().authenticated(), entao estas rotas
 * ja exigem JWT sem configuracao adicional.
 */
@Slf4j
@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlacesController {

    private final PlacesService service;

    /** O front esconde a lupa quando isto retorna false. */
    @GetMapping("/disponivel")
    public ResponseEntity<Map<String, Boolean>> disponivel() {
        return ResponseEntity.ok(Map.of("disponivel", service.disponivel()));
    }

    @PostMapping("/buscar")
    public ResponseEntity<List<PlaceSugestaoDTO>> buscar(@RequestBody BuscaPlacesDTO dto) {
        return ResponseEntity.ok(service.buscarSugestoes(dto));
    }

    @GetMapping("/detalhes")
    public ResponseEntity<PlaceDetalheDTO> detalhes(@RequestParam String placeId,
                                                    @RequestParam(required = false) String sessionToken) {
        return ResponseEntity.ok(service.buscarDetalhes(placeId, sessionToken));
    }
}
