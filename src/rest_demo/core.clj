(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))
;my people-collection mutable collection vector
(def people-collection (atom []))

;collection helper function to add new person
(defn addperson [firstname surname]
  (swap! people-collection conj {:firstname (str/capitalize firstname)
                                 :surname (str/capitalize surname)})
  )
;add json objects
(addperson "Michael" "Basweti")
(addperson "TRiza" "Basweti")

;return json list pf people
(defn people-handler [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (
           str (json/write-str @people-collection)
               )
   }
  )

(defn hello-name [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (->
              (pp/pprint req)
              (str "Hello " (:name (:params req))))})

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

(defn getparameter [req pname] (get (:params req) pname))

;(println (getparameter req :firstname))

; Add a new person into the people-collection
(defn addperson-handler [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (-> (let [p (partial getparameter req)]
                  (str (json/write-str (addperson (p :firstname) (p :surname))))))})

(defroutes app-routes
           (GET "/" [] simple-body-page)
           (GET "/request" [] request-example)
           (GET "/hello" [] hello-name)
           (GET "/people" [] people-handler)
           (GET "/add" [] addperson-handler)
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
