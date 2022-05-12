(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))



;simple body page
(defn simple-body-page [req]
  {:status 200 :headers {"Content-Type" "text/html"} :body "Hello World"}
  )

;return request body
(defn request-example [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (
           ->> (pp/pprint req)
               (str "Request object: " req)
           )
   }
  )

(defroutes app-routes
           (GET "/" [] simple-body-page)
           (GET "/request" [] request-example)
           (route/not-found "Error, page not found")
           )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;let is used to bind data strutures to symbols i.e
  ;(let [x *]
  ;  (println (x 2 8))
  ;  )
  ;16
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ;Run the server with RIng.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ;Run server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http://127.0.0.1: " port "/"))
    ))
