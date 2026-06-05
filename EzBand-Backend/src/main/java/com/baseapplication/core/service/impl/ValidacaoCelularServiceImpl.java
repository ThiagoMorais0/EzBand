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

        String token = gerarToken();
        
        TokenValidacaoCelular tokenValidacao = new TokenValidacaoCelular();
        tokenValidacao.setToken(token);
        tokenValidacao.setCelular(celularNormalizado);
        tokenValidacao.setIdMembroFantasma(request.getIdMembroFantasma());
        tokenValidacao.setIdUsuario(request.getIdUsuario());
        tokenValidacao.setDataCriacao(LocalDateTime.now());
        tokenValidacao.setDataExpiracao(LocalDateTime.now().plusMinutes(10));
        tokenValidacao.setValidado(false);
        
        tokenDao.save(tokenValidacao);
        
        String mensagem = String.format(
            "🎵 *EzBand Manager*\n\n" +
            "Olá, %s!\n\n" +
            "Seu código de validação é: *%s*\n\n" +
            "⏰ Este código expira em 10 minutos.\n\n" +
            "Se você não solicitou este código, ignore esta mensagem.",
            nomeDestinatario, token
        );
        
        try {
            whatsappService.enviarMensagem(celularNormalizado, mensagem);
            log.info("Token de validação enviado para {} ({})", nomeDestinatario, celularNormalizado);
        } catch (Exception e) {
            log.error("Erro ao enviar token via WhatsApp: {}", e.getMessage());
            throw new BadRequestException("Erro ao enviar código de validação. Verifique se o número está correto.");
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
        ).orElseThrow(() -> new BadRequestException("Token inválido ou expirado"));
        
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
        }
        
        log.info("Celular {} validado com sucesso", celularNormalizado);
        
        return Map.of(
            "sucesso", true,
            "mensagem", "Celular validado com sucesso",
            "celular", celular
        );
    }

    private String gerarToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000);
        return String.valueOf(token);
    }
}
