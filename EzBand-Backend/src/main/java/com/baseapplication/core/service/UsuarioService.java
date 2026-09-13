package com.baseapplication.core.service;

import com.baseapplication.core.dto.ParticipacoesEspeciaisDTO;
import com.baseapplication.core.dto.SeguidorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.EmailDTO;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioPainelDTO;
import com.baseapplication.core.dto.BuscaGlobalDTO;
import com.baseapplication.core.dto.InstrumentoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoRelacionamento;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.dto.ShowDTO;

import java.util.List;

public interface UsuarioService {

//    Usuario findByLogin(String nome);
	Usuario findByEmail(String nome);

	Usuario salvar(Usuario usuario);

	InfoUsuarioPainelDTO buscarInfoPainel();

	void atualizarInformacoesPerfil(InfoPerfilUsuarioDTO infoPerfil);

	Usuario buscarPorId(Long id);

	Usuario findByCelular(String celular);

	Usuario buscarPorContato(String contato, TipoContato tipoContato);

	Boolean verificarEmailJaCadastrado(String email);

	InfoPerfilUsuarioDTO editarUsuarioComImagem(String usuarioJson, MultipartFile imagem, Boolean removerImagemDePerfil);


	void enviarSolicitacaoParaIngressarBanda(Long idBanda, String instrumento);

	InfoPerfilUsuarioDTO buscarInformacoesDoPerfil();

	List<BandaDTO> buscarBandasDoUsuario();

	InfoPerfilUsuarioDTO buscarInformacoesDoPerfilPorId(Long idUsuario);

	void reportarErro(String mensagem);

	void enviarEmail(EmailDTO email);

    ResponseEntity<?> buscarProximosEventosDoUsuario();

	InfoPerfilUsuarioDTO buscarPorEmail(String email);

	ParticipacoesEspeciaisDTO buscarParticipacoesEspeciais();

	/** Shows de participacao especial aceitos pelo usuario, para perfil e agenda publicos. */
	List<ShowDTO> buscarShowsParticipacoesEspeciaisPorUsuario(Long idUsuario);

	List<BuscaGlobalDTO> buscarGlobal(String termo);

	void seguirUsuario(Long idUsuario);

	void deixarDeSeguir(Long idUsuario);

	TipoRelacionamento buscarTipoRelacionamento(Long idUsuario);

    List<InfoPerfilUsuarioDTO> buscarAmigos();

    List<InfoPerfilUsuarioDTO> buscarSugestoes(String termo);

    List<SeguidorDTO> buscarSeguidores(Long idUsuario);

    List<SeguidorDTO> buscarSeguindo(Long idUsuario);

    List<InstrumentoDTO> buscarInstrumentosDoUsuario(Long idUsuario);

    InstrumentoDTO adicionarInstrumento(String nome);

    void removerInstrumento(Long idInstrumento);

    InstrumentoDTO definirInstrumentoFavorito(Long idInstrumento);

    void salvarInstrumentosRegistro(Long idUsuario, List<String> instrumentos, String instrumentoFavorito);

    void atualizarTiposUsuario(List<String> tipos);
}
