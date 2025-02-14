package com.baseapplication.core.service.impl;

import com.baseapplication.core.service.EmailService;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public Boolean enviarEmail(String assunto, String mensagem, String destinatario) {
        // Configurações do servidor SMTP
        String usuario = "ezbandapp@gmail.com"; // Substitua pelo seu e-mail
        String senha = "uzio ylxn shlp krob"; // Substitua pela sua senha ou App Password

        // Configuração das propriedades do servidor
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        // Criação da sessão com autenticação
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, senha);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(usuario));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setContent(mensagem, "text/html; charset=utf-8");
            message.setSubject(assunto);

            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            throw new ServiceException("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}
