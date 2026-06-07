package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.MembroFantasmaDao;
import com.baseapplication.core.dao.TokenValidacaoCelularDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.ConfirmacaoTokenDTO;
import com.baseapplication.core.dto.ValidacaoCelularRequestDTO;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.MembroFantasma;
import com.baseapplication.core.model.TokenValidacaoCelular;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.ValidacaoCelularService;
import com.baseapplication.core.service.WhatsappService;
import com.baseapplication.core.utils.PhoneNumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidacaoCelularServiceImpl implements ValidacaoCelularService {

    private final TokenValidacaoCelularDao tokenDao;
    private final MembroFantasmaDao membroFantasmaDao;
    private final UsuarioDao usuarioDao;
    private final WhatsappService whatsappService;
    private final PhoneNumberUtil phoneNumberUtil;

    @Value("${app.frontend.url:https://ezband.cloud}")
    private String frontendUrl;

    @Override
    @Transactional
    public Map<String, Object> gerarEEnviarToken(ValidacaoCelularRequestDTO request) throws BadRequestException {
        String celular = request.getCelular();

        if (celular == null || celular.isBlank()) {
            throw new BadRequestException("Celular é obrigatório");
        }

        String celularNormalizado = phoneNumberUtil.normalizeToE164(celular);

        String nomeDestinatario = "";

        if (request.getIdMembroFantasma() != null) {
            MembroFantasma membro = membroFantasmaDao.findById(request.getIdMembroFantasma())
                    .orElseThrow(() -> new ResourceNotFoundException("Membro fantasma não encontrado"));
            nomeDestinatario = membro.getNome();
        } else if (request.getIdUsuario() != null) {
            Usuario usuario = usuarioDao.findById(request.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            nomeDestinatario = usuario.getNome();
        } else {
            throw new BadRequestException("É necessário informar idMembroFantasma ou idUsuario");
        }

        verificarConexaoEvolution();

        String token = gerarToken();

        TokenValidacaoCelular tokenValidacao = new TokenValidacaoCelular();
        tokenValidacao.setToken(token);
        tokenValidacao.setCelular(celularNormalizado);
        tokenValidacao.setIdMembroFantasma(request.getIdMembroFantasma());
        tokenValidacao.setIdUsuario(request.getIdUsuario());
        tokenValidacao.setDataCriacao(LocalDateTime.now());
        tokenValidacao.setDataExpiracao(LocalDateTime.now().plusMinutes(2));
        tokenValidacao.setValidado(false);

        tokenDao.save(tokenValidacao);

        String linkConfirmacao = String.format(
            "%s/confirmar-celular?token=%s&celular=%s",
            frontendUrl, token, celularNormalizado
        );

        String mensagem = String.format(
            "🎵 *EzBand Manager*\n\n" +
            "Olá, %s!\n\n" +
            "Para confirmar seu número, toque no link abaixo:\n" +
            "👉 %s\n\n" +
            "Ou insira o código manualmente: *%s*\n\n" +
            "⏰ Válido por 2 minutos.\n\n" +
            "Se você não solicitou este código, ignore esta mensagem.",
            nomeDestinatario, linkConfirmacao, token
        );

        try {
            whatsappService.enviarMensagem(celularNormalizado, mensagem);
            log.info("Token de validação enviado para {} ({})", nomeDestinatario, celularNormalizado);
        } catch (Exception e) {
            log.error("Erro ao enviar token via WhatsApp: {}", e.getMessage());
            throw new BadRequestException("A mensagem não pôde ser entregue. Tente novamente mais tarde.");
        }

        return Map.of(
            "sucesso", true,
            "mensagem", "Código de validação enviado com sucesso",
            "celular", celular,
            "nomeDestinatario", nomeDestinatario
        );
    }

    @Override
    @Transactional
    public Map<String, Object> validarToken(ConfirmacaoTokenDTO request) throws BadRequestException {
        String celular = request.getCelular();
        String token = request.getToken();

        if (celular == null || celular.isBlank()) {
            throw new BadRequestException("Celular é obrigatório");
        }

        if (token == null || token.isBlank()) {
            throw new BadRequestException("Token é obrigatório");
        }

        String celularNormalizado = phoneNumberUtil.normalizeToE164(celular);

        TokenValidacaoCelular tokenValidacao = tokenDao.buscarTokenValido(
            celularNormalizado, token, LocalDateTime.now()
        ).orElseThrow(() -> new BadRequestException("Código inválido ou expirado"));

        tokenValidacao.setValidado(true);
        tokenValidacao.setDataValidacao(LocalDateTime.now());
        tokenDao.save(tokenValidacao);

        if (tokenValidacao.getIdMembroFantasma() != null) {
            MembroFantasma membro = membroFantasmaDao.findById(tokenValidacao.getIdMembroFantasma())
                    .orElseThrow(() -> new ResourceNotFoundException("Membro fantasma não encontrado"));
            membro.setCelularValidado(true);
            membro.setDataCelularValidado(LocalDateTime.now());
            membroFantasmaDao.save(membro);
        } else if (tokenValidacao.getIdUsuario() != null) {
            Usuario usuario = usuarioDao.findById(tokenValidacao.getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            usuario.setCelularValidado(true);
            usuarioDao.save(usuario);
            enviarMensagemBoasVindas(celularNormalizado, usuario.getNome());
        }

        log.info("Celular {} validado com sucesso", celularNormalizado);

        return Map.of(
            "sucesso", true,
            "mensagem", "Número confirmado com sucesso!",
            "celular", celular
        );
    }

    @Override
    public Map<String, Object> verificarDisponibilidade() {
        try {
            verificarConexaoEvolution();
            return Map.of("disponivel", true, "mensagem", "WhatsApp conectado");
        } catch (BadRequestException e) {
            return Map.of("disponivel", false, "mensagem", e.getMessage());
        } catch (Exception e) {
            return Map.of("disponivel", false, "mensagem", "Serviço de WhatsApp temporariamente indisponível");
        }
    }

    private void verificarConexaoEvolution() throws BadRequestException {
        try {
            Map<String, Object> status = whatsappService.obterStatusConexao();
            Object instanceObj = status.get("instance");
            if (instanceObj instanceof Map<?, ?> instance) {
                String state = String.valueOf(instance.get("state"));
                if (!"open".equals(state)) {
                    throw new BadRequestException("A mensagem não pôde ser entregue. Tente novamente mais tarde.");
                }
            } else {
                throw new BadRequestException("A mensagem não pôde ser entregue. Tente novamente mais tarde.");
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Falha ao verificar conexão Evolution: {}", e.getMessage());
            throw new BadRequestException("A mensagem não pôde ser entregue. Tente novamente mais tarde.");
        }
    }

    private void enviarMensagemBoasVindas(String celular, String nome) {
        String mensagem = String.format(
            "🎉 *Número confirmado!*\n\n" +
            "Tudo certo, %s! Seu número está vinculado ao EzBand. 🎸\n\n" +
            "Por aqui você vai receber avisos de shows, ensaios, novas músicas no repertório e novidades da sua banda. " +
            "Fique atento — esse número é o seu canal direto com o palco! 🎵\n\n" +
            "Bem-vindo(a) ao EzBand! 🤘",
            nome
        );
        try {
            whatsappService.enviarMensagem(celular, mensagem);
        } catch (Exception e) {
            log.warn("Falha ao enviar mensagem de boas-vindas para {}: {}", celular, e.getMessage());
        }
    }

    private String gerarToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000);
        return String.valueOf(token);
    }
}
