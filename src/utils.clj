(ns utils
  (:import 
   (java.time.format DateTimeFormatter)
   (java.time Instant)
   (java.time ZoneId)))

(defn rfc-3339 [date]
  (str (.toInstant date)))

(defn rfc-3339-now []
  (str (Instant/now)))

(defn current-year []
  (subs (rfc-3339-now) 0 4))

(def full-date-formatter (DateTimeFormatter/ofPattern "dd.MM.yyyy"))
(def month-formatter (DateTimeFormatter/ofPattern "MMMM yyyy"))

(defn date-str [date formatter]
  (let [local-date
        (-> date
            .toInstant
            (.atZone (ZoneId/of "UTC+1"))
            .toLocalDate)]
    (.format local-date formatter)))

(defn- html-time-tag [formatter]
  (fn [date]
    (when date
      [:time.mr-2.text-sm.text-slate-500 {:datetime (rfc-3339 date)} (date-str date formatter)])))

(def full-date-tag (html-time-tag full-date-formatter))
(def month-tag (html-time-tag month-formatter))

(defn abbreviate [s]
  (if (< 110 (count s))
    (apply str (take 109 s) "…")
    s))