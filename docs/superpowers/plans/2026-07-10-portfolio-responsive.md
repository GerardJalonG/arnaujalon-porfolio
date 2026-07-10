# Porfolio Arnau Jalón — Plan de Implementación

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Completar el porfolio estático (8 páginas), fiel al PDF de referencia en desktop, con versión móvil nueva (scroll + menú hamburguesa) e imágenes reales extraídas del PDF.

**Architecture:** HTML estático multipágina con Vite 7 + Tailwind CSS v4. Navegación compartida copiada de forma idéntica en cada página (footer horizontal en desktop, overlay hamburguesa en móvil, JS vanilla en `src/main.js`). Desktop = una pantalla por página (como el PDF); móvil = scroll vertical con columnas apiladas.

**Tech Stack:** Vite 7, Tailwind CSS v4 (`@tailwindcss/vite`), JS vanilla, Python (pypdfium2 + Pillow) solo para extraer imágenes del PDF.

**Referencia visual:** renders del PDF en `scratch_pdf/p1.png` … `p8.png` (raíz del repo). Spec: `docs/superpowers/specs/2026-07-10-portfolio-responsive-design.md`.

**Nota sobre tests:** proyecto sin framework de test; la verificación es: (a) `npm run build` sin errores, (b) revisión visual con el dev server (Claude Preview o navegador) a 1280×800 y 375×812 contra los renders del PDF. No aplicar TDD clásico.

---

### Task 1: Extraer imágenes del PDF y convertirlas a WebP

**Files:**
- Create: `portfolio/src/img/*.webp` (19 imágenes, ver mapping)
- Scratch: `scratch_pdf/extracted/` (temporal, no committear)

- [ ] **Step 1: Extraer todas las imágenes embebidas del PDF**

Ejecutar desde la raíz del repo (`C:\arnaujalon-porfolio`):

```python
# scratch_pdf/extract.py
import pypdfium2 as pdfium
import os

os.makedirs('scratch_pdf/extracted', exist_ok=True)
pdf = pdfium.PdfDocument('porfolio-referencia.pdf')
for p in range(len(pdf)):
    page = pdf[p]
    for i, obj in enumerate(page.get_objects(filter=[pdfium.raw.FPDF_PAGEOBJ_IMAGE], max_depth=4)):
        try:
            bitmap = obj.get_bitmap(render=False)
            img = bitmap.to_pil()
            img.save(f'scratch_pdf/extracted/p{p+1}_img{i}.png')
            print(f'p{p+1}_img{i}: {img.size}')
        except Exception as e:
            print(f'p{p+1}_img{i}: ERROR {e}')
```

Run: `python scratch_pdf/extract.py`
Expected: lista de imágenes con tamaños (las fotos reales tienen >500px de lado; iconos pequeños se descartan luego).

- [ ] **Step 2: Inspeccionar y mapear**

Abrir (tool Read) cada `scratch_pdf/extracted/p*_img*.png` grande y asignarla según este mapping (por página del PDF):

| Página PDF | Contenido | Nombre destino |
|---|---|---|
| p3 | perro border collie con arnés azul | `home-dog.webp` |
| p3 | torso con prenda SK-016 | `home-fashion.webp` |
| p3 | mueble Kettal pérgola | `home-furniture.webp` |
| p4 | modelo con orejeras y teckel | `qisu-hero.webp` |
| p5 | teckel con chaqueta lila | `qisu-vest-harness.webp` |
| p5 | caniche + sudadera ilustrada | `qisu-dmatnm.webp` |
| p5 | arnés azul/rosa fondo rosa | `qisu-air-canvas.webp` |
| p5 | persona con bolsos | `qisu-walkies-bag.webp` |
| p5 | perro con gorro/chapa | `qisu-tags.webp` |
| p5 | botella beige en mano | `qisu-woof-n-wash.webp` |
| p6 | dos personas en escombrera industrial | `sk016-campo.webp` |
| p6 | prenda naranja colgada | `sk016-material.webp` |
| p7 | detalle mesa/silla | `outdoor-1.webp` |
| p7 | pérgola escritorio frontal | `outdoor-2.webp` |
| p7 | módulos en sala oscura | `outdoor-3.webp` |
| p8 | módulos madera clara (zapatero) | `otros-escora.webp` |
| p8 | bols cerámica con comida | `otros-cordoba.webp` |
| p8 | axonometría espacio | `otros-rayling.webp` |
| p8 | cartel amarillo en persiana | `otros-politics.webp` |

- [ ] **Step 3: Convertir a WebP optimizado**

```python
# scratch_pdf/convert.py — editar MAPPING con los nombres reales del paso 2
from PIL import Image
import os

MAPPING = {
    'p3_img0.png': 'home-dog.webp',
    # ... completar con los 19 pares reales tras la inspección ...
}
DEST = 'portfolio/src/img'
for src, dst in MAPPING.items():
    img = Image.open(f'scratch_pdf/extracted/{src}').convert('RGB')
    if img.width > 1600:
        img = img.resize((1600, int(img.height * 1600 / img.width)), Image.LANCZOS)
    img.save(f'{DEST}/{dst}', 'WEBP', quality=80)
    kb = os.path.getsize(f'{DEST}/{dst}') // 1024
    print(f'{dst}: {img.size} {kb}KB')
```

Run: `python scratch_pdf/convert.py`
Expected: 19 archivos `.webp` en `portfolio/src/img/`, cada uno < 300 KB.

- [ ] **Step 4: Añadir scratch al gitignore y committear**

Añadir a `.gitignore` la línea `scratch_pdf/` y `porfolio-referencia.pdf` (el PDF de 11 MB no debe entrar al repo salvo que el usuario diga lo contrario).

```bash
git add .gitignore portfolio/src/img/
git commit -m "feat: añadir imágenes reales del porfolio extraídas del PDF"
```

---

### Task 2: Base compartida — CSS, favicon, main.js (hamburguesa), vite.config

**Files:**
- Modify: `portfolio/src/style.css`
- Rewrite: `portfolio/src/main.js`
- Create: `portfolio/public/favicon.svg`
- Modify: `portfolio/vite.config.ts`
- Delete: `portfolio/src/counter.js`, `portfolio/src/javascript.svg`, `portfolio/public/vite.svg`

- [ ] **Step 1: Reescribir `portfolio/src/style.css`** (elimina el @font-face roto)

```css
@import "tailwindcss";

:root {
  --font-sans: "Helvetica Neue", Helvetica, Arial, sans-serif;
}

body {
  font-family: var(--font-sans);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
```

- [ ] **Step 2: Reescribir `portfolio/src/main.js`** (menú móvil)

```js
const btn = document.getElementById('menu-btn');
const overlay = document.getElementById('menu-overlay');

if (btn && overlay) {
  btn.addEventListener('click', () => {
    const isOpen = overlay.classList.toggle('flex');
    overlay.classList.toggle('hidden', !isOpen);
    btn.textContent = isOpen ? '✕' : '☰';
    document.body.classList.toggle('overflow-hidden', isOpen);
  });
}
```

