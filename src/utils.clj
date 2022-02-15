(ns utils
  (:import 
   (java.time.format DateTimeFormatter)
   (java.time Instant)
   (java.time ZoneId)))

(defn rfc-3339 [date]
  (str (.toInstant date)))

(defn rfc-3339-now []
  (str (Instant/now)))

(def display-formatter (DateTimeFormatter/ofPattern "dd.MM.yyyy"))
(def month-formatter (DateTimeFormatter/ofPattern "MMMM yyyy"))

(defn date-str [date formatter]
  (let [local-date
        (-> date
            .toInstant
            (.atZone (ZoneId/of "UTC+1"))
            .toLocalDate)]
    (.format local-date formatter)))

(defn date-tag [date]
  [:time {:datetime (rfc-3339 date)} (date-str date display-formatter)])

(defn month-tag [date]
  [:time {:datetime (rfc-3339 date)} (date-str date month-formatter)])

(defn abbreviate [s]
  (if (< 110 (count s))
    (apply str (take 109 s) "…")
    s))