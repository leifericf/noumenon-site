# Noumenon Site

Source for [noumenon.leifericf.com](https://noumenon.leifericf.com). Static HTML/CSS deployed via GitHub Pages.

## Layout

- `index.html`, `style.css`, `favicon.svg` — landing page
- `install` — `curl | bash` install script for the `noum` CLI
- `openapi.yaml` — mirror of [`leifericf/noumenon/resources/openapi.yaml`](https://github.com/leifericf/noumenon/blob/main/resources/openapi.yaml), refreshed daily by `.github/workflows/sync-openapi.yml`
- `CNAME` — custom domain pointer

## Develop

Open `index.html` directly, or serve locally:

```bash
python3 -m http.server 8000
```

## Deploy

Pushes to `main` deploy automatically via `.github/workflows/deploy-pages.yml`.
