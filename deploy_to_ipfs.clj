(ns deploy-to-ipfs
  (:require
   [babashka.curl :as curl]
   [cheshire.core :as json]
   [clojure.string :as str]))

(def netcup-endpoint "https://ccp.netcup.net/run/webservice/servers/endpoint.php?JSON")

(def customer-number (System/getenv "CUSTOMERID"))
(def api-key (System/getenv "APIKEY"))
(def api-password (System/getenv "APIPASSWORD"))

(defn- post [auth-env body]
  (-> netcup-endpoint
      (curl/post
       {:headers {"Accept" "application/json"}
        :body
        (json/generate-string
         (update body :param merge auth-env))})
      :body
      (json/parse-string keyword)
      :responsedata))

(defn login [auth-env]
  (when-let [auth-token
             (-> auth-env
                 (post {:action "login"})
                 :apisessionid)]
    (-> auth-env
        (dissoc :apipassword)
        (assoc :apisessionid auth-token))))

(defn info [auth-env]
  (post auth-env
        {:action "infoDnsRecords"
         :param {:domainname "ebbinghaus.me"}}))

(defn update-dns-record! [auth-env {:keys [domainname dnsrecordset]
                                    :as param}]
  (and
   (post
    auth-env
    {:action "updateDnsRecords"
     :param param})
   auth-env))



(defn find-record [auth-env host]
  (let [records (:dnsrecords (info auth-env))]
    (first (filter #(= host (:hostname %)) records))))

(defn update-dnslink! [auth-env new-destination]
  (let [{:keys [id destination]} (find-record auth-env "_dnslink.blog")]
    (when-not (= new-destination destination)
      (update-dns-record! auth-env
                          {:domainname "ebbinghaus.me"
                           :dnsrecordset
                           {:dnsrecords
                            #{(cond->
                               {:hostname "_dnslink.blog"
                                :type "TXT"
                                :destination new-destination}
                                id (assoc :id id))}}}))))


(defn -main [cid]
  (-> {:apikey api-key
       :customernumber customer-number
       :apipassword api-password}
      login
      (update-dnslink! (str "dnslink=/ipfs/" (str/trim cid)))))

(-main (slurp *in*))