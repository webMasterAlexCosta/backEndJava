document.addEventListener('DOMContentLoaded', function() {
    const dropdown = document.querySelector('.dropdown-menu');
const toggleButton = document.querySelector('.nav-submenu-icon');

toggleButton.addEventListener('click', () => {
    dropdown.classList.toggle('show');
});
});