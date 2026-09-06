package com.baseapplication.core.service;

import com.baseapplication.core.dto.places.BuscaPlacesDTO;
import com.baseapplication.core.dto.places.PlaceDetalheDTO;
import com.baseapplication.core.dto.places.PlaceSugestaoDTO;

import java.util.List;

public interface PlacesService {

    /** True quando a chave do Google esta configurada. Se false, o front esconde a lupa. */
    boolean disponivel();

    List<PlaceSugestaoDTO> buscarSugestoes(BuscaPlacesDTO dto);

    PlaceDetalheDTO buscarDetalhes(String placeId, String sessionToken);
}
