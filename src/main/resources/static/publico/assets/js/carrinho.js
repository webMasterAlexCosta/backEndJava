document.addEventListener('DOMContentLoaded', () => {
    updateCartUI();

    document.querySelectorAll(".remove-btn").forEach(button => {
        button.addEventListener("click", removeProduct);
    });

    document.querySelectorAll(".quantity-input").forEach(input => {
        input.addEventListener("change", updateQuantity);
    });
});

function addProdutoCarrinho(event) {
    const button = event.target;
    const productInfos = button.closest('.produto');
    const productImage = productInfos.querySelector(".produto-image").src;
    const productName = productInfos.querySelector(".produto-title").innerText;
    const productPrice = parseFloat(productInfos.querySelector(".produto-price").innerText.replace('R$', '').replace(',', '.'));
    const productTamanho = parseInt(productInfos.querySelector(".tamanho").value);
    const productQuantidade = parseInt(productInfos.querySelector(".quantidade").value);

    let cart = JSON.parse(localStorage.getItem('cart')) || [];

    const existingProductIndex = cart.findIndex(item => item.nome === productName && item.tamanho === productTamanho);

    if (existingProductIndex > -1) {

        cart[existingProductIndex].quantidade += productQuantidade;
    } else {

        cart.push({
            id: new Date().getTime().toString(),
            nome: productName,
            tamanho: productTamanho,
            preco: productPrice,
            quantidade: productQuantidade,
            imagem: productImage
        });
    }

    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartUI();
}

function removeProduct(event) {
    const index = parseInt(event.target.getAttribute('data-index'), 10);
    Swal.fire({
        title: 'Confirmar Remoção',
        text: 'Você deseja remover este item do carrinho?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sim, remover',
        cancelButtonText: 'Cancelar'
    }).then(result => {
        if (result.isConfirmed) {
            let cart = JSON.parse(localStorage.getItem('cart')) || [];
            cart.splice(index, 1);
            localStorage.setItem('cart', JSON.stringify(cart));
            updateCartUI();

            const total = parseFloat(document.querySelector('.cart-summary p').innerText.replace('Total: R$', '').replace(',', '.'));

            if (total === 0) {
                Swal.fire({
                    title: 'Carrinho Vazio',
                    text: 'Seu carrinho está vazio.',
                    icon: 'info',
                    confirmButtonText: 'OK'
                });
            } else {
                Swal.fire({
                    title: 'Item Removido',
                    text: 'Você deseja remover outro item ou finalizar a compra?',
                    icon: 'info',
                    showCancelButton: true,
                    confirmButtonText: 'Finalizar Compra',
                    cancelButtonText: 'Remover Outro Item'
                }).then(result => {
                    if (result.isConfirmed) {
                        window.location.href = '/publico/checkout.html';
                    }
                });
            }
        }
    });
}

function updateQuantity(event) {
    const index = parseInt(event.target.getAttribute('data-index'), 10);
    const newQuantity = parseInt(event.target.value, 10);
    let cart = JSON.parse(localStorage.getItem('cart')) || [];

    if (index >= 0 && index < cart.length) {
        cart[index].quantidade = newQuantity;
        localStorage.setItem('cart', JSON.stringify(cart));
        updateCartUI();
    }
}

function updateCartUI() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const cartItems = document.getElementById('cart-items');
    cartItems.innerHTML = '';

    cart.forEach((item, index) => {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td><img src="${item.imagem}" alt="${item.nome}" class="cart-img" /></td>
            <td><h3>${item.nome}</h3></td>
            <td><span>Tamanho:</span><p>${item.tamanho}</p></td>
            <td><span>Quantidade:</span><input type="number" value="${item.quantidade}" min="1" class="quantity-input" data-index="${index}" /></td>
            <td><span>Preço Unitário:</span>R$ ${item.preco.toFixed(2)}</td>
            <td><span>Sub-Total:</span>R$ ${(item.preco * item.quantidade).toFixed(2)}</td>
            <td><button class="remove-btn" data-index="${index}">Remover</button></td>
        `;

        cartItems.appendChild(row);
    });

    updateCartSummary();

    document.querySelectorAll(".remove-btn").forEach(button => {
        button.addEventListener("click", removeProduct);
    });

    document.querySelectorAll(".quantity-input").forEach(input => {
        input.addEventListener("change", updateQuantity);
    });
}

function updateCartSummary() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const total = cart.reduce((acc, item) => acc + (item.preco * item.quantidade), 0);

    document.querySelector('.cart-summary p').innerHTML = `<strong>Total:</strong> R$ ${total.toFixed(2)}`;
}

function fazerCompra(event) {
    event.preventDefault();
    const total = parseFloat(document.querySelector('.cart-summary p').innerText.replace('Total: R$', '').replace(',', '.'));

    if (total === 0) {
        return Swal.fire({
            title: 'Carrinho Vazio',
            text: 'Seu Carrinho já está Vazio',
            icon: 'warning',
            confirmButtonText: 'OK'
        });
    } else if (!usuario.perfil) {
        return Swal.fire({
            title: 'Você precisa estar logado para poder comprar',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sim, Fazer Login!',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = '/publico/login.html';
            }
        });
    } else {
        return Swal.fire({
            title: 'Você está indo para a última etapa para finalizar sua compra. Deseja continuar?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sim, Continuar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = "/publico/checkout.html";
            }
        });
    }
}

function limpar(event) {
    event.preventDefault();
    const total = parseFloat(document.querySelector('.cart-summary p').innerText.replace('Total: R$', '').replace(',', '.'));

    if (total === 0) {
        return Swal.fire({
            title: 'Carrinho Vazio',
            text: 'Seu Carrinho já está Vazio',
            icon: 'warning',
            confirmButtonText: 'OK'
        });
    } else {
        return Swal.fire({
            title: 'Deseja remover todos os produtos?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sim, Limpar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                localStorage.setItem('cart', JSON.stringify([]));
                updateCartUI();
                Swal.fire({
                    title: 'Você esvaziou seu carrinho',
                    icon: 'success',
                    showCancelButton: false,
                    confirmButtonText: 'OK'
                });
            }
        });
    }
}
