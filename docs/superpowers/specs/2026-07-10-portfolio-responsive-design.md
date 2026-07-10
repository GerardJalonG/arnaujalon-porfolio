# Porfolio Arnau Jalón — Diseño: completar, fidelidad al PDF y responsive

**Fecha:** 2026-07-10
**Referencia visual:** `porfolio-referencia.pdf` (8 páginas, solo desktop). Renders en `scratch_pdf/p1..p8.png`.
**Stack:** Vite 7 + Tailwind CSS v4 (plugin `@tailwindcss/vite`), HTML estático. Sin frameworks (decisión explícita: no React/Astro).

## Objetivo

Dejar el porfolio acabado y publicable: contenido completo según el PDF, diseño desktop fiel al PDF, versión móvil diseñada nueva (el PDF no la define), y pulido general.

## 1. Arquitectura de páginas (8, mapeadas al PDF)

| # | Archivo | Sección PDF (página) | Estado actual |
|---|---------|----------------------|---------------|
| 1 | `index.html` | Portada / Contacto (p1) | OK, pulir |
| 2 | `about.html` | Sobre mí, retrato (p2) | OK, pulir |
| 3 | `projects.html` | Sobre mí, experiencia 2.1–2.3 (p3) | imágenes rotas |
| 4 | `qisu.html` | Qisu Brand intro (p4) | imagen rota (usa retrato en vez de foto Qisu) |
| 5 | `qisuprojects.html` | Qisu, selección de 6 proyectos (p5) | es copia de qisu.html — rehacer entera |
| 6 | `sk016.html` | SK-016 (p6) | imágenes rotas |
| 7 | `outdoor.html` | Outdoor Workplace (p7) | imágenes rotas |
| 8 | `otros.html` **(nueva)** | Otros Proyectos (p8) | no existe |

Numeración visible de página (esquina superior derecha) consistente con el PDF: about=1, projects=2, qisu=3, qisuprojects=4, sk016=5, outdoor=6, otros=7. La portada no lleva número.

## 2. Navegación compartida

Un único patrón idéntico en las 8 páginas (copiado de forma consistente, sin includes):

- **Desktop (`md:`+):** barra horizontal al pie con `<hr>` encima, 6 entradas: CONTACTO · SOBRE MI · QISU BRAND (Accesorios para perros.) · SK-016 (Moda, materiales y tendencias.) · OUTDOOR WORKPLACE (Mobiliario.) · OTROS PROYECTOS. Igual que el PDF.
- **Móvil (< `md`):** el footer se oculta; botón hamburguesa ☰ fijo arriba a la derecha que abre un overlay a pantalla completa con los 6 enlaces apilados. JS vanilla mínimo (~5 líneas, inline o en `src/main.js`).
- **Enlaces correctos en todas las páginas** (hoy hay muchos `#`): CONTACTO→index, SOBRE MI→about, QISU BRAND→qisu, SK-016→sk016, OUTDOOR WORKPLACE→outdoor, OTROS PROYECTOS→otros.
- **Sección activa:** gris oscuro (`text-gray-500`/`text-gray-700`) y `cursor-default`; el resto `text-gray-300` con hover. En páginas hijas (projects → SOBRE MI activa; qisuprojects → QISU BRAND activa).
- Flechas ←/→ dentro de la página para el flujo secuencial (about→projects, qisu→qisuprojects, etc.), como ahora. Añadirlas donde faltan (outdoor, sk016, otros).

## 3. Estrategia responsive

El PDF define solo desktop; el móvil se diseña adaptando su estilo minimalista.

- **Desktop (`md:`+):** una pantalla por página (sin scroll), grids multicolumna, fiel al PDF. Se mantiene el layout actual donde ya coincide.
- **Móvil:** las páginas scrollean con normalidad. Cambios raíz:
  - `h-screen` → `min-h-screen`; eliminar `overflow-hidden`.
  - Grids multicolumna → 1 columna apilada (orden lógico de lectura: título, texto, imágenes).
  - Cuadrículas de proyectos (qisuprojects 3×2, otros 4×1) → 1 columna en móvil, 2 en `sm:` si cabe.
  - Tipografía: títulos `text-xl` en móvil / `text-2xl` en `md:`; márgenes con `px-4 md:mx-[2vw]`.
  - Imágenes: `aspect-ratio` (cuadradas o 4/5 según el PDF) + `object-cover`, nunca alturas fijas en vh.
  - El menú pasa a hamburguesa (ver §2).

