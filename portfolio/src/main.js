const btn = document.getElementById('menu-btn');
const overlay = document.getElementById('menu-overlay');

if (btn && overlay) {
  const setMenuOpen = (isOpen) => {
    overlay.classList.toggle('flex', isOpen);
    overlay.classList.toggle('hidden', !isOpen);
    btn.classList.toggle('is-active', isOpen);
    btn.setAttribute('aria-expanded', String(isOpen));
    btn.setAttribute('aria-label', isOpen ? 'Cerrar menú' : 'Abrir menú');
    document.body.classList.toggle('overflow-hidden', isOpen);
  };

  btn.addEventListener('click', () => {
    setMenuOpen(btn.getAttribute('aria-expanded') !== 'true');
  });

  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape' && btn.getAttribute('aria-expanded') === 'true') {
      setMenuOpen(false);
      btn.focus();
    }
  });
}
