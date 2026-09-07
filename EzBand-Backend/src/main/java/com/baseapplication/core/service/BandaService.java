package com.baseapplication.core.service;

import java.util.List;

import com.baseapplication.core.dto.BuscaBandaDTO;
import com.baseapplication.core.dto.EditarMembroMusicoBandaDTO;
import com.baseapplication.core.dto.EventoConviteDTO;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.EnsaiosFuturosDTO;
import com.baseapplication.core.dto.InfoMembroBandaDTO;
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


    Long novaBanda(String bandaJson, MultipartFile logo, MultipartFile banner);

    ResponseEntity<?> buscarBandaParaIngressar(Long idBanda);


    Integer buscarQuantidadeDeShows(Long idBanda);
    Integer buscarQuantidadeDeEnsaios(Long idBanda);
    Integer buscarQuantidadeDeNotificacoes(Long idBanda);
    Integer buscarQuantidadeDeMembros(Long idBanda);
    Integer buscarQuantidadeDeMusicasNoRepertorio(Long idBanda);


    void sairDaBanda(Long idBanda, Long id);

    ShowsFuturosDTO buscarShowsFuturosBanda(Long idBanda, Long idUsuario) ;

    Banda buscarPorId(Long idBanda);

    EnsaiosFuturosDTO buscarEnsaiosFuturosBanda(Long idBanda, Long id) ;

    List<RepertorioBandaDTO> buscarRepertorio(Long idBanda);

    void adicionarMusicaAoRepertorio(RepertorioBandaDTO repertorioBandaDTO);

    void editarBanda(String bandaJson, MultipartFile logo, Boolean removerLogo, MultipartFile banner, Boolean removerBanner);

    List<String> getPermissoesMusico(Long idBanda, Long id);

    void enviarConviteParaUsuarioIngressarBanda(Long idBanda, Long idUsuarioConvidado, List<EventoConviteDTO> eventos);

    List<EnsaioDTO> buscarEnsaios(Long idBanda);

    List<ShowDTO> buscarShows(Long idBanda);

    void atualizarMusicaRertorio(RepertorioBandaDTO repertorioBandaDTO);

    void alterarPermissaoMembro(EditarMembroMusicoBandaDTO permissaoMusicoDTO);

    List<Banda> buscarSugestoes(BuscaBandaDTO dto);

    void removerMusicaDoRepertorio(Long id, Long idBanda);

    void atualizarOrdemRepertorio(Long idBanda, List<RepertorioBandaDTO> repertorio);

}
