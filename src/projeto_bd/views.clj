(ns projeto-bd.views
  (:require [clojure.string :as st]
            [hiccup.table :refer :all]
            [hiccup.core :refer :all])
  (:import  bdconnector.PostgreSQLJDBC))

(def conn
  "Cria uma instância da classe Java para ser utilizada na interfac"
  (new PostgreSQLJDBC))

(defn le-arquivo
  "Função para leitura de arquivo, recebe uma string com caminho do arquivo a ser lido, baseado
  na pasta resources, e retorna uma string com o conteúdo do arquivo. Retorna nil em caso de erro."
  [filename]
  (try
    (slurp (str "resources/" filename))
  (catch Exception e nil)))

(defn keywordize
  "Função para converter uma lista de mapas do Java em uma lista de
  mapas de Clojure. Retorna uma lazy-seq de mapas na forma Clojure."
  [hashmaplist]
  ;;Lazy-seq são preferíveis por serem processadas apenas no momento em
  ;;que são realmente utilizadas e processarem apenas oq de fato é utilizado.
  (map #(into {} (for [[k v] %] [(keyword k) v])) hashmaplist))

(defn make-table
  "Função para gerar uma tabela HTML a partir de uma lista de mapas."
  [hashmaplist]
  (let [chaves    (keys (first hashmaplist))
        header    (reduce into [] (zipmap (map keyword chaves) chaves))
        conteudo  (keywordize hashmaplist)]
    (str "<div class=\"table-container\">" (html (to-table1d conteudo header)) "</div>")))

(defn base-plate
  "Função que gera um corpo padrão para o conteúdo html das views. Recebe o título da página
  e o conteúdo de seu corpo. Retorna uma string contendo a página completa."
  [title body]
  (str
   "<!DOCTYPE html>
    <html>
      <head>
        <meta charset=\"utf-8\">
        <title>" title "</title>
        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>
        <style>"
        (str
          (le-arquivo "/public/css/basenitro.css")
          (le-arquivo "/public/css/base.css"))
        "</style>
      </head>
      <body>
        <div class=\"wrapper\">"
          body
        "</div>
      </body>
    </html>"))

(defn template1
  "Template básico para o site, útil para modelar conteúdos conforme a necessidade."
  [content]
  (str
    (le-arquivo "/partials/header.html")
    (le-arquivo "/partials/navigation.html")
    (str "<div class=\"main\">"
          content
          "</div>")
      (le-arquivo "/partials/footer.html")))

(defn p404 []
  "Função que apresenta a página 404 a partir do arquivo 404.html"
  (le-arquivo "/public/404.html"))

(defn index []
  "Função que monta o conteúdo da página principal."
  (base-plate
    "Bem vindo!"
    (template1
     "<h2>Bem vindo ao mundo Clojure!</h2>
     <p>Essa pagina esta em uma função index no caminho \"projeto-bd/view.clj\"</p>
     <p>Divirta-se ao edita-la!</p>")))

(defn users
  "Função que retorna o resultado da busca na tabela de usuários a partir de um termo."
  [termo]
  (base-plate
    "Usuarios do sistema"
    (template1
      (str
        (le-arquivo "/partials/buscauser.html")
        (if (nil? termo) nil
          (str "<h3>Resultados para a busca de <b>" termo "</b>:</h3>"
               (make-table (. conn consUsuaNome termo))))))))

(defn eventos-data
  "Função que retorna o resultado da busca na tabela de eventos a partir de datas."
  [ativos data1 data2]
  (let [ativos (if (= ativos "false") false true)]
    (base-plate
      "Eventos por data"
      (template1
        (str
          (le-arquivo "/partials/buscaevento.html")
          (cond
            (= data1 "TODOS") (str "<h3>Mostrando <b>todos</b> os eventos disponíveis:</h3>"
                                (make-table (. conn consEvenData ativos)))
            (and (not= data1 "TODOS") (not= data2 nil)) (str "<h3>Eventos encontrados entre <b>" data1 "</b> e <b>" data2 "</b>:</h3>"
                                                          (make-table (. conn consEvenData data1 data2 ativos)))
            :else nil))))))

(defn p-cadastro
  "Função que retorna a página de cadastro de usuário."
  []
  (base-plate
    "Incluir usuario no sistema"
    (template1
      (le-arquivo "/partials/formulario.html"))))

(defn mostrartabela
  "Função que retorna a página de exibição de tabelas."
  [termo]
  (base-plate
    "Mostrar tabela do BD"
    (template1
      (str
        (le-arquivo "/partials/buscatabela.html")
        (if (nil? termo) nil
          (str "<h3>Mostrando tabela <b>" termo "</b>:</h3>"
               (make-table (. conn mostraTabela termo))))))))

(defn f-cadastro
  "Função que executa o cadastro de um usuário a partir dos parâmetros passados
  através do formulário."
  [mapa]
  (let [nome  (get mapa "nome")
        email (get mapa "email")
        cpf   (get mapa "cpf")
        tipo  (get mapa "tipo")]
    (. conn insereUsua nome email cpf tipo)))