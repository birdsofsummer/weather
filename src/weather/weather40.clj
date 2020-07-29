;https://github.com/dakrone/cheshire
;https://github.com/Raynes/fs/
;https://github.com/dakrone/clj-http

(ns weather.weather40
   (:require 
      [clj-http.client :as client]
      [http.async.client :as http]
      [cheshire.core :refer :all]
      [somnium.congomongo :as m]
      [clojure.string :as string]
      [clojure.data.json :as json]
      [cheshire.core :as json1]
      [clj-http.conn-mgr :as conn-mgr]
      [net.cgrand.enlive-html :as html]
    ; [cljs.js :as cljs.js]
    ; [clojure.java.io :as io]
    ; [me.raynes.fs :refer :all]
    ; [me.raynes.fs.compression :refer :all]
   )  
  (:refer-clojure
   :exclude [compile take drop sort distinct conj! disj! case])

  (:use [clojure.java.io])

  (:use [clojureql.internal :only [update-or-insert-vals update-vals]]
        [clojure.java.jdbc ] ;:only [with-connection find-connection]
        clojureql.core
        [clojure.java.io :only [delete-file]]
        ;clojure.contrib.mock
        )
 ;  (:import (javax.sql DataSource))
   (:import java.io.File)
   (:import [java.net URL])
   (:import java.util.Date)
   (:import java.io.Closeable
           java.sql.Connection)

   ;(import (org.apache.http.entity.mime HttpMultipartMode))
 )