- [ ] **Step 3: Crear `portfolio/public/favicon.svg`**

```svg
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32">
  <rect width="32" height="32" fill="white"/>
  <text x="16" y="22" font-family="Helvetica, Arial, sans-serif" font-size="14" text-anchor="middle" fill="#111">AJ</text>
</svg>
```

- [ ] **Step 4: Reescribir `portfolio/vite.config.ts`** (8 entradas de build)

```ts
import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite'
import { resolve } from 'path'

export default defineConfig({
  plugins: [tailwindcss()],
  build: {
    rollupOptions: {
      input: {
        index: resolve(__dirname, 'index.html'),
        about: resolve(__dirname, 'about.html'),
        projects: resolve(__dirname, 'projects.html'),
        qisu: resolve(__dirname, 'qisu.html'),
        qisuprojects: resolve(__dirname, 'qisuprojects.html'),
        sk016: resolve(__dirname, 'sk016.html'),
        outdoor: resolve(__dirname, 'outdoor.html'),
        otros: resolve(__dirname, 'otros.html'),
      },
    },
  },
})
```

Nota: `otros.html` aún no existe — el build fallará hasta la Task 9. Es esperado; no ejecutar `npm run build` hasta entonces.

- [ ] **Step 5: Borrar restos de plantilla Vite**

```bash
rm portfolio/src/counter.js portfolio/src/javascript.svg portfolio/public/vite.svg
```

- [ ] **Step 6: Commit**

```bash
git add -A portfolio/src portfolio/public portfolio/vite.config.ts
git commit -m "chore: base compartida (css limpio, menú móvil, favicon, entradas de build)"
```

---

### Patrón de navegación compartido (referencia para Tasks 3-9)

Cada página incluye, dentro de `<body>` y antes de `<main>`, el botón hamburguesa y el overlay; y al final de `<main>`, el footer. **La entrada activa** lleva `text-gray-500 cursor-default` (footer) y `text-gray-700` (overlay); las demás `text-gray-300 hover:text-gray-700 transition`. Cada task de página incluye su HTML completo ya con la entrada activa correcta — copiar literal.

Todas las páginas cargan el JS con `<script type="module" src="./src/main.js"></script>` antes de `</body>` y el favicon con `<link rel="icon" type="image/svg+xml" href="/favicon.svg" />`.

---

### Task 3: `index.html` (Portada / Contacto)

**Files:**
- Rewrite: `portfolio/index.html`

- [ ] **Step 1: Reescribir el archivo completo**

```html
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="./src/style.css" rel="stylesheet">
    <title>Arnau Jalón — Diseñador de Producto</title>
  </head>
  <body class="antialiased">
    <button id="menu-btn" aria-label="Abrir menú" class="md:hidden fixed top-4 right-4 z-50 text-2xl leading-none">☰</button>
    <div id="menu-overlay" class="hidden md:hidden fixed inset-0 z-40 bg-white flex-col items-center justify-center gap-8 text-center">
      <a href="./index.html" class="text-xl text-gray-700 cursor-default">CONTACTO</a>
      <a href="./about.html" class="text-xl text-gray-400">SOBRE MI</a>
      <a href="./qisu.html" class="text-xl text-gray-400">QISU BRAND<span class="block text-xs">Accesorios para perros.</span></a>
      <a href="./sk016.html" class="text-xl text-gray-400">SK-016<span class="block text-xs">Moda, materiales y tendencias.</span></a>
      <a href="./outdoor.html" class="text-xl text-gray-400">OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span></a>
      <a href="./otros.html" class="text-xl text-gray-400">OTROS PROYECTOS</a>
    </div>

    <main class="grid min-h-screen md:h-screen grid-rows-[auto_1fr_auto] md:overflow-hidden">

      <div></div>

      <section class="grid px-4 md:mx-[2vw] mt-[2vh] grid-cols-1 md:grid-cols-[60%_40%] md:grid-rows-[1fr_2fr_3fr] gap-y-6">
        <p class="text-xl md:text-2xl md:col-start-1 md:row-start-1">PORFOLIO DE DISEÑO</p>
        <p class="text-xl md:text-2xl md:col-start-2 md:row-start-1">ARNAU JALÓN</p>

        <p class="text-xl md:text-2xl md:col-start-2 md:row-start-2">Diseñador de Producto</p>

        <p class="text-base md:col-start-1 md:row-start-3 md:justify-self-end md:mr-[15vh]">Contacto</p>

        <div class="md:col-start-2 md:row-start-3">
          <div class="grid grid-cols-1 md:grid-cols-[60%_40%] items-end">
            <div class="grid grid-cols-2 text-base">
              <div class="flex flex-col">
                <p>Instagram:</p>
                <p>Mail:</p>
                <p>Teléfono:</p>
              </div>
              <div class="flex flex-col">
                <p>@arnaujalon</p>
                <p>arnau.jalon@gmail.com</p>
                <p>+34 688 84 10 09</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <footer class="hidden md:block w-full">
        <hr class="border-t border-gray-300 w-[95%] mx-auto" />
        <nav class="flex justify-evenly py-3">
          <a href="./index.html" class="text-base text-center text-gray-500 cursor-default">CONTACTO</a>
          <a href="./about.html" class="text-base text-gray-300 hover:text-gray-700 transition">SOBRE MI</a>
          <a href="./qisu.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            QISU BRAND<span class="block text-xs">Accesorios para perros.</span>
          </a>
          <a href="./sk016.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            SK-016<span class="block text-xs">Moda, materiales y tendencias.</span>
          </a>
          <a href="./outdoor.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span>
          </a>
          <a href="./otros.html" class="text-base text-gray-300 hover:text-gray-700 transition">OTROS PROYECTOS</a>
        </nav>
      </footer>

    </main>
    <script type="module" src="./src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 2: Verificar en dev server**

Run: `npm run dev` (en `portfolio/`), abrir la página a 1280×800 y 375×812.
Expected: desktop igual que `scratch_pdf/p1.png`; en móvil todo apilado y legible, hamburguesa abre/cierra el overlay.

- [ ] **Step 3: Commit**

```bash
git add portfolio/index.html
git commit -m "feat: portada responsive con navegación completa"
```

---

### Task 4: `about.html` (Sobre mí — retrato, página 1)

**Files:**
- Rewrite: `portfolio/about.html`

- [ ] **Step 1: Reescribir el archivo completo**

```html
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="./src/style.css" rel="stylesheet">
    <title>Arnau Jalón — Sobre mí</title>
  </head>
  <body class="antialiased">
    <button id="menu-btn" aria-label="Abrir menú" class="md:hidden fixed top-4 right-4 z-50 text-2xl leading-none">☰</button>
    <div id="menu-overlay" class="hidden md:hidden fixed inset-0 z-40 bg-white flex-col items-center justify-center gap-8 text-center">
      <a href="./index.html" class="text-xl text-gray-400">CONTACTO</a>
      <a href="./about.html" class="text-xl text-gray-700 cursor-default">SOBRE MI</a>
      <a href="./qisu.html" class="text-xl text-gray-400">QISU BRAND<span class="block text-xs">Accesorios para perros.</span></a>
      <a href="./sk016.html" class="text-xl text-gray-400">SK-016<span class="block text-xs">Moda, materiales y tendencias.</span></a>
      <a href="./outdoor.html" class="text-xl text-gray-400">OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span></a>
      <a href="./otros.html" class="text-xl text-gray-400">OTROS PROYECTOS</a>
    </div>

    <main class="grid min-h-screen md:h-screen grid-rows-[auto_1fr_auto]">

      <header class="grid grid-cols-1 md:grid-cols-[20%_60%_20%] px-4 md:mx-[2vw] mt-[2vh] mb-[4vh] md:mb-[7vh] text-xl md:text-2xl gap-y-4">
        <p>SOBRE MI</p>
        <p>Estudiar diseño me ha enseñado a escuchar, a compartir y a mantener siempre la inquietud por seguir aprendiendo.</p>
        <p class="hidden md:block text-base justify-self-end mr-[10%]">1</p>
      </header>

      <section class="grid grid-cols-1 md:grid-cols-[20%_30%_1fr] md:grid-rows-[1fr_auto] px-4 md:px-0 md:ml-[2vw] mb-[2vh] gap-y-6">
        <p class="md:col-start-2 text-base text-justify">
          El diseño me ha llevado a explorar. Cada pensamiento, idea, nota y corrección forman parte del proceso, y lo enriquecen.
          <br /><br />
          He aprendido a no tener miedo al error, a observar y relacionar campos que parecen inconexos. A sentir y entender el mundo de una forma más analítica, crítica y pura.
        </p>

        <div class="md:col-start-3 md:row-start-1 md:row-end-3 flex items-center justify-center">
          <img src="./src/img/image.webp"
               alt="Arnau Jalón sentado en una silla"
               class="w-full max-w-sm md:w-100 aspect-square object-cover" />
        </div>

        <div class="md:col-start-1 md:row-start-2 md:col-end-3 text-base leading-tight flex flex-col">
          <p>Graduado en Elisava. <span class="text-xs">2023.</span></p>
          <p>Escuela Universitaria de Diseño e Ingeniería de Barcelona.</p>
          <div class="h-3"></div>
          <p>Programa de Intercambio Internacional en EDNA. <span class="text-xs">2022.</span></p>
          <p>L'École de Design Nantes Atlantique.</p>
        </div>

        <a href="./projects.html" aria-label="Siguiente página" class="md:col-start-3 md:row-start-2 flex md:self-end justify-self-end w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&rarr;</a>
      </section>

      <footer class="hidden md:block w-full">
        <hr class="border-t border-gray-300 w-[95%] mx-auto" />
        <nav class="flex justify-evenly py-3">
          <a href="./index.html" class="text-base text-gray-300 hover:text-gray-700 transition">CONTACTO</a>
          <a href="./about.html" class="text-base text-gray-500 cursor-default">SOBRE MI</a>
          <a href="./qisu.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            QISU BRAND<span class="block text-xs">Accesorios para perros.</span>
          </a>
          <a href="./sk016.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            SK-016<span class="block text-xs">Moda, materiales y tendencias.</span>
          </a>
          <a href="./outdoor.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span>
          </a>
          <a href="./otros.html" class="text-base text-gray-300 hover:text-gray-700 transition">OTROS PROYECTOS</a>
        </nav>
      </footer>

    </main>
    <script type="module" src="./src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 2: Verificar en dev server** (desktop vs `scratch_pdf/p2.png`, móvil apilado sin scroll horizontal)

