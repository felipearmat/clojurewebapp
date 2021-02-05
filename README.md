# projeto-bd

Projeto para aplicação prática de conceitos de Banco de Dados.
Turma: SCC0540 - Banco de Dados - 2018
Profª: Eliane Parros M. de Souza
Entrega: 

## Usage

Para iniciar o sistema é aconselhado que se utilize o docker, com a imagem leiningen, por praticidade.
Uma vez que o docker esteja instalado você pode executar o seguinte comando (remova o símbolo de cifrão):

$: docker run --rm -it -v [caminho para a pasta do projeto]:/work -w /work -p 8000:8000 clojure:lein-2.8.1 bash

O parâmetro --rm remove o contâiner assim que este for encerrado, o parâmetro -it cria uma interface interativa,
o parâmetro -v monta o caminho de uma pasta local para o contâiner na pasta /work do contâiner (p. ex. o caminho "/home/docker/projects" pode ser mapeado nesta pasta, permitindo a alteração de arquivos locais através do 
contâiner), o parâmetro -w indica a pasta a ser aberta assim que o contâiner for iniciado, o parâmetro -p mapeia
uma porta local para a porta do contâiner, permitindo a comunicação, seguido da versão da imagem a ser iniciada
no contâiner do docker e o primeiro comando a ser executado.

Depois de executar o comando, navegue até a pasta do projeto (se esta já não for a pasta atual) e execute o comando

$: lein run 8000

Você deve ver a mensagem "INFO:oejs.Server:main: Started", ou algo parecido, neste caso o sistema foi iniciado, acesse o ip padrão do seu docker (normalmente 127.0.0.1) na porta 8000 para ver o resultado.
Você pode acessar seu navegador e colocar o endereço http://localhost:8000 (se a configuração do seu docker
for a padrão, caso você esteja usando o dockertools o endereço será http://192.168.99.100:8000).

## License

Copyright © 2018

Distributed under the MIT License either version 1.0 or any later version.
