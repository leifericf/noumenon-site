(ns noumenon-site.highlight
  "Inline JavaScript for client-side syntax highlighting of code blocks.
   Applied to <pre><code data-lang=\"...\"> elements at page load.
   Supports bash, clojure, edn, json — adapted from mino-site.highlight.")

(def highlight-js
  "
(function() {
  function escHtml(s) {
    return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
  }
  function tokenize(code, patterns) {
    var toks = [];
    patterns.forEach(function(p) {
      code = code.replace(p.re, function(m) {
        toks.push('<span class=\"hl-' + p.cls + '\">' + m + '</span>');
        return '\\x00T' + (toks.length-1) + 'T\\x00';
      });
    });
    return [code, toks];
  }
  function restore(code, toks) {
    return code.replace(/\\x00T(\\d+)T\\x00/g, function(_, i) { return toks[parseInt(i)]; });
  }

  function hlBash(code) {
    code = escHtml(code);
    var pair = tokenize(code, [
      { re: /(#[^\\n]*)/g,                     cls: 'comment' },
      { re: /(\"(?:[^\"\\\\]|\\\\.)*\")/g,     cls: 'string' },
      { re: /('(?:[^'\\\\]|\\\\.)*')/g,        cls: 'string' }
    ]);
    code = pair[0];
    code = code.replace(/(\\$\\w+|\\$\\{[^}]+\\})/g, '<span class=\"hl-type\">$1</span>');
    code = code.replace(/\\b(if|then|elif|else|fi|for|while|do|done|case|esac|in|function|return|export|local|set|cd|echo|exit|source|sudo|curl|chmod|mkdir|cp|mv|rm)\\b/g,
      '<span class=\"hl-keyword\">$1</span>');
    code = code.replace(/(^|\\s)(-{1,2}[\\w-]+)/g, '$1<span class=\"hl-number\">$2</span>');
    return restore(code, pair[1]);
  }

  function hlClojure(code) {
    code = escHtml(code);
    var pair = tokenize(code, [
      { re: /(;[^\\n]*)/g,                     cls: 'comment' },
      { re: /(\"(?:[^\"\\\\]|\\\\.)*\")/g,     cls: 'string' }
    ]);
    code = pair[0];
    code = code.replace(/(:[a-zA-Z][a-zA-Z0-9_\\-.*+!?\\/&lt;&gt;]*)/g, '<span class=\"hl-type\">$1</span>');
    code = code.replace(/\\b(\\d+\\.?\\d*)\\b/g, '<span class=\"hl-number\">$1</span>');
    code = code.replace(/(?<=\\()\\b(def|defn|defmacro|let|fn|if|when|when-let|if-let|cond|condp|case|do|loop|recur|try|catch|finally|throw|quote|and|or|->|->>|->|map|filter|reduce|require|ns|nil|true|false)\\b/g,
      '<span class=\"hl-keyword\">$1</span>');
    return restore(code, pair[1]);
  }

  function hlJson(code) {
    code = escHtml(code);
    var pair = tokenize(code, [
      { re: /(\"(?:[^\"\\\\]|\\\\.)*\")\\s*:/g, cls: 'type' },
      { re: /(\"(?:[^\"\\\\]|\\\\.)*\")/g,     cls: 'string' }
    ]);
    code = pair[0];
    code = code.replace(/\\b(true|false|null)\\b/g, '<span class=\"hl-keyword\">$1</span>');
    code = code.replace(/\\b(\\d+\\.?\\d*)\\b/g, '<span class=\"hl-number\">$1</span>');
    return restore(code, pair[1]);
  }

  var highlighters = { bash: hlBash, clojure: hlClojure, edn: hlClojure, json: hlJson };

  document.querySelectorAll('pre code[data-lang]').forEach(function(el) {
    var fn = highlighters[el.getAttribute('data-lang')];
    if (fn) el.innerHTML = fn(el.textContent);
  });

  function attachFilter(inputId, rowSelector, dataKeys) {
    var input = document.getElementById(inputId);
    if (!input) return;
    var rows = document.querySelectorAll(rowSelector);
    input.addEventListener('input', function() {
      var q = input.value.toLowerCase().trim();
      rows.forEach(function(row) {
        if (!q) { row.style.display = ''; return; }
        var match = false;
        for (var i = 0; i < dataKeys.length; i++) {
          var v = (row.getAttribute('data-' + dataKeys[i]) || '').toLowerCase();
          if (v.indexOf(q) !== -1) { match = true; break; }
        }
        row.style.display = match ? '' : 'none';
      });
    });
  }
  attachFilter('queries-filter', '.queries-table tbody tr', ['name', 'desc']);
  attachFilter('schema-filter',  '.schema-table tbody tr',  ['name', 'doc']);
})();
")
