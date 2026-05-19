(ns noumenon-site.content.concepts.data-safety
  "Data-safety concept page covering sensitive-file filtering, what
   gets analyzed, and what is (and isn't) sent to LLMs.")

(def sensitive-rows
  [["Filename starts with " [:code ".env"]
    "Anything that looks like a dotenv file. Allowlisted: .env.example, .env.sample, .env.template."]
   ["Extensions"
    [:span (for [ext ["pem" "key" "p12" "pfx" "keystore" "jks" "cert"]]
             [:span (when-not (= ext "pem") ", ") [:code (str "*." ext)]])]
    "Private keys and certificates."]
   ["Exact filenames"
    [:span (for [name [".npmrc" ".pypirc" ".netrc" ".htpasswd" ".pgpass"
                       "credentials.json" "token.json"]]
             [:span (when-not (= name ".npmrc") ", ") [:code name]])]
    "Well-known credential files."]
   ["Path segments"
    [:code ".ssh/"]
    "Anything inside a .ssh directory at any depth."]
   ["SSH key prefixes"
    [:span [:code "id_rsa*"] ", " [:code "id_ed25519*"] ", " [:code "id_ecdsa*"]]
    "OpenSSH private-key files."]])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "sensitive-files"} "Sensitive Files"]
   [:p
    "Real codebases contain real secrets, especially in their git history. "
    "Noumenon tracks the existence of sensitive files (so commit history "
    "stays accurate) but never reads their contents. The filter runs before "
    "any file content is loaded for analysis or import-graph extraction."]
   [:table.md-table
    [:thead [:tr [:th "Pattern"] [:th "Examples"] [:th "Notes"]]]
    (into [:tbody]
          (for [[pat examples notes] sensitive-rows]
            [:tr [:td pat] [:td examples] [:td notes]]))]
   [:p
    "If your repo has secrets in a non-standard path, the existing list is "
    "easy to extend. Patterns live in "
    [:a {:href "https://github.com/leifericf/noumenon/blob/main/src/noumenon/files.clj"}
     "src/noumenon/files.clj"]
    " under " [:code "sensitive-extensions"] ", " [:code "sensitive-basenames"]
    ", and " [:code "sensitive-path-segments"] "."]

   [:h2 {:id "what-gets-analyzed"} "What Gets Analyzed"]
   [:p
    "Only files with a recognized programming-language extension are candidates "
    "for the analyze stage. Everything else (images, archives, fonts, binaries, "
    "compiled artifacts, lockfiles, config formats Noumenon doesn't understand) "
    "stays in the file index but never goes to the LLM."]
   [:p
    "The recognized set covers the usual suspects: Clojure, Python, JS/TS, Rust, "
    "Java, C#, C/C++, Go, Elixir, Erlang, Ruby, Swift, Kotlin, Scala, Haskell, "
    "OCaml, Lua, R, Perl, PHP, Terraform, Protobuf, GraphQL, plus shell, SQL, "
    "HTML, CSS, JSON, YAML, XML, TOML, EDN, and the MSBuild project formats. "
    "If your language isn't on the list, the file is skipped silently."]
   [:p
    "Perforce clones get a separate exclusion pass for game-engine binaries "
    "(Unreal " [:code ".uasset"] ", Unity " [:code ".prefab"] ", " [:code ".fbx"]
    "/" [:code ".png"] "/" [:code ".wav"] "/" [:code ".mp4"] " families) so "
    "the working tree stays small. See "
    [:a {:href "/concepts/source-control/#perforce"} "Source control"]
    " for the full list and how to override it."]

   [:h2 {:id "llm-exposure"} "What's Actually Sent to the LLM"]
   [:ul
    [:li
     [:strong "Analyze:"]
     " one file's source at a time, only if it passes the sensitive-file "
     "filter and has a recognized language. Output is structured metadata "
     "(complexity, code smells, segments), not free-form text."]
    [:li
     [:strong "Synthesize:"]
     " summaries from earlier analyze passes plus the directory structure. "
     "No raw source. Hierarchical map-reduce keeps prompts bounded."]
    [:li
     [:strong "Ask:"]
     " a TF-IDF-seeded short list of relevant files, plus the question. "
     "The agent can request specific files by path, never the whole tree."]
    [:li
     [:strong "Introspect:"]
     " prompts, examples, and benchmark scores. No user code beyond the "
     "fixed benchmark question set."]]

   [:h2 {:id "delta-locality"} "Branch Deltas Stay Local"]
   [:p
    [:em "Experimental — interfaces may change between releases."] " "
    "When a developer is on a feature branch, Noumenon materializes a "
    "sparse " [:em "delta database"] " under "
    [:code "~/.noumenon/deltas/"]
    " containing only the files that differ from the hosted trunk basis. "
    "The delta lives on the developer's own machine, never on the shared "
    "instance. Federated queries combine trunk + delta in a single HTTP "
    "roundtrip on the local-mode daemon, so the working-branch view never "
    "leaves the laptop. See "
    [:a {:href "/concepts/source-control/#branches"} "Source control"]
    " for the full picture."]

   [:h2 {:id "deployment-shapes"} "Deployment Shapes"]
   [:p
    "There is no runtime-mode toggle. The daemon's bind address is the "
    "deployment signal:"]
   [:ul
    [:li
     [:strong "Local (bind " [:code "127.0.0.1"] ", the default)."]
     " Fine for laptop use. LLM credentials resolve from env vars first, "
     "then fall back to " [:code "~/.noumenon/credentials"]
     " — so " [:code "noum setup"] " is enough; you don't need to "
     [:code "source"] " anything in your shell."]
    [:li
     [:strong "Shared service (bind non-loopback, e.g. " [:code "0.0.0.0"] ")."]
     " The daemon disables the file-credentials fallback at startup, so "
     "LLM credentials must come from env vars on the host. A user's "
     [:code "~/.noumenon/credentials"] " cannot leak into a multi-tenant "
     "deployment. The daemon also warns if " [:code "NOUMENON_LLM_BASE_URL"]
     " is " [:code "http://"] " — terminate TLS at a reverse proxy and "
     "front the upstream call with " [:code "https://"] " in production. "
     "Authentication of the client is unchanged (" [:code "NOUMENON_TOKEN"]
     " and the Datomic-stored token table)."]]
   [:p
    "If you want multi-model flexibility, point " [:code "NOUMENON_LLM_BASE_URL"]
    " at a router (OpenRouter, self-hosted LiteLLM, etc.) and let it handle "
    "provider selection. Noumenon itself never routes."]
   [:p
    "See " [:a {:href "/server/"} "Run as a shared service"] " for how to "
    "wire these into a Docker deployment."]

   [:h2 {:id "transparency"} "Cost Transparency"]
   [:p
    "Every LLM call is recorded in Datomic with the model, the input token "
    "count, the output token count, and an estimated dollar cost. Provider "
    "and model-source provenance go on the transaction too. Per-file "
    "telemetry streams while analyze runs; totals land in the graph when "
    "it finishes."]
   [:p "Three named queries cover the spend axis:"]
   [:ul
    [:li [:code "llm-cost-total"]
     " sums input tokens, output tokens, and dollars across every recorded call."]
    [:li [:code "llm-cost-by-model"]
     " groups the same totals by model."]
    [:li [:code "llm-cost-by-file"]
     " gives per-file analyze cost, most expensive first."]]
   [:p
    "Each runs through " [:code "noum query <name> <repo>"] ", "
    [:code "noumenon_query"] " over MCP, or the HTTP API. The dollar "
    "figure is an " [:em "estimate"] " from a built-in price table — "
    "directional, not invoice-grade."]

   [:div.callout
    [:p
     "If you find a sensitive-file pattern Noumenon should recognize but "
     "doesn't, please "
     [:a {:href "https://github.com/leifericf/noumenon/issues"} "open an issue"]
     ". The blocklist is meant to be conservative, but it's also community-driven."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Data Safety"]
    [:p.lead
     "Real codebases have real secrets in them. Noumenon blocks well-known "
     "sensitive files from analysis, skips binary assets automatically, and "
     "records every LLM call so you can see exactly what was sent and what "
     "it cost."]
    (prose-body)]])
