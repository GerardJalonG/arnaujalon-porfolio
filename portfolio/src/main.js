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