;(md "/tmp/a/b/c")
(defn md [p] 
    (println "mkdir " p)
    (def a (string/split p #"/" ))
    (def n (count a))
    (doseq [x (range 1 (+ 1 n))] 
      (def f (string/join "/" (take x a)))
      (println f)
            (try
                (.mkdir (java.io.File. f)) 
                (catch Exception e (str "fail to mkdir " (.getMessage e))))

      )
  )


(defn now [] 
   (.getTime (java.util.Date.)))

(defn today []
  (.format (java.text.SimpleDateFormat. "yyyyMMdd") (System/currentTimeMillis))
)

(defn pick1 [k] 
  (fn [x] 
    (map #(get x %) k) 
    ) 
)

(defn keys1 [x]
  (->> x first keys)
)

; (slurp "http://www.baidu.com" :encoding "utf-8")
; (slurp "json/weather.json")
( defn write-json [d f] 
 ( let [t   (json1/encode d {:pretty true})] 
       (spit f t  :append false)
   )
)


(def mongo "mongodb://localhost:27017")
(defn conn []
   ( let 
     [c (m/make-connection mongo)]
     (m/set-connection! c)
     (println (m/databases))
     (m/set-database! "weather")
     (m/set-database! "sz")
     c
   )
)

(defn save [d]
  (let 
    [c (conn)]
    (dorun (m/mass-insert! :points d))
    (m/close-connection c)
  )
)


(defn get-baidu []
;{:type "hidden", :name "bdorz_come", :value "1"}
  (let [
     z (html/html-resource (URL. "https://www.baidu.com"))
     a (map #(:name (:attrs %)) (html/select z [:input]))
     b (map #(:value (:attrs %)) (html/select z [:input]))
     c (:name (:attrs (first (html/select z [:input]))))
     d (vec (map (fn [x] (#(-> [(:value %) (:name %)] vec) (:attrs  x))) (html/select z [:input])))   
  ]
  (println a)
  (println b)
  (println c)
  (println d)
  )
)

(defn get-weather1 []
  (let [
        u "http://www.weather.com.cn/weather40dn/101280601.shtml",
        q {
           ;"Callback" "bShare.viewcb",
            "h"  "",
            "uuid"  "",
            "sc"  "1",
            "l"  "17",
            "lite"  "1",
            "ot"  "【深圳天气】深圳今天天气预报,今天,今天天气,7天,15天天气预报,天气预报一周,天气预报15天查询",
            "cs"  "UTF-8",
            "kws"  "深圳天气预报,深圳今日天气,深圳周末天气,深圳一周天气预报,深圳15日天气预报,深圳40日天气预报"
        }
        h {
            :User-Agent "Mozilla/5.0 (X11; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0", 
            :Accept "*/*", 
            :Accept-Language "en-US,en;q=0.5", 
            :Pragma "no-cache", 
            :Cache-Control "no-cache",
            :Referer "http://www.weather.com.cn/weather40dn/101280601.shtml",
         ;   :foo ["bar" "baz"], 
         ;   :eggplant "quux",
        }
        o {
            :headers h 
            :query-params q
        }
        body (:body (client/get u o))
    ]
        body
    )
)


;js-eval https://github.com/clojure/clojurescript/blob/9778b34d9e988a28c64133c4751d235bbbd3e966/src/main/cljs/cljs/js.cljs#L74
;example
; {
;  "alins": "斋醮-祈福-栽种-掘渠-放水",
;  "als": "嫁娶-移徙-纳财-入宅-开市",
;  "c1": "04",
;  "c2": "301",
;  "cla": "d15 pre",
;  "cloud1": "44.9",
;  "cloud2": "82.7",
;  "date": "20200730",
;  "fch": "20",
;  "fe": "",
;  "h4max": "32",
;  "h4min": "26",
;  "hgl": "57%",
;  "hmax": "32",
;  "hmin": "25",
;  "hol": "",
;  "hp": "0",
;  "insuit": "cl,dy,ls,pk,xc,yh",
;  "jq": "",
;  "max": "32",
;  "maxobs": "",
;  "min": "27",
;  "minobs": "",
;  "nl": "初十",
;  "nlyf": "六月",
;  "r": "f",
;  "rain1": "6.7",
;  "rain2": "1.8",
;  "rainobs": "",
;  "rhmax": "92.6",
;  "rhmin": "77.8",
;  "suit": "nl,pj",
;  "t1": "",
;  "t1t": "",
;  "t2": "",
;  "t3": "",
;  "t3t": "",
;  "time": "18:00",
;  "tm": "20200730",
;  "today": "",
;  "update": "yes",
;  "w1": "雷阵雨转雨",
;  "wd1": "<3级",
;  "wd1_code": "0",
;  "wd2_code": "2",
;  "winter": "中伏 第 5 天",
;  "wk": "四",
;  "wor": "",
;  "ws1": "",
;  "ws1_code": "0",
;  "ws2_code": "0",
;  "yl": ""
;}


(defn get-weather2 []
  "http://www.weather.com.cn/weather40dn/101280601.shtml"  ;空
  (let [
        year 2020
        id1 101280601
        id2 202007
        u (format "http://d1.weather.com.cn/calendarFromMon/%s/%s_%s.html"  year id1 id2) 
        h {
            :User-Agent "Mozilla/5.0 (X11; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0", 
            :Accept "*/*", 
            :Accept-Language "en-US,en;q=0.5", 
            :Pragma "no-cache", 
            :Cache-Control "no-cache",
            :Referer "http://www.weather.com.cn/weather40dn/101280601.shtml",
         ;   :foo ["bar" "baz"], 
         ;   :eggplant "quux",
        }
        o {
            :headers h 
          }
        body (:body (client/get u o))
        text (string/trim (second (string/split body #"=")))
        j (json/read-str text)
        kk (->> j first keys)
        j1 (map (pick1 ["date" "min" "max" "w1"]) j)
        today-weather (filter #(= (get % "date") (today)) j )
        dir "json"
        file-name (format  "%s/weather.json" dir) 
        file-name1 (format "%s/%s_weather.json" dir (today))
        ]
       (println kk)        
       (println j1)        
       (println file-name)
       (println file-name1)
       (write-json j file-name)
       (write-json today-weather file-name1)
       ;(save j)
       j 
   )
)


;http://cgrand.github.io/enlive/syntax.html
(defn get-weather [] 
 (
   let [ 
        u  "http://www.weather.com.cn/weather40dn/101280601.shtml"  ;空
        u1 "http://d1.weather.com.cn/calendarFromMon/2020/101280601_202007.html" ;var fc40 = 
        u2 "http://d1.weather.com.cn/dingzhi/101280601.html"
        u3 "http://d1.weather.com.cn/index_around_2017/101280601.html"
        t (html/html-resource (URL. u))
        yangli (html/text (html/select t [:span#yangli]))
        table (html/select t [:table])
        td (html/select table [:td])
        td1 (map #(:content %)  td)
   ]
   (print yangli)
  )
)













;;;mysql;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn mysql? []
  (isa? (class (connection)) com.mysql.jdbc.JDBC4Connection))

; https://github.com/LauJensen/clojureql/blob/master/test/test.clj
(def mysql
  {
   :classname   "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :user        "root"
   :password    "123456"
   :auto-commit true
   :fetch-size  1500
   :subname     "//localhost:3306/test"
 })

(def c (open-global mysql))

(defn drop-if [table]
  (try (drop-table table) (catch Exception _)))


(defn drop-schema []
  ;(drop-if :salary)
  (drop-if :user)
  )



;;;;;;;;;;;;;;;;;;;;;;;;crud;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;rs (table :user)
;rs (select (table :user) (where (= :id 5)))
;rs (project (table :user) [:name])
;rs (sort (table :user) [:id :name])
;rs (table :user)] (println rs) rs))
;rs (project (table :user) [:name])
;rs (clojureql.core/sort (table :user) [:id#desc :name#asc])

;select table_name from information_schema.tables where table_schema='test' and table_type='base table';

;SELECT GROUP_CONCAT(COLUMN_NAME SEPARATOR ",") 
;FROM information_schema.COLUMNS 
;WHERE 
;TABLE_SCHEMA = 'test' 
;AND 
;TABLE_NAME = 'user'

;  :update_time 
;  :table_comment 
;  :avg_row_length 
;  :table_collation 
;  :table_type 
;  :table_schema 
;  :row_format 
;  :auto_increment 
;  :data_length 
;  :table_rows 
;  :check_time 
;  :checksum 
;  :index_length 
;  :create_time 
;  :create_options 
;  :engine 
;  :table_catalog 
;  :max_data_length 
;  :version 
;  :table_name 
;  :data_free

; (query-tables "test")
(defn query-tables [db]
  (with-connection mysql 
  (with-results 
    [
     rs (-> (table :information_schema.tables) 
            (select 
               (where (
                       and 
                       (= :table_schema db) 
                       (= :table_type "base table")
                       ) 
                ) 
             )
            ) 
     ] 
    (println rs) 
    rs
    )
   )
)


(defn query-tables1 [[db-name config]]
  (with-connection config
  (with-results 
    [
     rs (-> (table :information_schema.tables) 
            (select 
               (where (
                       and 
                       (= :table_schema db-name) 
                       (= :table_type "base table")
                       ) 
                ) 
             )
            ) 
     ] 
    (println rs) 
    rs
    )
   )
)


(defn query1 [db-name config {table-name :table_name}]
  (with-connection config 
    (with-results [rs (-> 
                        (table table-name) 
                        ;(clojureql.core/sort [:id#desc :name#asc]) 
                        ;(clojureql.core/take 50)
                        ;(clojureql.core/drop 2)
                        )] 
      ;(doseq [r rs] (println r))
      (println rs)
      rs
      )
   )
)

(defn query [table-name]
  (with-connection mysql 
    (with-results [rs (-> 
                        (table table-name) 
                        ;(clojureql.core/sort [:id#desc :name#asc]) 
                        ;(clojureql.core/take 50)
                        ;(clojureql.core/drop 2)
                        )] 
      ;(doseq [r rs] (println r))
      (println rs)
      rs
      )
   )
)

(defn export-db [db] 
    ( 
     ->>
      (query-tables db)
      (map :table_name )
      (map (fn [x] {x (query x)}))
      (into {})
))

(defn  write-json1 [[file_name data]] 
  (
     let [ 
           p "/tmp/db/"
           n (str p file_name ".json") 
     ]
    (spit  n (json1/encode data {:pretty true}) :append false)      
))

(defn find-db-host [config]
  (-> 
    (:subname config)
    (string/split #":" ) 
    first 
    (string/replace "//" "")
    )
  )

(defn find-db-name [config] 
  (-> 
    (:subname config) 
    (string/split #"/") 
    last
    (string/replace  #"\?.*" "" )
    ))


; (export-dbs [mysql])
(defn export-dbs [dbs] 
      (def configs  (->> (map (fn [x] [ (find-db-name x) x]) dbs) (into {})))
      (def all-tables 
           (map (fn [[db-name config]]
                 (def tables (query-tables1 [db-name config ]))
                 [db-name config tables]   
            )
            configs
           ))
      (map  (fn [[db-name config tables]] 
                 (map (fn [x]  
                        (
                         let [ 
                               host-name (find-db-host config)
                               table-name (:table_name x)
                               p (str "/tmp/db/" host-name "/" (today) "/" db-name  "/") 
                               file-name (str p table-name ".json") 
                         ]
                        (println db-name table-name p file-name)
                        (md p)
                        (def table-data (query1 db-name config x))
                            (println "save to " file-name)
                            (spit  file-name (json1/encode table-data {:pretty true}) :append false)      
                         )
                        ) tables)
                 ) all-tables)
)


(defn export-test []
    (map write-json1 (export-db "test"))
)

(defn query2 [table-name]
  (with-connection mysql 
    (with-results [
          rs (-> (table table-name) (select (where (> :id 5))))
       ] 
      ;(doseq [r rs] (println r))
      (println rs)
      rs
      )
   )
)

(defn update-user [name]
  (with-connection mysql 
    (with-results [
                   rs (update! (table :user) (where (> :id 1)) {:name name})
                  ;rs (update-in! (table :user) (where (> :id 1)) {:name "Bob"})
                   ] 
      (println rs)
      ;(doseq [r rs] (println r))
      rs
      )
   )
)

(defn add-user [name,n]
  (
   let [d (for [x (range n)] {:id x :name name})]
  (with-connection mysql 
    (with-results [rs (clojureql.core/conj! (table :user) d)] 
      (println rs)
      ;(doseq [r rs] (println r))
      rs
   ))
))


(defn del-user []
  (with-connection mysql 
    (with-results [rs (clojureql.core/disj! (table :user) (where (> :id -10)))] 
      ;(doseq [r rs] (println r))
      (println rs)
      rs
    )
   )
)




;;;;;;;;;;;;;;;;;;;;;;;;crud;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
;(def databases [mysql postgresql sqlite3 derby])
;(def postgresql
;  {:classname   "org.postgresql.Driver"
;   :subprotocol "postgresql"
;   :user        "cql"
;   :password    "cql"
;   :auto-commit true
;   :fetch-size  500
;   :subname     "//localhost:5432/cql"})

;(def sqlite3
;  {:classname   "org.sqlite.JDBC"
;   :subprotocol "sqlite"
;   :subname     "/tmp/cql.sqlite3"
;   :create      true})


;;;mysql;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;







