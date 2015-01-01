(ns olyp-app-utils.olyp-central-api-client
  (:require [org.httpkit.client :as http]
            cheshire.core
            [clojure.tools.logging :as log]))

(defn get-response-body [res]
  (if (.startsWith (get-in res [:headers :content-type] "") "application/json")
    (cheshire.core/parse-string (:body res))
    (:body res)))

(defmacro handle-res [perform-res res-binding & handlers]
  (if (odd? (count handlers))
    (throw (IllegalArgumentException. "Expected an even number of clauses")))
  `(let [~res-binding ~perform-res
         status# (:status ~res-binding)]
     (case status#
       ~@handlers
       (do
         (log/error (str "Unhandled central api request:" ~res-binding))
         {:status 501 :body (str "501 Not Implemented (" status# ")")}))))

(defn get-request-body [body]
  (cond
   (map? body) (cheshire.core/generate-string body)
   (list? body) (cheshire.core/generate-string body)
   (vector? body) (cheshire.core/generate-string body)
   :else body))

(defn request
  ([ctx method path]
     (request ctx method path nil))
  ([ctx method path body]
     (let [res
           @(http/request
             {:method method
              :body (get-request-body body)
              :url (str (:url ctx) path)
              :client (:httpkit-client ctx)})]
       {:status (:status res)
        :headers (:headers res)
        :body (get-response-body res)
        :httpkit-res res})))