- [ ] **Step 3: Commit**

```bash
git add portfolio/about.html
git commit -m "feat: sobre mí responsive"
```

---

### Task 5: `projects.html` (Experiencia destacada, página 2)

**Files:**
- Rewrite: `portfolio/projects.html`

- [ ] **Step 1: Reescribir el archivo completo**

```html
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="./src/style.css" rel="stylesheet">
    <title>Arnau Jalón — Experiencia</title>
  </head>
  <body class="antialiased">
    <button id="menu-btn" aria-label="Abrir menú" class="md:hidden fixed top-4 right-4 z-50 text-2xl leading-none">☰</button>
    <div id="menu-overlay" class="hidden md:hidden fixed inset-0 z-40 bg-white flex-col items-center justify-center gap-8 text-center">
      <a href="./index.html" class="text-xl text-gray-400">CONTACTO</a>
      <a href="./about.html" class="text-xl text-gray-700 cursor-default">SOBRE MI</a>
      <a href="./qisu.html" class="text-xl text-gray-400">QISU BRAND<span class="block text-xs">Accesorios para perros.</span></a>
      <a href="./sk016.html" class="text-xl text-gray-400">SK-016<span class="block text-xs">Moda, materiales y tendencias.</span></a>
      <a href="./outdoor.html" class="text-xl text-gray-400">OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span></a>
      <a href="./otros.html" class="text-xl text-gray-400">OTROS PROYECTOS</a>
    </div>

    <main class="grid min-h-screen md:h-screen grid-rows-[auto_1fr_auto]">

      <header class="grid grid-cols-1 md:grid-cols-[20%_60%_20%] px-4 md:mx-[2vw] mt-[2vh] text-xl md:text-2xl gap-y-4">
        <p>SOBRE MI</p>
        <p>Experiencia en las áreas de accesorios, mascotas, decoración y mobiliario. Conocimientos en moda, tejidos, y exploración de nuevos materiales y tendencias.</p>
        <p class="hidden md:block text-base justify-self-end mr-[10%]">2</p>
      </header>

      <section class="grid grid-cols-1 md:grid-cols-[20%_70%_10%] px-4 md:px-0 md:ml-[2vw] mt-6">
        <div class="grid grid-cols-1 md:grid-cols-3 md:col-start-2 gap-4 md:gap-2">
          <figure>
            <img src="./src/img/home-dog.webp" alt="Perro con arnés Qisu Brand" class="w-full aspect-[4/5] object-cover" />
            <figcaption class="text-xs italic mt-1">#DogWear #Accessories</figcaption>
          </figure>
          <figure>
            <img src="./src/img/home-fashion.webp" alt="Prenda experimental SK-016" class="w-full aspect-[4/5] object-cover" />
            <figcaption class="text-xs italic mt-1">#Fashion #Materiality</figcaption>
          </figure>
          <figure>
            <img src="./src/img/home-furniture.webp" alt="Mobiliario Outdoor Workplace para Kettal" class="w-full aspect-[4/5] object-cover" />
            <figcaption class="text-xs italic mt-1">#ProductDesign #Furniture</figcaption>
          </figure>
        </div>
      </section>

      <section class="grid grid-cols-1 md:grid-cols-4 px-4 md:mx-[2vw] mt-8 md:mt-[5vh] mb-6 text-base text-justify gap-y-6">
        <div>
          <p class="text-sm">Experiencia destacada:</p>
          <a href="./about.html" aria-label="Página anterior" class="inline-flex mt-4 w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&larr;</a>
        </div>

        <div>
          <p>2.1. <a href="./qisu.html" class="hover:text-gray-500 transition">QISU BRAND</a> <span class="text-xs">2022-2025.</span></p>
          <p class="text-xs mt-4 md:mx-8">Colecciones de producto sostenible para perros y accesorios para humanos, parte del actual catálogo de Qisu Brand.</p>
        </div>
        <div>
          <p>2.2. <a href="./sk016.html" class="hover:text-gray-500 transition">SK-016</a> <span class="text-xs">2022-2023.</span></p>
          <p class="text-xs mt-4 md:mx-8">Nuevas tendencias y materiales para una moda sostenible. Fue parte de la exposición <span class="italic">"Imaginar els Possibles"</span> del DHub Barcelona.</p>
        </div>
        <div>
          <p>2.3. <a href="./outdoor.html" class="hover:text-gray-500 transition">OUTDOOR WORKPLACE</a> <span class="text-xs">2022-2023.</span></p>
          <p class="text-xs mt-4 md:mx-8">Proyecto de investigación. Exploración de nuevas tipologías de mobiliario y propuesta para el actual catálogo de Kettal.</p>
        </div>
      </section>

      <footer class="hidden md:block w-full">
        <hr class="border-t border-gray-300 w-[95%] mx-auto" />
        <nav class="flex justify-evenly py-3">
          <a href="./index.html" class="text-base text-gray-300 hover:text-gray-700 transition">CONTACTO</a>
          <a href="./about.html" class="text-base text-gray-500 cursor-default">SOBRE MI</a>
          <a href="./qisu.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            QISU BRAND<span class="block text-xs">Accesorios para perros.</span>
          </a>
          <a href="./sk016.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            SK-016<span class="block text-xs">Moda, materiales y tendencias.</span>
          </a>
          <a href="./outdoor.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span>
          </a>
          <a href="./otros.html" class="text-base text-gray-300 hover:text-gray-700 transition">OTROS PROYECTOS</a>
        </nav>
      </footer>

    </main>
    <script type="module" src="./src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 2: Verificar** (desktop vs `scratch_pdf/p3.png`; imágenes cargan; móvil: fotos apiladas a ancho completo)

- [ ] **Step 3: Commit**

```bash
git add portfolio/projects.html
git commit -m "feat: página de experiencia con fotos reales y hashtags"
```

---

### Task 6: `qisu.html` (Qisu Brand intro, página 3)

**Files:**
- Rewrite: `portfolio/qisu.html`

- [ ] **Step 1: Reescribir el archivo completo**

```html
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="./src/style.css" rel="stylesheet">
    <title>Arnau Jalón — Qisu Brand</title>
  </head>
  <body class="antialiased">
    <button id="menu-btn" aria-label="Abrir menú" class="md:hidden fixed top-4 right-4 z-50 text-2xl leading-none">☰</button>
    <div id="menu-overlay" class="hidden md:hidden fixed inset-0 z-40 bg-white flex-col items-center justify-center gap-8 text-center">
      <a href="./index.html" class="text-xl text-gray-400">CONTACTO</a>
      <a href="./about.html" class="text-xl text-gray-400">SOBRE MI</a>
      <a href="./qisu.html" class="text-xl text-gray-700 cursor-default">QISU BRAND<span class="block text-xs">Accesorios para perros.</span></a>
      <a href="./sk016.html" class="text-xl text-gray-400">SK-016<span class="block text-xs">Moda, materiales y tendencias.</span></a>
      <a href="./outdoor.html" class="text-xl text-gray-400">OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span></a>
      <a href="./otros.html" class="text-xl text-gray-400">OTROS PROYECTOS</a>
    </div>

    <main class="grid min-h-screen md:h-screen grid-rows-[auto_1fr_auto]">

      <header class="grid grid-cols-[1fr_auto] px-4 md:mx-[2vw] mt-[2vh] mb-[4vh] text-xl md:text-2xl">
        <p>QISU BRAND</p>
        <p class="hidden md:block text-base mr-[10%]">3</p>
      </header>

      <section class="grid grid-cols-1 md:grid-cols-[30%_50%] md:grid-rows-[1fr_auto] px-4 md:px-0 mb-[2vh] gap-y-6">

        <div class="md:col-start-1 md:row-start-1 md:row-end-3 flex items-center md:pt-[5vh] md:mx-[2vw]">
          <img src="./src/img/qisu-hero.webp"
               alt="Modelo con orejeras y perro teckel con jersey Qisu"
               class="w-full max-w-md h-auto object-cover" />
        </div>

        <section class="text-justify md:col-start-2 md:row-start-1 md:row-end-3 flex flex-col md:ml-[3vw]">
          <div class="text-xl md:text-2xl">
            <p>Diseñador de producto en Qisu Brand. Moda sostenible y accesorios para perros y humanos.</p>
          </div>

          <div class="text-base mt-4">
            <p>En Qisu, he desarrollado proyectos de producto, textil, ilustración, identidad gráfica, packaging, y diseño de stands y espacios para ferias, eventos y tiendas. He liderado la dirección creativa de shootings, la selección de modelos y la producción de vestuario y escenografía.</p>
          </div>

          <div class="text-xs mt-4">
            <p>Aquí he aprendido a diseñar desde la creatividad y la estrategia, a hacer análisis de mercado, y a liderar el equipo de diseño de una marca emergente. A hacer el seguimiento de proveedores, planificar los calendarios, calcular el impacto y la viabilidad de las campañas, y prever los beneficios esperados de cada lanzamiento junto al resto de departamentos.</p>
          </div>

          <div class="flex md:items-end justify-end md:justify-start mt-6 md:mt-auto">
            <a href="./qisuprojects.html" aria-label="Ver proyectos de Qisu" class="w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&rarr;</a>
          </div>
        </section>

      </section>

      <footer class="hidden md:block w-full">
        <hr class="border-t border-gray-300 w-[95%] mx-auto" />
        <nav class="flex justify-evenly py-3">
          <a href="./index.html" class="text-base text-gray-300 hover:text-gray-700 transition">CONTACTO</a>
          <a href="./about.html" class="text-base text-gray-300 hover:text-gray-700 transition">SOBRE MI</a>
          <a href="./qisu.html" class="text-base text-center text-gray-500 cursor-default">
            QISU BRAND<span class="block text-xs">Accesorios para perros.</span>
          </a>
          <a href="./sk016.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            SK-016<span class="block text-xs">Moda, materiales y tendencias.</span>
          </a>
          <a href="./outdoor.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span>
          </a>
          <a href="./otros.html" class="text-base text-gray-300 hover:text-gray-700 transition">OTROS PROYECTOS</a>
        </nav>
      </footer>

    </main>
    <script type="module" src="./src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 2: Verificar** (desktop vs `scratch_pdf/p4.png`)

