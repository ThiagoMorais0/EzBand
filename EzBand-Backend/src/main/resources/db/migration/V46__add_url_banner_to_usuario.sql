-- Banner (imagem de capa) do perfil publico do usuario, exibido acima da foto.
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS url_banner VARCHAR(255);
