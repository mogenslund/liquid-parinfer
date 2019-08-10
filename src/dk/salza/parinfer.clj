(ns dk.salza.parinfer
  ( :require [dk.salza.liq.editor :as editor]
             [dk.salza.liq.slider :refer :all])
  (:import [com.oakmac.parinfer Parinfer]))

(def old-insertmode (atom nil))

(defn beginning-of-toplevel
  [sl]
  (loop [s sl]
    (let [c (get-char s)
          cb (get-char (left s))]
      (if (or
            (beginning? s)
            (and (not= c " ") (not= c "\n") (= cb "\n")))
        s
        (recur (left s))))))

(defn end-of-toplevel
  [sl]
  (loop [s sl]
    (let [c (get-char s)
          cn (get-char (right s))]
      (if (or
            (end? s)
            (and (= c "\n") (not= cn "\n") (not= cn " ")))
        s
        (recur (right s))))))

(defn get-column
  [sl]
  (- (get-point sl) (get-point (beginning-of-line sl))))

(defn forward-line-n
  [sl n]
  ((apply comp (repeat n forward-line)) sl)) 

(defn par-indent-toplevel
  [sl]
  (let [col (get-column sl)
        lnr (get-linenumber sl)
        sl0 (-> sl
                end-of-toplevel
                (set-mark "parinfer")
                beginning-of-toplevel)
        substring (get-region sl0 "parinfer")
        modified (Parinfer/indentMode substring (int col) (int (- lnr (get-linenumber sl0))) nil false)]
     (-> sl0
         (delete-region "parinfer")
         (insert (.text modified))
         beginning-of-toplevel
         (forward-line-n (- lnr (get-linenumber sl0)))
         (right col)
         (remove-mark "parinfer"))))

(defn par-indent-right
  [sl]
  (let [col (get-column sl)]
    (-> sl
        beginning-of-line
        (insert " ")
        par-indent-toplevel
        (right col))))

(defn par-indent-left
  [sl]
  (let [col (get-column sl)
        sl0 (beginning-of-line sl)]
    (if (= (get-char sl0) " ")
      (-> sl0
          right
          (delete 1)
          par-indent-toplevel
          (right (max 0 (- col 1))))
      (par-indent-toplevel sl))))

(defn parinfer-keymap
  []
  (assoc ((@editor/editor ::editor/keymaps) "dk.salza.liq.keymappings.insert")
     "backspace" #(-> % (delete 1) par-indent-toplevel)
     " " #(-> % (insert " ") par-indent-toplevel)
     :selfinsert (fn [string] (editor/apply-to-slider #(par-indent-toplevel (insert % string))))))

(defn toggle-parinfer
  []
  (let [m1 @old-insertmode
        m2 ((@editor/editor ::editor/keymaps) "dk.salza.liq.keymappings.insert")]
    (reset! old-insertmode m2)
    (editor/add-keymap m1)))

(defn run
  []
  (when (not @old-insertmode)
    (reset! old-insertmode (parinfer-keymap)))
  (toggle-parinfer)
  (editor/add-interactive "Toggle parinfer" toggle-parinfer))
