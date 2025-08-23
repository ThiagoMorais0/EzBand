package com.baseapplication.core.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.EmailDTO;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioPainelDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.model.Usuario;

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

	InfoPerfilUsuarioDTO editarUsuarioComImagem(String usuarioJson, MultipartFile imagem);


	void enviarSolicitacaoParaIngressarBanda(Long idBanda, String instrumento);

	InfoPerfilUsuarioDTO buscarInformacoesDoPerfilPorId(Long idUsuario);

	void reportarErro(String mensagem);

	void enviarEmail(EmailDTO email);

    ResponseEntity<?> buscarProximosEventosDoUsuario();
}
