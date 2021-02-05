(ns projeto-bd.core
	(:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.route :as route]
            [projeto-bd.views :as views]
            [environ.core :refer [env]])
  (:gen-class))

(defn welcome
  "Handler para a página principal. Chama a função index do ns views
  para montar o conteúdo."
  [request]
  {:status 200
   :body (views/index)
   :headers {"Content-Type" "text/html; charset=utf-8"}})

(defn usuarios
  "Handler para a página de pesquisa de usuários. Chama a funcao user
  em views para montar o conteúdo baseado no parametro passado na url."
  [request]
  (let [termo (:termo (:route-params request))]
  {:status 200
   :body  (views/users termo)
   :headers {"Content-Type" "text/html; charset=utf-8"}}))

(defn hello
  "Handler teste para debug de passagem de parâmetro via get."
  [request]
  (let [name (:name (:route-params request))]
    {:status 200
     :body (str "Hello " name ".  I got your name from the web URL")
     :headers {"Content-Type" "text/html; charset=utf-8"}}))

(defn f-cadastro
  "Handler para efetuar cadastro de usuario via ajax."
  [request]
  (let [values (:form-params request)]
  (views/f-cadastro values)
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}}))

(defn p-cadastro
  "Handler para a página de cadastro de usuários. Chama a funcao de views
  com o formulário para cadastro."
  [request]
  {:status 200
   :body  (views/p-cadastro)
   :headers {"Content-Type" "text/html; charset=utf-8"}})

(defn eventos-data
  "Handler para a página de pesquisa de eventos por data. Chama a funcao eventos-data
  em views para montar o conteúdo baseado nas datas passadas pela url."
  [request]
  (let [data1 (:data1 (:route-params request))
        data2 (:data2 (:route-params request))
        ativos (:ativos (:route-params request))
        ativos (if (nil? ativos) "true" "false")]
  {:status 200
   :body  (views/eventos-data ativos data1 data2)
   :headers {"Content-Type" "text/html; charset=utf-8"}}))

(defn mostrartabela
  "Handler para a página que exibe as tabelas do BD. Chama a funcao mostrartabela
  em views para montar o conteúdo baseado no nome utilizado na url."
  [request]
  (let [tabela (:tabela (:route-params request))
        values #{"ESTADIAS" "VAGAS" "TELEFONES_ESTADIA" "USUARIOS" "TELEFONES_USUARIO"
                 "UNIVERSIDADES" "EVENTOS" "DISTANCIAS_UES" "DISTANCIAS_UEV" "DISTANCIAS_EE"}]
  {:status 200
   :body  (if (or (nil? tabela) (contains? values tabela))
            (views/mostrartabela tabela)
            (views/p404))
   :headers {"Content-Type" "text/html; charset=utf-8"}}))

;;Define as rotas que existem no site.
(defroutes site-routes
  (GET "/" [] welcome)
  (route/resources "/")
  (GET "/buscausuarios" [] usuarios)
  (GET "/buscausuarios/:termo" [] usuarios)
  (GET "/buscaevento/data" [] eventos-data)
  (GET "/buscaevento/data/TODOS/" [] eventos-data)
  (GET "/buscaevento/data/TODOS/:inativos" [] eventos-data)
  (GET "/buscaevento/data/:data1/:data2/:inativos" [] eventos-data)
  (GET "/mostrartabela" [] mostrartabela)
  (GET "/mostrartabela/:tabela" [] mostrartabela)
  (POST "/cadastrausuario" [] f-cadastro)
  (GET "/cadastrausuario" [] p-cadastro)
  (GET "/hello/:name" [] hello)
  (route/not-found (views/p404)))

;;Chamada do handler para ser utilizada no modo de produção
(def handler
  (handler/site #'site-routes))

(defn -main
  "Função principal, executada com comando lein run. Recebe a porta
  a ser iniciado o web-server, como padrão usa a porta 5000."
  [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty handler
      {:port port :join? false})))