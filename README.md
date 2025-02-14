## Sobre os containers

### Configurações:
- O arquivo compose.yml é responsável pela orquestração da subida dos conainers como uma unica aplicação.
- Os arquivos Dockerfile definem os detalhes de cada conteiner especifico.
- O arquivo .env contém as variáveis de ambiente que serão injetadas dentro dos containers. Está no gitignore pois são as informações sensíveis. Pode conter todas as senhas e/ou secret_keys (para criptografia, por exemplo) que serão utilizadas em todos os serviços.
### Atualizando:
- Backend: gerar o jar com maven clean package
- Frontend:
- DB:
  - Se houver atualização de usuario/senha, é necessário alterar dentro do conteiner antes de mexer no .env
### Rodando a aplicação:
- Na raiz do projeto, executar o comando "docker compose up" para subir os serviços.
  - Quando houver alteração, "docker compose up --build", para buildar antes de subir.
### Diferentes ambientes:
Ainda precisamos definir a melhor abordagem. Sugestão é termos dois conjuntos de .env diferentes, um para prd outro para dev.
Também é interessante termos infos variáveis no compose para dev e prd, como nome dos containers e networks, por exemplo.