- [ ] **Step 3: Commit**

```bash
git add portfolio/qisu.html
git commit -m "feat: qisu brand con foto real y html válido"
```

---

### Task 7: `qisuprojects.html` (Qisu — selección de proyectos, página 4) — REHACER

**Files:**
- Rewrite: `portfolio/qisuprojects.html`

- [ ] **Step 1: Reescribir el archivo completo** (cuadrícula 3×2; en desktop cabe con scroll ligero si hace falta — usar `min-h-screen` sin `h-screen`)

```html
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="./src/style.css" rel="stylesheet">
    <title>Arnau Jalón — Qisu Brand, proyectos</title>
  </head>
  <body class="antialiased">
    <button id="menu-btn" aria-label="Abrir menú" class="md:hidden fixed top-4 right-4 z-50 text-2xl leading-none">☰</button>
    <div id="menu-overlay" class="hidden md:hidden fixed inset-0 z-40 bg-white flex-col items-center justify-center gap-8 text-center">
      <a href="./index.html" class="text-xl text-gray-400">CONTACTO</a>
      <a href="./about.html" class="text-xl text-gray-400">SOBRE MI</a>
      <a href="./qisu.html" class="text-xl text-gray-700 cursor-default">QISU BRAND<span class="block text-xs">Accesorios para perros.</span></a>
      <a href="./sk016.html" class="text-xl text-gray-400">SK-016<span class="block text-xs">Moda, materiales y tendencias.</span></a>
      <a href="./outdoor.html" class="text-xl text-gray-400">OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span></a>
      <a href="./otros.html" class="text-xl text-gray-400">OTROS PROYECTOS</a>
    </div>

    <main class="grid min-h-screen grid-rows-[auto_1fr_auto]">

      <header class="grid grid-cols-[1fr_auto] px-4 md:mx-[2vw] mt-[2vh] mb-[4vh] text-xl md:text-2xl">
        <p>QISU BRAND, selección de proyectos</p>
        <p class="hidden md:block text-base mr-[10%]">4</p>
      </header>

      <section class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-x-8 gap-y-10 px-4 md:mx-[2vw] mb-8">

        <article class="grid grid-cols-[45%_55%] gap-x-4">
          <div>
            <img src="./src/img/qisu-vest-harness.webp" alt="Teckel con Vest Harness lila" class="w-full aspect-square object-cover" />
            <p class="text-xs italic mt-1">#DogWear</p>
          </div>
          <div>
            <h2 class="text-base md:text-xl">VEST HARNESS</h2>
            <p class="text-xs text-justify mt-4">Un arnés convertible en chaqueta, innovador por su diseño y pensado ergonómicamente para los perros más pequeños.</p>
          </div>
        </article>

        <article class="grid grid-cols-[45%_55%] gap-x-4">
          <div>
            <img src="./src/img/qisu-dmatnm.webp" alt="Caniche junto a sudadera con ilustración DMATNM" class="w-full aspect-square object-cover" />
            <p class="text-xs italic mt-1">#Fashion #Illustration</p>
          </div>
          <div>
            <h2 class="text-base md:text-xl">*DMATNM</h2>
            <p class="text-xs text-justify mt-4">Ilustraciones para los diseños de las nuevas sudaderas personalizables <span class="italic">"Dog Moms Are The New MILFS"</span>, y <span class="italic">"Dog Dads Are The New DILFS"</span>.</p>
          </div>
        </article>

        <article class="grid grid-cols-[45%_55%] gap-x-4">
          <div>
            <img src="./src/img/qisu-air-canvas.webp" alt="Air Canvas Harness sobre fondo rosa" class="w-full aspect-square object-cover" />
            <p class="text-xs italic mt-1">#DogWear</p>
          </div>
          <div>
            <h2 class="text-base md:text-xl">AIR CANVAS HARNESS</h2>
            <p class="text-xs text-justify mt-4">Un arnés ligero, parte de la colección <span class="italic">"Summer Collection"</span> pensado para el verano.</p>
            <p class="text-xs text-justify mt-2">Le acompaña un collar, una correa y un porta-bolsas a juego, una cama portátil de viaje, toalla y helados de peluche.</p>
          </div>
        </article>

        <article class="grid grid-cols-[45%_55%] gap-x-4">
          <div>
            <img src="./src/img/qisu-walkies-bag.webp" alt="Persona llevando los bolsos Walkies Bag" class="w-full aspect-square object-cover" />
            <p class="text-xs italic mt-1">#Fashion #Accessories</p>
          </div>
          <div>
            <h2 class="text-base md:text-xl">WALKIES BAG</h2>
            <p class="text-xs text-justify mt-4">Bolso con tres posiciones, pensado para solucionar las necesidades del paseo con tu perro.</p>
            <p class="text-xs text-justify mt-2">Convertible en riñonera y mochila, incluye una serie de complementos, como un bol portátil y un porta-premios a juego.</p>
          </div>
        </article>

        <article class="grid grid-cols-[45%_55%] gap-x-4">
          <div>
            <img src="./src/img/qisu-tags.webp" alt="Perro con gorro y chapa identificativa Qisu Tags" class="w-full aspect-square object-cover" />
            <p class="text-xs italic mt-1">#Accessories</p>
          </div>
          <div>
            <h2 class="text-base md:text-xl">QISU TAGS</h2>
            <p class="text-xs text-justify mt-4">Chapas identificativas para perros. La chapa simula perros disfrazados con gorritos, y por eso, en el rodaje, disfrazamos a los perros a conjunto de la chapa.</p>
            <p class="text-xs text-justify mt-2">La funda de silicona no solo protege la chapa de golpes y ralladuras, sino que además las hace coleccionables e intercambiables.</p>
          </div>
        </article>

        <article class="grid grid-cols-[45%_55%] gap-x-4">
          <div>
            <img src="./src/img/qisu-woof-n-wash.webp" alt="Botella portátil Woof n Wash en una mano" class="w-full aspect-square object-cover" />
            <p class="text-xs italic mt-1">#Accessories</p>
          </div>
          <div>
            <h2 class="text-base md:text-xl">WOOF N WASH</h2>
            <p class="text-xs text-justify mt-4">Un imprescindible para los paseos, que complementa muchas de las colecciones de la marca.</p>
            <p class="text-xs text-justify mt-2">Una botella portátil para llevar agua y limpiar los pipis en la calle.</p>
          </div>
        </article>

      </section>

      <div class="px-4 md:mx-[2vw] mb-4 flex justify-between">
        <a href="./qisu.html" aria-label="Página anterior" class="w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&larr;</a>
        <a href="./sk016.html" aria-label="Siguiente página" class="w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&rarr;</a>
      </div>

      <footer class="hidden md:block w-full">
        <hr class="border-t border-gray-300 w-[95%] mx-auto" />
        <nav class="flex justify-evenly py-3">
          <a href="./index.html" class="text-base text-gray-300 hover:text-gray-700 transition">CONTACTO</a>
          <a href="./about.html" class="text-base text-gray-300 hover:text-gray-700 transition">SOBRE MI</a>
          <a href="./qisu.html" class="text-base text-center text-gray-500 cursor-default">
            QISU BRAND<span class="block text-xs">Accesorios para perros.</span>
          </a>
          <a href="./sk016.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            SK-016<span class="block text-xs">Moda, materiales y tendencias.</span>
          </a>
          <a href="./outdoor.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span>
          </a>
          <a href="./otros.html" class="text-base text-gray-300 hover:text-gray-700 transition">OTROS PROYECTOS</a>
        </nav>
      </footer>

    </main>
    <script type="module" src="./src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 2: Verificar** (desktop vs `scratch_pdf/p5.png` — cuadrícula 3 columnas × 2 filas; móvil: 1 columna)

- [ ] **Step 3: Commit**

```bash
git add portfolio/qisuprojects.html
git commit -m "feat: página de selección de proyectos Qisu (6 productos del PDF)"
```

---

### Task 8: `sk016.html` y `outdoor.html` (páginas 5 y 6)

**Files:**
- Rewrite: `portfolio/sk016.html`
- Rewrite: `portfolio/outdoor.html`

- [ ] **Step 1: Reescribir `portfolio/sk016.html`**

```html
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="./src/style.css" rel="stylesheet">
    <title>Arnau Jalón — SK-016</title>
  </head>
  <body class="antialiased">
    <button id="menu-btn" aria-label="Abrir menú" class="md:hidden fixed top-4 right-4 z-50 text-2xl leading-none">☰</button>
    <div id="menu-overlay" class="hidden md:hidden fixed inset-0 z-40 bg-white flex-col items-center justify-center gap-8 text-center">
      <a href="./index.html" class="text-xl text-gray-400">CONTACTO</a>
      <a href="./about.html" class="text-xl text-gray-400">SOBRE MI</a>
      <a href="./qisu.html" class="text-xl text-gray-400">QISU BRAND<span class="block text-xs">Accesorios para perros.</span></a>
      <a href="./sk016.html" class="text-xl text-gray-700 cursor-default">SK-016<span class="block text-xs">Moda, materiales y tendencias.</span></a>
      <a href="./outdoor.html" class="text-xl text-gray-400">OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span></a>
      <a href="./otros.html" class="text-xl text-gray-400">OTROS PROYECTOS</a>
    </div>

    <main class="grid min-h-screen md:h-screen grid-rows-[auto_1fr_auto]">

      <header class="grid grid-cols-[1fr_auto] px-4 md:mx-[2vw] mt-[2vh] mb-[4vh] text-xl md:text-2xl">
        <p>SK-016</p>
        <p class="hidden md:block text-base mr-[10%]">5</p>
      </header>

      <section class="h-full min-h-0 flex flex-col">
        <section class="text-justify flex-none flex flex-col px-4 md:px-0 md:w-[50%] md:ml-[40%]">
          <div class="text-xl md:text-2xl">
            <p>SK-016 es un proyecto en el que se exploran nuevas tendencias y materiales para una moda sostenible.</p>
          </div>

          <div class="text-base mt-4">
            <p>Fue parte de la exposición <span class="italic">"Imaginar els Possibles"</span> del DHub Barcelona, 2022-2023.</p>
          </div>

          <div class="text-xs mt-4 gap-y-4 flex flex-col">
            <p>El consumo masivo hace que la industria de la moda sea la segunda más contaminante y demandante de agua del mundo. Además de generar alrededor del 20% de las aguas residuales del planeta, el 73% de los textiles acaban incinerados o en vertederos.</p>
            <p>"SK-016" imagina una nueva forma de confeccionar prendas, mediante una futura tendencia de moda DIY, utilizando un material fácil de degradar, fácil de fabricar y 100% vegetal, permitiendo un sinfín de patrones que juegan con las tonalidades y las transparencias. Al aplicar calor para unir los fragmentos, creamos nuestra segunda piel.</p>
          </div>
        </section>

        <div class="px-4 md:mx-[2vw] mb-[2vh] mt-6 flex-1 min-h-0">
          <div class="grid grid-cols-1 md:grid-cols-[3fr_2fr] gap-4 md:gap-3 h-full items-stretch">
            <img src="./src/img/sk016-campo.webp" alt="Dos personas con prendas SK-016 frente a una fábrica" class="w-full aspect-[4/3] md:aspect-auto md:h-full object-cover" />
            <img src="./src/img/sk016-material.webp" alt="Prenda SK-016 de material vegetal translúcido" class="w-full aspect-[4/3] md:aspect-auto md:h-full object-cover" />
          </div>
        </div>

        <div class="px-4 md:mx-[2vw] mb-4 flex justify-between">
          <a href="./qisuprojects.html" aria-label="Página anterior" class="w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&larr;</a>
          <a href="./outdoor.html" aria-label="Siguiente página" class="w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&rarr;</a>
        </div>
      </section>

      <footer class="hidden md:block w-full">
        <hr class="border-t border-gray-300 w-[95%] mx-auto" />
        <nav class="flex justify-evenly py-3">
          <a href="./index.html" class="text-base text-gray-300 hover:text-gray-700 transition">CONTACTO</a>
          <a href="./about.html" class="text-base text-gray-300 hover:text-gray-700 transition">SOBRE MI</a>
          <a href="./qisu.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            QISU BRAND<span class="block text-xs">Accesorios para perros.</span>
          </a>
          <a href="./sk016.html" class="text-base text-center text-gray-500 cursor-default">
            SK-016<span class="block text-xs">Moda, materiales y tendencias.</span>
          </a>
          <a href="./outdoor.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span>
          </a>
          <a href="./otros.html" class="text-base text-gray-300 hover:text-gray-700 transition">OTROS PROYECTOS</a>
        </nav>
      </footer>

    </main>
    <script type="module" src="./src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 2: Reescribir `portfolio/outdoor.html`** (mismo esqueleto; texto corregido del PDF; 3 imágenes; activa OUTDOOR WORKPLACE)

