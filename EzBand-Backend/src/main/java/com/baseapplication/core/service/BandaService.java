package com.baseapplication.core.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.EnsaiosFuturosDTO;
import com.baseapplication.core.dto.InfoMembroBandaDTO;
import com.baseapplication.core.dto.MusicaDTO;
import com.baseapplication.core.dto.RepertorioBandaDTO;
import com.baseapplication.core.dto.ShowsFuturosDTO;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.dto.BandaDTO;

public interface BandaService {
    List<Banda> buscarBandasPorUsuario(Long idUsuario);

    List<Banda> buscarParticipacoesEspeciais(Long idUsuario);

    BandaDTO getInfo(Long idBanda);

    void cadastrarUsuario(Long idBanda, Long idUsuario, String instrumentos);

    void expulsarUsuario(Long idBanda, Long idUsuario);

    List<InfoMembroBandaDTO> buscarMembros(Long idBanda);


    void novaBanda(String bandaJson, MultipartFile logo);

    BandaDTO buscarBandaParaIngressar(Long idBanda);


    Integer buscarQuantidadeDeShows(Long idBanda);
    Integer buscarQuantidadeDeEnsaios(Long idBanda);
    Integer buscarQuantidadeDeNotificacoes(Long idBanda);
    Integer buscarQuantidadeDeMembros(Long idBanda);
    Integer buscarQuantidadeDeMusicasNoRepertorio(Long idBanda);

    Integer getNivelPermissaoUsuario(Long idBanda, Long id);

    void sairDaBanda(Long idBanda, Long id);

    ShowsFuturosDTO buscarShowsFuturosBanda(Long idBanda, Long idUsuario) ;

    Banda buscarPorId(Long idBanda);

    EnsaiosFuturosDTO buscarEnsaiosFuturosBanda(Long idBanda, Long id) ;

    List<MusicaDTO> buscarRepertorio(Long idBanda);

    void adicionarMusicaAoRepertorio(RepertorioBandaDTO repertorioBandaDTO);
}
