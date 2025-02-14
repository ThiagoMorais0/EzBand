package com.baseapplication.core.service;

import com.baseapplication.core.dto.EmailDTO;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioPainelDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.superClasses.NotificacaoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UsuarioService {

//    Usuario findByLogin(String nome);
    Usuario findByEmail(String nome);

    void salvar(Usuario usuario);

    InfoUsuarioPainelDTO buscarInfoPainel();

    void atualizarInformacoesPerfil(InfoPerfilUsuarioDTO infoPerfil);

    Usuario buscarPorId(Long id);

    Usuario findByCelular(String celular);

    Usuario buscarPorContato(String contato, TipoContato tipoContato);

    Boolean verificarEmailJaCadastrado(String email);

    InfoPerfilUsuarioDTO editarUsuarioComImagem(InfoPerfilUsuarioDTO usuario, MultipartFile imagem);

    Integer buscarQuantidadeNotificacoes(Long idUsuario);

    void enviarSolicitacaoParaIngressarBanda(Long idBanda, Long idUsuarioRemetente, String instrumento);

    InfoPerfilUsuarioDTO buscarInformacoesDoPerfilPorId(Long idUsuario);

    List<NotificacaoDTO> buscarNotificacoesUsuario(Long id);

    void reportarErro(String mensagem);

    void enviarEmail(EmailDTO email);
}
