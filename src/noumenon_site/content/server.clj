(ns noumenon-site.content.server
  "Server-mode deploy page: conceptual intro plus the DEPLOY.md mirror."
  (:require [noumenon-site.parse.deploy :as parse]))

(defn- intro []
  [:div.prose
   [:h2 {:id "why"} "Why Run a Shared Instance"]
   [:p
    "A team using the same codebase doesn't each need their own knowledge "
    "graph. A shared Noumenon daemon holds one Datomic database per repo, "
    "everyone queries it, and the analyze cost is paid once instead of N times."]
   [:ul
    [:li
     [:strong "Consistent answers."]
     " The Ask agent, MCP tools, and direct queries all read from the same "
     "graph, so different teammates get the same view of the same code."]
    [:li
     [:strong "Cost amortization."]
     " The analyze stage is the expensive one. Run it once on the server, "
     "every reader benefits."]
    [:li
     [:strong "Always-fresh data."]
     " Webhooks or a polling loop keep the graph current without anyone "
     "running " [:code "noum update"] "."]
    [:li
     [:strong "No client setup beyond a token."]
     " " [:code "noum connect <url> --token <t>"] " makes every CLI command "
     "and MCP tool route through the remote daemon. Local repo paths are "
     "translated to canonical names, so " [:code "noum ask ./my-repo \"...\""]
     " works against a shared graph."]]

   [:h2 {:id "shape"} "How It's Deployed"]
   [:p
    "One Docker container per instance. Bind mount a data volume for the "
    "Datomic store and the cloned repos, set " [:code "NOUMENON_TOKEN"]
    " to a random secret, and you're done. Front it with Caddy or Nginx "
    "if you want TLS and a public hostname."]
   [:p
    "Two roles. The admin token registers repos, mints reader tokens, and "
    "triggers re-imports. Reader tokens are read-only. Tokens are HMAC-prefix "
    "values stored in the same Datomic database; rotate them at any time "
    "without restarting the server."]
   [:p
    "Federated, not multi-tenant. Each instance is independent. There's no "
    "user identity beyond tokens, no single sign-on, no cross-instance graph "
    "sharing. Want a per-team or per-product split? Run multiple instances; "
    "they don't know about each other."]

   [:h2 {:id "refresh"} "Webhook Refresh"]
   [:p
    "Point GitHub or GitLab at " [:code "/api/webhook"] " and the daemon "
    "re-imports on every push. Set " [:code "NOUMENON_WEBHOOK_SECRET"]
    " and the server verifies the HMAC signature ("
    [:code "X-Hub-Signature-256"] " on GitHub, "
    [:code "X-Gitlab-Token"] " on GitLab) before doing anything."]
   [:p
    "If webhooks aren't an option, the server falls back to a polling "
    "loop on " [:code "NOUMENON_POLL_INTERVAL"] " minutes. Set it to "
    [:code "0"] " to disable polling entirely (recommended once webhooks "
    "are wired up). The configuration table and the exact request shape "
    "are in the Webhook Setup section of the deploy guide below."]

   [:p
    "Hardening tip: run with " [:code "NOUMENON_RUNTIME_MODE=service"]
    " so provider credentials are env-only and provider base URLs are "
    "required to be HTTPS. See "
    [:a {:href "/concepts/data-safety/#runtime-modes"} "Runtime Modes"]
    "."]

   [:h2 {:id "ops"} "Day-Two Operations"]
   [:p
    "The DEPLOY.md mirror below covers the operational specifics: env vars "
    "(bind address, port, log format, concurrency, poll interval), reverse-proxy "
    "snippets for Caddy and Nginx, monitoring via " [:code "/health"]
    " plus structured-JSON logs, backup and restore via volume snapshot, "
    "rolling upgrades, and webhook setup with HMAC verification."]])

(defn page []
  [:section.docs
   [:div.container
    [:h1.docs-title "Run Noumenon as a Shared Service"]
    [:p.lead
     "Deploy a centralized Noumenon instance for your team. One graph per "
     "repo, role-based tokens, and every CLI command and MCP tool routes "
     "through it transparently."]
    (intro)
    [:hr.divider {:style "margin: 2rem 0;"}]
    (if-let [blocks (parse/parsed)]
      (into [:div.prose] blocks)
      [:p [:em "Deploy guide source not available."]])]])
