(ns noumenon-site.content.concepts.source-control
  "Source-control concept page covering Git and Perforce ingestion.")

(def identifier-rows
  [["Local path"           "/path/to/repo"
    "Use the working tree as-is. No clone, no network."]
   ["Git URL"              "https://github.com/owner/repo.git"
    "Cloned to data/repos/<repo-name> on first use, reused thereafter."]
   ["Perforce depot path"  "//depot/Project/main/..."
    "Bridged through git-p4. Cloned to data/repos/Project-main."]
   ["Database name"        "noumenon"
    "Already-imported repo, looked up by canonical name."]])

(defn- code-block [lang body]
  [:pre [:code {:data-lang lang} body]])

(defn- prose-body []
  [:div.prose
   [:h2 {:id "identifiers"} "Four Kinds of Repo Identifier"]
   [:p
    "Every Noumenon command that takes a repo argument ("
    [:code "noum digest"] ", " [:code "noum ask"] ", " [:code "noumenon_query"]
    ", and the HTTP endpoints) accepts any of the four. The dispatch is a "
    "small cond that checks Perforce depot syntax first, then git URL "
    "shape, then a filesystem path, and falls back to looking the name up "
    "as an existing database."]
   [:table.md-table
    [:thead [:tr [:th "Input"] [:th "Example"] [:th "What happens"]]]
    (into [:tbody]
          (for [[label example body] identifier-rows]
            [:tr [:td label] [:td [:code example]] [:td body]]))]

   [:h2 {:id "git"} "Git"]
   [:p
    "Native. Noumenon shells out to the " [:code "git"]
    " CLI (no libgit2 or JGit) to extract commits, authors, file paths, "
    "parent edges, and per-commit diffs, and writes them into Datomic. "
    "Local repos and remote URLs follow the same code path; URLs are "
    "cloned once into " [:code "data/repos/"] " and reused on subsequent runs."]
   [:p [:strong "What gets imported:"]]
   [:ul
    [:li "Every commit on the configured branch (default: HEAD)."]
    [:li "Author name and email as written in the commit. No alias merging or identity unification yet."]
    [:li "Files touched per commit, including renames and deletions."]
    [:li "Per-commit additions, deletions, and changed-file lists from " [:code "git log --numstat"] "."]]
   [:p
    "Subsequent " [:code "noum update"] " calls are incremental. "
    "Only commits newer than the last imported HEAD are processed."]

   [:h2 {:id "perforce"} "Perforce"]
   [:p
    "Bridged through " [:code "git-p4"] ". A depot path that starts with "
    [:code "//"] " triggers a " [:code "git p4 clone"] " into "
    [:code "data/repos/<derived-name>"] ". Once cloned, it's a git repo "
    "and the rest of the pipeline runs unchanged."]
   (code-block "bash" "noum digest //depot/ProjectA/main/...")
   [:p [:strong "Requirements:"]]
   [:ul
    [:li [:code "git p4"] " on the PATH (ships with most Git for Windows builds; on macOS via "
     [:code "brew install git"] " plus Python; on Linux often a separate package)."]
    [:li "A working " [:code "P4PORT"] " / " [:code "P4USER"] " environment, the same as you'd use for the " [:code "p4"] " CLI."]]
   [:p [:strong "Tuning the clone:"]]
   [:ul
    [:li [:code "--use-client-spec"] " clones exactly the workspace view configured in your P4 client. Ignores Noumenon's default exclusions."]
    [:li [:code "--max-changes N"] " limits history depth. Useful for huge depots; defaults to full history."]
    [:li [:code "--p4-include \"*.uasset\""] " / " [:code "--p4-exclude \"*.custom\""]
     " adjust the binary-asset exclusion list."]
    [:li [:code "--no-default-excludes"] " skips the built-in exclusions entirely."]]
   [:p
    "The default exclusions strip common game-engine binaries: Unreal "
    [:code ".uasset"] "/" [:code ".umap"] ", Unity " [:code ".prefab"]
    "/" [:code ".asset"] ", and the " [:code ".fbx"] "/" [:code ".png"] "/"
    [:code ".wav"] "/" [:code ".mp4"] " families. The clone stays small "
    "and the LLM sees source code rather than assets. Override per-import when you need "
    "a specific binary type indexed."]
   [:p
    [:code "noum update"] " on a git-p4 clone runs " [:code "git p4 sync"]
    " followed by " [:code "git p4 rebase"] " before the incremental import, "
    "so new changelists land in the graph the same way new git commits do."]

   [:h2 {:id "branches"} "Branches and Local Deltas"]
   [:p
    [:em "Experimental — interfaces may change between releases."] " A "
    "Noumenon database tracks one branch at a time. Each database carries "
    "branch metadata (" [:code ":branch/name"] ", " [:code ":branch/kind"]
    " — " [:code ":trunk"] " / " [:code ":feature"] " / " [:code ":release"]
    ", " [:code ":branch/vcs"]
    ") and the repo entity points to the current branch via "
    [:code ":repo/branch"]
    ". Trunk is hosted (one shared knowledge graph for the team); a "
    "developer's working branch lives in a sparse "
    [:em "delta DB"] " on the developer's own machine."]
   [:p
    "When local " [:code "git rev-parse HEAD"]
    " diverges from the trunk DB's " [:code ":repo/head-sha"] ", "
    [:code "noum delta-ensure"]
    " (or POST " [:code "/api/delta/ensure"] ") materializes a delta DB at "
    [:code "~/.noumenon/deltas/<repo>__<safe-branch>__<basis7>/"]
    " containing only the files added, modified, or deleted vs the trunk "
    "basis SHA. Deletions are stored as " [:code ":file/deleted? true"]
    " tombstones rather than retracted, so federated queries can subtract "
    "them cleanly."]
   [:p [:strong "Federated answers."]
    " A " [:em "federation-safe"]
    " named query (those flagged " [:code ":federation-safe? true"]
    " in their EDN) can run merged across trunk and a delta in a single "
    "HTTP roundtrip via " [:code "/api/query-federated"]
    ". The server overlays the delta on trunk by injecting "
    [:code "(not [?file :file/path \"<p>\"])"]
    " clauses for each delta path, then concatenates the delta's own rows "
    "on top. The launcher detects divergence automatically and reroutes "
    [:code "noum query"]
    " transparently — a yellow banner makes the rerouting observable. "
    "Disable per-call with " [:code "--no-auto-federate"] " or persistently "
    "with " [:code "noum settings federation/auto-route false"] "."]
   [:p [:strong "Why server-side."]
    " Federation lives in the daemon because the Babashka launcher does "
    "not carry datomic-client. One HTTP roundtrip beats coordinating "
    "multiple from a language without direct DB access. Delta DBs require "
    "a co-located daemon for the same reason — full remote-mode support "
    "is a future iteration."]
   [:p [:strong "Throwaway by design."]
    " Delta DBs are wipe-and-rebuild on schema mismatch or basis drift; "
    "no migrations runner. " [:code "bb prune-deltas"]
    " interactively GCs orphan directories whose trunk DB has been deleted."]
   [:p [:strong "Content addressing for free."]
    " Files now carry " [:code ":file/blob-sha"]
    " from " [:code "git ls-tree"] ". The "
    [:a {:href "/concepts/pipeline/#analyze"} "analyze stage"]
    " uses this for content-addressed promotion: when a file's blob has "
    "been analyzed before with the current prompt + model, the prior "
    "analysis is copied across instead of paying the LLM again."]

   [:h2 {:id "downstream"} "Same Graph, Regardless of Source"]
   [:p
    "Once the working tree is on disk, the rest of the pipeline (enrich, "
    "analyze, synthesize, embed) does not know or care which source-control "
    "system the repo came from. Queries, the Ask agent, MCP tools, and the "
    "knowledge graph schema are identical in either case."]

   [:div.callout
    [:p
     "The four-input dispatch lives in "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/src/noumenon/repo.clj"}
      "src/noumenon/repo.clj"]
     "; the Perforce bridge details are in "
     [:a {:href "https://github.com/leifericf/noumenon/blob/main/src/noumenon/git.clj"}
      "src/noumenon/git.clj"]
     "."]]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Source Control"]
    [:p.lead
     "Noumenon ingests git history into the knowledge graph. Native git "
     "works out of the box, and Perforce works through git-p4. Once the tree "
     "is on disk, everything downstream is identical."]
    (prose-body)]])
