#!/usr/bin/env bb

(require '[babashka.fs :as fs]
         '[babashka.process :refer [shell]]
         '[clojure.string :as str])

;; Define the logical reading order for the book.
;; We list these manually to ensure the flow matches the exam domains
;; rather than alphabetical order.
(def file-order
  ["practice-exam/practice-exam-1.md"
   "practice-exam/practice-exam-2.md"
   "practice-exam/practice-exam-3.md"
   "practice-exam/practice-exam-4.md"
   "practice-exam/practice-exam-5.md"
   "practice-exam/practice-exam-6.md"
   "practice-exam/practice-exam-7.md"
   "practice-exam/practice-exam-8.md"
   "practice-exam/practice-exam-9.md"
   "practice-exam/practice-exam-10.md"
   "practice-exam/practice-exam-11.md"
   "practice-exam/practice-exam-12.md"
   "practice-exam/practice-exam-13.md"
   "practice-exam/practice-exam-14.md"
   "practice-exam/practice-exam-15.md"
   "practice-exam/practice-exam-16.md"
   "practice-exam/practice-exam-17.md"
   "practice-exam/practice-exam-18.md"
   "practice-exam/practice-exam-19.md"
   "practice-exam/practice-exam-20.md"
   "practice-exam/practice-exam-21.md"
   "practice-exam/practice-exam-22.md"
   "practice-exam/practice-exam-23.md"])

(defn process-content [file]
  (let [content (slurp file)]
    ;; Files in 'sections/' reference images as "../images/file.png".
    ;; We strip the "../" so the path becomes "images/file.png",
    ;; which is valid relative to the repo root where we run the epub build.
    (str/replace content "../images" "images")))

(println "Reading and processing files...")

;; Combine all markdown files into a single stream
(let [full-book (->> file-order
                     (filter fs/exists?) ;; Skip if a file is missing/renamed
                     (map process-content)
                     (str/join "\n\n"))]

  (println "Generating EPUB with Pandoc...")

  ;; Pipe the processed content directly into pandoc via stdin
  (shell {:in full-book}
         "pandoc"
         "--from" "markdown"
         "--to" "epub"
         "--output" "AWS Cloud Practitioner Practice Exams.epub"
         "--metadata" "title=AWS Cloud Practitioner Practice Exams (CLF-C02)"
         "--toc"))

(println "Done! Requested epub")
