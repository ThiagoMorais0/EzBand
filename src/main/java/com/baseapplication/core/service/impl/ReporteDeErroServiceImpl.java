package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.ReporteDeErroDao;
import com.baseapplication.core.model.ReporteDeErro;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.EmailService;
import com.baseapplication.core.service.ReporteDeErroService;
import com.baseapplication.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReporteDeErroServiceImpl implements ReporteDeErroService {

    @Autowired
    private ReporteDeErroDao reporteDeErroDao;

    @Autowired
    private EmailService emailService;

    @Override
    public void reportarErro(String mensagem, Usuario usuario) {
        reporteDeErroDao.save(new ReporteDeErro(usuario, mensagem));
        emailService.enviarEmail(criarAssuntoEmail(usuario), criarMensagemEmail(usuario, mensagem), "thiagomface@gmail.com");
    }

    private String criarMensagemEmail(Usuario usuario, String mensagem) {
        return "<!DOCTYPE html> " +
                "<body> " +
                "    <h3>Erro reportado pelo Usuário: " + usuario.getNome() + "</h3> " +
                "    <br> " +
                "    <p>" + mensagem + "</p> " +
                "    <br><br> " +
                "    <h4>Informações do usuário:</h4> " +
                "    <h5>Nome: " + usuario.getNome() + "</h5> " +
                "    <h5>Email: " + usuario.getEmail() + "</h5> " +
                "    <h5>Celular: " + usuario.getCelular() + "</h5> " +
                "    <h5>Ativo desde: " + DateUtils.localDateToString(usuario.getDataCriacao()) + "</h5> " +
                "</body> " +
                "</html>";
    }

    private String criarAssuntoEmail(Usuario usuario) {
        return "EzBand - O usuário " + usuario.getNome() + " reportou um erro!";
    }
}
