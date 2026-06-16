-- Habilita notificações WhatsApp para usuários que já têm celular validado
-- e ainda não possuem registro em CONFIGURACAO_NOTIFICACAO_USUARIO.
INSERT INTO configuracao_notificacao_usuario (id_usuario, receber_notificacoes_whatsapp)
SELECT u.id, TRUE
FROM usuario u
WHERE u.celular_validado = TRUE
  AND NOT EXISTS (
      SELECT 1 FROM configuracao_notificacao_usuario c WHERE c.id_usuario = u.id
  );