```html
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="./src/style.css" rel="stylesheet">
    <title>Arnau Jalón — Outdoor Workplace</title>
  </head>
  <body class="antialiased">
    <button id="menu-btn" aria-label="Abrir menú" class="md:hidden fixed top-4 right-4 z-50 text-2xl leading-none">☰</button>
    <div id="menu-overlay" class="hidden md:hidden fixed inset-0 z-40 bg-white flex-col items-center justify-center gap-8 text-center">
      <a href="./index.html" class="text-xl text-gray-400">CONTACTO</a>
      <a href="./about.html" class="text-xl text-gray-400">SOBRE MI</a>
      <a href="./qisu.html" class="text-xl text-gray-400">QISU BRAND<span class="block text-xs">Accesorios para perros.</span></a>
      <a href="./sk016.html" class="text-xl text-gray-400">SK-016<span class="block text-xs">Moda, materiales y tendencias.</span></a>
      <a href="./outdoor.html" class="text-xl text-gray-700 cursor-default">OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span></a>
      <a href="./otros.html" class="text-xl text-gray-400">OTROS PROYECTOS</a>
    </div>

    <main class="grid min-h-screen md:h-screen grid-rows-[auto_1fr_auto]">

      <header class="grid grid-cols-[1fr_auto] px-4 md:mx-[2vw] mt-[2vh] mb-[4vh] text-xl md:text-2xl">
        <p>OUTDOOR WORKPLACE</p>
        <p class="hidden md:block text-base mr-[10%]">6</p>
      </header>

      <section class="h-full min-h-0 flex flex-col">
        <section class="text-justify flex-none flex flex-col px-4 md:px-0 md:w-[50%] md:ml-[40%] mb-[1vh]">
          <div class="text-xl md:text-2xl">
            <p>Outdoor Workplace, en colaboración con Kettal, fue el resultado de mi trabajo de investigación de fin de grado en Elisava.</p>
          </div>

          <div class="text-base mt-4">
            <p>Outdoor Workplace, Oficinas al Aire Libre, apuesta por adaptar de manera cómoda y versátil los espacios exteriores a entornos destinados al trabajo.</p>
          </div>

          <div class="text-xs mt-4 gap-y-4 flex flex-col">
            <p>La propuesta hacia Kettal surgió por querer incorporar a su actual catálogo una nueva colección diseñada específicamente para satisfacer las necesidades que puedan surgir al trabajar en espacios al aire libre. Quise focalizar el inicio de esta investigación en un contexto actual, que refleja cambios consecuentes de la pandemia. Concretamente, se exploraron las nuevas formas que hemos adoptado a la hora de trabajar, cambios a los que me tuve que enfrentar mientras cursaba mis primeros años del grado universitario.</p>
          </div>
        </section>

        <div class="px-4 md:mx-[2vw] mt-4 mb-[2vh] flex-1 min-h-0">
          <div class="grid grid-cols-1 md:grid-cols-[2fr_2fr_2fr] h-full gap-4 md:gap-x-3 items-stretch">
            <img src="./src/img/outdoor-1.webp" alt="Detalle de mesa y silla Outdoor Workplace" class="w-full aspect-[4/3] md:aspect-auto md:h-full object-cover" />
            <img src="./src/img/outdoor-2.webp" alt="Pérgola de trabajo Outdoor Workplace, vista frontal" class="w-full aspect-[4/3] md:aspect-auto md:h-full object-cover" />
            <img src="./src/img/outdoor-3.webp" alt="Módulos Outdoor Workplace en sala de exposición" class="w-full aspect-[4/3] md:aspect-auto md:h-full object-cover" />
          </div>
        </div>

        <div class="px-4 md:mx-[2vw] mb-4 flex justify-between">
          <a href="./sk016.html" aria-label="Página anterior" class="w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&larr;</a>
          <a href="./otros.html" aria-label="Siguiente página" class="w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&rarr;</a>
        </div>
      </section>

      <footer class="hidden md:block w-full">
        <hr class="border-t border-gray-300 w-[95%] mx-auto" />
        <nav class="flex justify-evenly py-3">
          <a href="./index.html" class="text-base text-gray-300 hover:text-gray-700 transition">CONTACTO</a>
          <a href="./about.html" class="text-base text-gray-300 hover:text-gray-700 transition">SOBRE MI</a>
          <a href="./qisu.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            QISU BRAND<span class="block text-xs">Accesorios para perros.</span>
          </a>
          <a href="./sk016.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            SK-016<span class="block text-xs">Moda, materiales y tendencias.</span>
          </a>
          <a href="./outdoor.html" class="text-base text-center text-gray-500 cursor-default">
            OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span>
          </a>
          <a href="./otros.html" class="text-base text-gray-300 hover:text-gray-700 transition">OTROS PROYECTOS</a>
        </nav>
      </footer>

    </main>
    <script type="module" src="./src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 3: Verificar** (desktop vs `scratch_pdf/p6.png` y `p7.png`)

- [ ] **Step 4: Commit**

```bash
git add portfolio/sk016.html portfolio/outdoor.html
git commit -m "feat: sk-016 y outdoor workplace con fotos reales, typos corregidos y flechas"
```

---

### Task 9: `otros.html` (Otros Proyectos, página 7) — NUEVA

**Files:**
- Create: `portfolio/otros.html`

- [ ] **Step 1: Crear el archivo completo**

```html
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="./src/style.css" rel="stylesheet">
    <title>Arnau Jalón — Otros proyectos</title>
  </head>
  <body class="antialiased">
    <button id="menu-btn" aria-label="Abrir menú" class="md:hidden fixed top-4 right-4 z-50 text-2xl leading-none">☰</button>
    <div id="menu-overlay" class="hidden md:hidden fixed inset-0 z-40 bg-white flex-col items-center justify-center gap-8 text-center">
      <a href="./index.html" class="text-xl text-gray-400">CONTACTO</a>
      <a href="./about.html" class="text-xl text-gray-400">SOBRE MI</a>
      <a href="./qisu.html" class="text-xl text-gray-400">QISU BRAND<span class="block text-xs">Accesorios para perros.</span></a>
      <a href="./sk016.html" class="text-xl text-gray-400">SK-016<span class="block text-xs">Moda, materiales y tendencias.</span></a>
      <a href="./outdoor.html" class="text-xl text-gray-400">OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span></a>
      <a href="./otros.html" class="text-xl text-gray-700 cursor-default">OTROS PROYECTOS</a>
    </div>

    <main class="grid min-h-screen grid-rows-[auto_1fr_auto]">

      <header class="grid grid-cols-[1fr_auto] px-4 md:mx-[2vw] mt-[2vh] mb-[4vh] text-xl md:text-2xl">
        <p>OTROS PROYECTOS</p>
        <p class="hidden md:block text-base mr-[10%]">7</p>
      </header>

      <section class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-x-6 gap-y-10 px-4 md:mx-[2vw] mb-8">

        <article>
          <img src="./src/img/otros-escora.webp" alt="Módulos de madera Escora apilados" class="w-full aspect-[4/3] object-cover" />
          <p class="text-xs italic mt-1">#ProductDesign #Furniture</p>
          <h2 class="text-base md:text-xl mt-6">ESCORA</h2>
          <p class="text-xs mt-4">Proyecto desarrollado en Elisava.</p>
          <p class="text-xs text-justify mt-4">Un proyecto de mobiliario inspirado en el mar Mediterráneo y su historia.</p>
          <p class="text-xs text-justify mt-2">Como resultado, se desarrolló un conjunto versátil de tres módulos cúbicos de madera contrachapada que funcionan como zapatero. Su estructura fue inspirada en el mundo de la pesca e imita las costillas del esqueleto estructural de un barco pesquero.</p>
        </article>

        <article>
          <img src="./src/img/otros-cordoba.webp" alt="Vajilla de gres Córdoba con comida" class="w-full aspect-[4/3] object-cover" />
          <p class="text-xs italic mt-1">#FoodDesign #Pottery</p>
          <h2 class="text-base md:text-xl mt-6">CÓRDOBA</h2>
          <p class="text-xs mt-4">Proyecto desarrollado en Elisava.</p>
          <p class="text-xs text-justify mt-4">Un proyecto personal que une las disciplinas del Diseño de Producto y el Food Design.</p>
          <p class="text-xs text-justify mt-2">Una vajilla artesanal compuesta por seis piezas de gres con esmaltado blanco brillante, y un utensilio de madera. Les acompaña una narrativa gastronómica que se remonta a los orígenes de esta ciudad.</p>
        </article>

        <article>
          <img src="./src/img/otros-rayling.webp" alt="Axonometría del espacio The Rayling System" class="w-full aspect-[4/3] object-cover" />
          <p class="text-xs italic mt-1">#ServiceDesign #Space</p>
          <h2 class="text-base md:text-xl mt-6">THE RAYLING SYSTEM</h2>
          <p class="text-xs mt-4">En colaboración con Elisava y el Ajuntament de Barcelona</p>
          <p class="text-xs text-justify mt-4">Se propuso un plan de reforma de los espacios públicos interiores y exteriores del barrio de Vilapicina y la Torre Llobeta, junto con una red de actividades y servicios que se propueso para disminuir algunos de los problemas de movilidad que enfrenta la gente mayor a diario.</p>
          <p class="text-xs text-justify mt-2">Le acompaña un sistema modular de raíles diseñados como elementos divisores de espacios, que cubrían necesidades de carácter ortopédico.</p>
        </article>

        <article>
          <img src="./src/img/otros-politics.webp" alt="Cartel amarillo de Politics of (In)visibility en la calle" class="w-full aspect-[4/3] object-cover" />
          <p class="text-xs italic mt-1">#GraphicDesign #Activism</p>
          <h2 class="text-base md:text-xl mt-6">POLITICS OF (IN)VISIBILITY</h2>
          <p class="text-xs mt-4">En colaboración con Elisava e Ideal, centro de artes digitales.</p>
          <p class="text-xs text-justify mt-4">En conjunto con otros 5 diseñadores, realizamos diferentes carteles que fueron parte de una exposición en la vía pública, recogida posteriormente en el centro de artes digitales "Ideal" en Barcelona.</p>
          <p class="text-xs text-justify mt-2">Todos con la intención de criticar y denunciar los espacios que habían sido alterados, para así, evitar que puedan ser ocupados por personas sin hogar.</p>
        </article>

      </section>

      <div class="px-4 md:mx-[2vw] mb-4 flex justify-start">
        <a href="./outdoor.html" aria-label="Página anterior" class="w-fit h-fit text-3xl text-gray-500 hover:text-gray-700 transition">&larr;</a>
      </div>

      <footer class="hidden md:block w-full">
        <hr class="border-t border-gray-300 w-[95%] mx-auto" />
        <nav class="flex justify-evenly py-3">
          <a href="./index.html" class="text-base text-gray-300 hover:text-gray-700 transition">CONTACTO</a>
          <a href="./about.html" class="text-base text-gray-300 hover:text-gray-700 transition">SOBRE MI</a>
          <a href="./qisu.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            QISU BRAND<span class="block text-xs">Accesorios para perros.</span>
          </a>
          <a href="./sk016.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            SK-016<span class="block text-xs">Moda, materiales y tendencias.</span>
          </a>
          <a href="./outdoor.html" class="text-base text-center text-gray-300 hover:text-gray-700 transition">
            OUTDOOR WORKPLACE<span class="block text-xs">Mobiliario.</span>
          </a>
          <a href="./otros.html" class="text-base text-gray-500 cursor-default">OTROS PROYECTOS</a>
        </nav>
      </footer>

    </main>
    <script type="module" src="./src/main.js"></script>
  </body>
