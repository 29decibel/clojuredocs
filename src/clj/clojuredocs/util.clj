(ns clojuredocs.util
  (:require [cheshire.core :as json]
            [clojure.string :as str]))

(defn url-encode [s]
  (when s
    (java.net.URLEncoder/encode s)))

(defn to-json [o]
  (json/generate-string o))

(defn from-json [s]
  (json/parse-string s true))

(defn uuid []
  (-> (java.util.UUID/randomUUID)
      str
      (str/replace #"-" "")))

(defn md5
  "Compute the hex MD5 sum of a string."
  [#^String str]
  (when str
    (let [alg (doto (java.security.MessageDigest/getInstance "MD5")
                (.reset)
                (.update (.getBytes str)))]
      (try
        (.toString (new BigInteger 1 (.digest alg)) 16)
        (catch java.security.NoSuchAlgorithmException e
          (throw (new RuntimeException e)))))))

(defn bson-id
  ([]
     (org.bson.types.ObjectId.))
  ([id-or-str]
     (org.bson.types.ObjectId/massageToObjectId id-or-str)))

(defn now [] (System/currentTimeMillis))

(defn timeago [millis]
  (when millis
    (let [millis (if (number? millis)
                   millis
                   (.getTime millis))
          ms (- (now) millis)
          s (/ ms 1000)
          m (/ s 60)
          h (/ m 60)
          d (/ h 24)
          y (/ d 365.0)]
      (cond
        (< s 60) "less than a minute"
        (< m 2) "1 minute"
        (< h 1) (str (int m) " minutes")
        (< h 2) "1 hour"
        (< d 1) (str (int h) " hours")
        (< d 2) "1 day"
        (< y 1) (str (int d) " days")
        :else (str (format "%.1f" y) " years")))))

(defn munge-name [s]
  (-> s
      str
      (str/replace #"\." "_dot")
      (str/replace #"\/" "_div")))

(defn unmunge-name [s]
  (-> s
      (str/replace #"_dot" ".")
      (str/replace #"_div" "/")
      (str/replace #"_qm" "?")))

(defn $var-link [ns name & contents]
  (vec (concat
         [:a {:href (str "/" ns "/" (munge-name name))}]
         contents)))

(defn pluralize [n single plural]
  (str n " " (if (= 1 n) single plural)))
