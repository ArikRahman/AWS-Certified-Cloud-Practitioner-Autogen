#!/usr/bin/env bb

(require '[babashka.fs :as fs]
         '[babashka.process :refer [shell]]
         '[clojure.string :as str])

;; Define the logical reading order for the book.
;; We list these manually to ensure the flow matches the exam domains
;; rather than alphabetical order.
(def file-order
  ["study-guide.md"
   "sections/cloud_computing.md"
   "sections/iam.md"
   "sections/ec2.md"
   "sections/ec2_instance_storage.md"
   "sections/elb_asg.md"
   "sections/s3.md"
   "sections/databases_analytics.md"
   "sections/other_compute.md"
   "sections/deploying_managing.md"
   "sections/global_infrastructure.md"
   "sections/cloud_integration.md"
   "sections/cloud_monitoring.md"
   "sections/vpc.md"
   "sections/security_compliance.md"
   "sections/machine_learning.md"
   "sections/billing_support.md"
   "sections/advanced_identity.md"
   "sections/other_aws_services.md"
   "sections/architecting_ecosystem.md"])

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
         "--output" "aws-clf-c02-notes.epub"
         "--metadata" "title=AWS Cloud Practitioner Notes (CLF-C02)"
         "--toc"))

(println "Done! Generated aws-clf-c02-notes.epub")
