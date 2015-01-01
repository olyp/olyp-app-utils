(ns olyp-app-utils.components.olyp-central-api-client-ctx
  (:require [com.stuartsierra.component :as component])
  (:import [org.httpkit.client HttpClient]))

(defrecord OlypCentralApiClientCtx [url]
  component/Lifecycle

  (start [component]
    (assoc component
      :httpkit-client (HttpClient.)))
  (stop [component]
    (.stop (:httpkit-client component))
    (dissoc component :httpkit-client)))

(defn create-ctx [{:keys [url]}]
  (OlypCentralApiClientCtx. url))
