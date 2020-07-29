;https://github.com/dakrone/cheshire
;https://github.com/Raynes/fs/
;https://github.com/dakrone/clj-http

(ns weather.core
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
      [weather.weather40 :refer [get-weather2]]
    ; [cljs.js :as cljs.js]
    ; [clojure.java.io :as io]
    ; [me.raynes.fs :refer :all]
    ; [me.raynes.fs.compression :refer :all]
   )  
   (:use [clojure.java.io])
   (:import java.io.File)
   (:import [java.net URL])
   (:import java.util.Date)
   ;(import (org.apache.http.entity.mime HttpMultipartMode))
 )


(defn -main
  "dddd"
  [& args]
   (get-weather2)
  )
