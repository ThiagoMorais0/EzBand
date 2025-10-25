## Sobre os containers

### Configurações:
- O arquivo compose.yml é responsável pela orquestração da subida dos conainers como uma unica aplicação.
- Os arquivos Dockerfile definem os detalhes de cada conteiner especifico.
- O arquivo .env contém as variáveis de ambiente que serão injetadas dentro dos containers. Está no gitignore pois são as informações sensíveis. Pode conter todas as senhas e/ou secret_keys (para criptografia, por exemplo) que serão utilizadas em todos os serviços.
- o arquivo .env deve ser criptografado com gpg e inserido na raiz do projeto.
### Atualizando:
- Backend: gerar o jar com maven clean package
- Frontend:
- DB:
  - Se houver atualização de usuario/senha, é necessário alterar dentro do conteiner antes de mexer no .env
### Rodando a aplicação:
- O arquivo "ezband.sh" automatiza a subida da aplicação
- Na raiz do projeto, executar o comando "bash ezband.sh -c (build|start|stop|restart) -s (nome do serviço)" para subir os serviços.
  - OBS: 
    1. para passar mais de um serviço, pode adicionar mais -s (nome do serviço)
    2. para passar todos, basta não inserir -s
    3. para serviços de start e restart, irá pedir senha do gpg, que é a chave secreta configurada na criptografia do arquivo .env.gpg
### Diferentes ambientes:
Ainda precisamos definir a melhor abordagem. Sugestão é termos dois conjuntos de .env diferentes, um para prd outro para dev.
Também é interessante termos infos variáveis no compose para dev e prd, como nome dos containers e networks, por exemplo.
### Mais instruções
- Arquivos .env_base e application-base.properties possuem instruções específicas