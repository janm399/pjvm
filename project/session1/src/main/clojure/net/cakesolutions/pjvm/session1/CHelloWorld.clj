(ns net.cakesolutions.pjvm.session1.CHelloWorld)

(println "Hello, world")

(import
;  'net.cakesolutions.pjvm.session1.GGreeter
;  'net.cakesolutions.pjvm.session1.SGreeter
  'net.cakesolutions.pjvm.session1.JGreeter)

; from Clojure to

; * Java:
(println (.greeting (new JGreeter)) )  ; Works!

; * Groovy:
; (println (.greeting (new GGreeter)))  ; Does not compile in same module

; * Scala:
; (println (.greeting (new SGreeter)) ) ; Does not compile in same module