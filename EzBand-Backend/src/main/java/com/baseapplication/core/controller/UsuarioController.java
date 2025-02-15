package com.baseapplication.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.EmailDTO;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.utils.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/buscarInfoUsuarioPainel")
    public RetornoDTO buscarInfoUsuarioPainel() {
        try {
            return RetornoDTO.success(usuarioService.buscarInfoPainel());
        } catch (Exception e) {
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/buscarInformacoesDoPerfil")
    public RetornoDTO buscarInformacoesDoPerfil() {
        try {
            return RetornoDTO.success(new InfoPerfilUsuarioDTO(Context.getUsuarioLogado()));
        } catch (Exception e) {
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/atualizarInformacoesPerfil")
    public RetornoDTO atualizarInformacoesPerfil(@RequestBody InfoPerfilUsuarioDTO infoPerfil) {
        try {
            usuarioService.atualizarInformacoesPerfil(infoPerfil);
            return RetornoDTO.success();
        } catch (Exception e) {
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/editarUsuarioComImagem")
    public RetornoDTO editarUsuarioComImagem(@RequestParam("usuario") String usuarioJson, MultipartFile imagem) throws JsonProcessingException {
        try{
            InfoPerfilUsuarioDTO usuario = new ObjectMapper().readValue(usuarioJson, InfoPerfilUsuarioDTO.class);
            return RetornoDTO.success(usuarioService.editarUsuarioComImagem(usuario, imagem));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/verificarEmailJaCadastrado")
    @CrossOrigin(origins = "*")
    public RetornoDTO verificarEmailJaCadastrado(@RequestParam String email) {
        try {
            return !usuarioService.verificarEmailJaCadastrado(email) ? RetornoDTO.success() : RetornoDTO.error("E-mail já cadastrado");
        } catch (Exception e) {
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/buscarQuantidadeNotificacoes")
    public RetornoDTO buscarQuantidadeNotificacoes(){
        try{
            return RetornoDTO.success(usuarioService.buscarQuantidadeNotificacoes(Context.getUsuarioLogado().getId()));
        }catch (Exception e){
            return RetornoDTO.error("Erro ao buscar notificações");
        }
    }

    @GetMapping("/enviarSolicitacaoParaIngressarBanda")
    public RetornoDTO enviarSolicitacaoParaIngressarBanda(@RequestParam Long idBanda, @RequestParam String instrumento){
        try{
            usuarioService.enviarSolicitacaoParaIngressarBanda(idBanda, Context.getUsuarioLogado().getId(), instrumento);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error("Erro ao enviar solicitação");
        }
    }

    @GetMapping("/buscarInformacoesDoPerfilPorId")
    public RetornoDTO buscarInformacoesDoPerfilPorId(@RequestParam Long idUsuario) {
        try {
            return RetornoDTO.success(usuarioService.buscarInformacoesDoPerfilPorId(idUsuario));
        } catch (Exception e) {
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/buscarNotificacoesUsuario")
    public RetornoDTO buscarNotificacoesUsuario(){
        try{
            return RetornoDTO.success(usuarioService.buscarNotificacoesUsuario(Context.getUsuarioLogado().getId()));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/reportarErro")
    public RetornoDTO reportarErro(@RequestBody String mensagem){
        try{
            usuarioService.reportarErro(mensagem);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error("Erro ao enviar mensagem");
        }
    }

    @PostMapping("/enviarEmail")
    public RetornoDTO enviarEmail(@RequestBody EmailDTO email){
        try{
            usuarioService.enviarEmail(email);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error("Erro ao enviar e-mail: " + e.getMessage());
        }
    }

}