## 4. Imágenes

- Extraer las fotos del PDF (pypdfium2/pdfimages), recortar si procede, exportar a **WebP** optimizado (~100–200 KB, ancho máx ~1600px).
- Nombres claros en `src/img/`: `qisu-vest-harness.webp`, `qisu-dmatnm.webp`, `qisu-air-canvas.webp`, `qisu-walkies-bag.webp`, `qisu-tags.webp`, `qisu-woof-n-wash.webp`, `qisu-hero.webp` (p4), `sk016-campo.webp`, `sk016-material.webp`, `outdoor-1.webp`, `outdoor-2.webp`, `outdoor-3.webp`, `otros-escora.webp`, `otros-cordoba.webp`, `otros-rayling.webp`, `otros-politics.webp`, `home-dog.webp`, `home-fashion.webp`, `home-furniture.webp` (p3).
- `about.html` mantiene `image.webp` (retrato existente).
- Todas con `alt` descriptivo.

## 5. Contenido a completar (texto literal del PDF)

- **`qisuprojects.html`** — rehacer como p5: título "QISU BRAND, selección de proyectos"; cuadrícula 3×2 de proyectos, cada uno con foto + título + descripción + hashtags en cursiva: Vest Harness (#DogWear), *DMATNM (#Fashion #Illustration), Air Canvas Harness (#DogWear), Walkies Bag (#Fashion #Accessories), Qisu Tags (#Accessories), Woof n Wash (#Accessories).
- **`otros.html`** — nueva, como p8: título "OTROS PROYECTOS"; 4 columnas: Escora (#ProductDesign #Furniture), Córdoba (#FoodDesign #Pottery), The Rayling System (#ServiceDesign #Space), Politics of (In)visibility (#GraphicDesign #Activism), con sus descripciones del PDF.
- **`qisu.html`** — sustituir el retrato por la foto real de Qisu (p4: modelo con teckel).
- **`projects.html`** — las 3 fotos reales de p3 con hashtags debajo; corregir typo "Conocimiento" → "Conocimientos".
- **`outdoor.html`** — corregir typos del texto ("incio"→"inicio", "conesecuentes"→"consecuentes") usando el literal del PDF; añadir flechas de navegación.

## 6. Pulido general

- `<title>` por página: "Arnau Jalón — Diseñador de Producto" (index), "Arnau Jalón — Sobre mí", "Arnau Jalón — Qisu Brand", "Arnau Jalón — SK-016", "Arnau Jalón — Outdoor Workplace", "Arnau Jalón — Otros proyectos".
- `lang="es"` en `index.html`; `<!doctype html>` en `projects.html` (falta).
- Eliminar `@font-face` roto de `style.css` (apunta a `/public/fonts/Helvetica.ttf`, no existe). La pila `"Helvetica Neue", Helvetica, Arial, sans-serif` ya replica el PDF.
- Favicon propio simple (monograma "AJ" en SVG) en vez del de Vite.
- Arreglar HTML inválido: `</p>` suelto en `qisu.html:35` y `qisuprojects.html`, `<div>` sin cerrar en `projects.html` (§sección experiencia).
- Borrar restos de plantilla Vite no usados: `src/counter.js`, `src/javascript.svg`, `public/vite.svg` (tras poner favicon propio).

## 7. Verificación

- `npm run dev` y revisión visual de las 8 páginas en desktop (1280×800) y móvil (375×812), comparando contra los renders del PDF.
- Comprobar: ningún enlace a `#`, ninguna imagen 404, menú hamburguesa funciona, sin scroll horizontal en móvil.
- `npm run build` termina sin errores y `vite.config.ts` incluye las 8 páginas como entradas de Rollup (hoy solo se buildea index — añadir `build.rollupOptions.input` con las 8).

## Fuera de alcance

- Frameworks (React/Astro), CMS, blog, animaciones de transición entre páginas.
- Despliegue/hosting (se puede tratar después).
- Versión en otros idiomas.
