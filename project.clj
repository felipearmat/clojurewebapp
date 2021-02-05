(defproject projeto-bd "0.1.0-SNAPSHOT"
  :description "Projeto de BD - SCC0540 - 2018"
  :authors "Felipe Araújo Matos              NUSP:5968691
            Eric Azevedo Guimarães           NUSP:10408330
            Vinícius do Nascimento Fontenele NUSP:9293651"
  :url "https://bitbucket.org/scc0540"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring "1.7.1"]
                 [compojure "1.6.1"]
                 [environ "1.0.0"]
                 [hiccup-table "0.2.0"]]
  :plugins [[lein-ring "0.12.4"]
            [environ/environ.lein "0.3.1"]]
  :ring {:handler projeto-bd.core/handler}
  :hooks [environ.leiningen.hooks]
  :repl-options {:timeout 200000}
  :java-source-paths ["src/java"]
  :resource-paths ["resources/postgresql-42.2.5.jar"]
  :main projeto-bd.core
  :uberjar-name "projetobd.jar"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :profiles 
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}
   :production {:env {:production true}
                :dependencies [[javax.servlet/servlet-api "2.5"]]}})