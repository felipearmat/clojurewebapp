(ns projeto-bd.core-test
  (:require [clojure.test :refer :all]
            [projeto-bd.core :refer :all]
            [ring.mock.request :as mock])
  (:import  bdconnector.PostgreSQLJDBC))

(deftest teste-leitor-de-arquivo
  (testing "Abrindo arquivo existente index.html"
    (is (= "<h1>Bem vindo ao mundo Clojure!</h1>  \r\n\r\n<p>Essa pagina esta em um arquivo index.html no caminho \"projeto-bd/resources/projeto_bd/paginas\"</p>\r\n<p>Divirta-se ao edita-lo!</p>" 
    		(leitor-de-arquivo "paginas/index.html"))))
  (testing "Abrindo arquivo inexistente"
    (is (= nil (leitor-de-arquivo "eu_nao_existo.html")))))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))