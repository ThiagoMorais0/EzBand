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
import com.baseapplication.core.model.Usuario;

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

	InfoPerfilUsuarioDTO buscarInformacoesDoPerfilPorId(Long idUsuario);

	void reportarErro(String mensagem);

	void enviarEmail(EmailDTO email);

    ResponseEntity<?> buscarProximosEventosDoUsuario();

	InfoPerfilUsuarioDTO buscarPorEmail(String email);

	ParticipacoesEspeciaisDTO buscarParticipacoesEspeciais();

	List<BuscaGlobalDTO> buscarGlobal(String termo);

	void seguirUsuario(Long idUsuario);

	void deixarDeSeguir(Long idUsuario);

    List<InfoPerfilUsuarioDTO> buscarAmigos();

    List<InfoPerfilUsuarioDTO> buscarSugestoes(String termo);

    List<SeguidorDTO> buscarSeguidores(Long idUsuario);

    List<InstrumentoDTO> buscarInstrumentosDoUsuario(Long idUsuario);

    InstrumentoDTO adicionarInstrumento(String nome);

    void removerInstrumento(Long idInstrumento);

    InstrumentoDTO definirInstrumentoFavorito(Long idInstrumento);

    void salvarInstrumentosRegistro(Long idUsuario, List<String> instrumentos, String instrumentoFavorito);

    void atualizarTiposUsuario(List<String> tipos);
}
