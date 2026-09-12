package com.baseapplication.core.controller;

import com.baseapplication.core.dto.palco.RiderTecnicoDTO;
import com.baseapplication.core.service.MapaPalcoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rider aberto, para a casa de show abrir o link sem ter conta no EzBand.
 * Fica separado do MapaPalcoController porque o caminho inteiro e liberado no
 * SecurityConfiguration -- misturar com os endpoints autenticados tornaria
 * facil liberar mais do que se pretende.
 */
@RestController
@RequestMapping("/publico/rider")
@RequiredArgsConstructor
public class RiderPublicoController {

    private final MapaPalcoService mapaPalcoService;

    @GetMapping("/{idBanda}/{idMapa}")
    public ResponseEntity<RiderTecnicoDTO> buscar(@PathVariable Long idBanda,
                                                  @PathVariable Long idMapa) {
        return ResponseEntity.ok(mapaPalcoService.gerarRiderPublico(idBanda, idMapa));
    }

    /**
     * Mapa padrao da banda. Vem antes da rota de dois ids no arquivo, mas o
     * prefixo literal "padrao" e o que evita ambiguidade -- se fosse
     * "/{idBanda}/padrao", o Spring tentaria converter "padrao" para Long.
     */
    @GetMapping("/padrao/{idBanda}")
    public ResponseEntity<RiderTecnicoDTO> buscarPadrao(@PathVariable Long idBanda) {
        RiderTecnicoDTO rider = mapaPalcoService.gerarRiderPadrao(idBanda);
        return rider == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(rider);
    }
}
