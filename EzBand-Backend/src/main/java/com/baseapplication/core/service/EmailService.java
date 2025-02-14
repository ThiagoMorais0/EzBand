package com.baseapplication.core.service;

public interface EmailService {

    Boolean enviarEmail(String assunto, String mensagem, String destino);
}
