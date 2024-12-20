package com.baseapplication.core.service;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.dto.BandaDTO;
import com.google.protobuf.ServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface BandaService {
    List<Banda> buscarBandasPorUsuario(Long idUsuario);

    List<Banda> buscarParticipacoesEspeciais(Long idUsuario);

    BandaDTO getInfo(Long idBanda) throws ServiceException;

    void cadastrarUsuario(Long idBanda, Long idUsuario, String instrumentos);

    void expulsarUsuario(Long idBanda, Long idUsuario);

    List<InfoMembroBandaDTO> buscarMembros(Long idBanda);


    void novaBanda(CadastroBandaDTO bandaDTO, MultipartFile logo);

    BandaDTO buscarBandaParaIngressar(Long idBanda) throws ServiceException;


    Integer buscarQuantidadeDeShows(Long idBanda);
    Integer buscarQuantidadeDeEnsaios(Long idBanda);
    Integer buscarQuantidadeDeNotificacoes(Long idBanda);
    Integer buscarQuantidadeDeMembros(Long idBanda);
    Integer buscarQuantidadeDeMusicasNoRepertorio(Long idBanda);

    Integer getNivelPermissaoUsuario(Long idBanda, Long id);

    void sairDaBanda(Long idBanda, Long id);

    ShowsFuturosDTO buscarShowsFuturosBanda(Long idBanda, Long idUsuario) throws ServiceException;

    Banda buscarPorId(Long idBanda);

    EnsaiosFuturosDTO buscarEnsaiosFuturosBanda(Long idBanda, Long id) throws ServiceException;

    List<MusicaDTO> buscarRepertorio(Long idBanda);

    void adicionarMusicaAoRepertorio(RepertorioBandaDTO repertorioBandaDTO);
}