</html>
```

Nota: "propueso" es literal del PDF (posible typo del original); corregir a "propuso" — confirmado como corrección deseada por coherencia con las otras correcciones de typos.

- [ ] **Step 2: Verificar** (desktop vs `scratch_pdf/p8.png` — 4 columnas; móvil 1 columna)

- [ ] **Step 3: Commit**

```bash
git add portfolio/otros.html
git commit -m "feat: página otros proyectos (Escora, Córdoba, Rayling, Politics)"
```

---

### Task 10: Verificación final completa

**Files:** ninguno (solo verificación) — posibles fixes menores en cualquier página.

- [ ] **Step 1: Build de producción**

Run: `npm run build` (en `portfolio/`)
Expected: termina sin errores y `dist/` contiene los 8 `.html`.

- [ ] **Step 2: Barrido visual desktop (1280×800)**

Con el dev server, visitar las 8 páginas y comparar cada una con su render (`scratch_pdf/p1..p8.png`). Comprobar: composición, textos, imágenes correctas, sección activa del footer.

- [ ] **Step 3: Barrido visual móvil (375×812)**

Las 8 páginas: sin scroll horizontal, hamburguesa abre/cierra y navega, texto legible (mín `text-xs`), imágenes a ancho completo, sin elementos cortados.

- [ ] **Step 4: Barrido de enlaces**

`grep -n 'href="#"' portfolio/*.html` → Expected: sin resultados.

- [ ] **Step 5: Fixes menores si los hay + commit final**

```bash
git add -A portfolio
git commit -m "fix: ajustes finales de verificación visual"
```

---

## Self-review (hecho)

- **Cobertura de spec:** §1 páginas → Tasks 3-9; §2 nav → patrón en cada task de página + JS en Task 2; §3 responsive → clases móvil/`md:` en cada página; §4 imágenes → Task 1; §5 contenido → Tasks 5-9; §6 pulido → Tasks 2-9 (titles, lang, doctype, favicon, font, borrado de restos); §7 verificación → Task 10 + steps por página.
- **Sin placeholders:** el único hueco deliberado es el `MAPPING` de Task 1 Step 3, que depende de la inspección visual del Step 2 (inevitable — los índices de imagen no se conocen hasta extraer).
- **Consistencia:** nombres de imagen idénticos entre Task 1 y Tasks 5-9; ids `menu-btn`/`menu-overlay` idénticos entre Task 2 y todas las páginas.
