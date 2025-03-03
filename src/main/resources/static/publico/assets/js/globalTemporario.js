function addToCart(produto) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    const index = cart.findIndex(item => item.id === produto.id && item.tamanho === produto.tamanho);

    if (index === -1) {
        cart.push(produto);
    } else {
        cart[index].quantidade += produto.quantidade;
    }

    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartCount();
}

function updateCartCount() {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    const count = cart.reduce((acc, item) => acc + item.quantidade, 0);
    document.querySelector('.cart-count').textContent = count;
}

document.addEventListener('DOMContentLoaded', () => {
    updateCartCount();
});
