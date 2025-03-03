
let paginaAtual = 1;
const produtosPorPagina = 3;
const token = sessionStorage.getItem('JWT_TOKEN');

document.addEventListener("DOMContentLoaded", () => {
    fetchProdutos(paginaAtual);
    updateCartCount();
});


let currentSlide = 0;
let slideInterval = 0;

function showSlide(index) {
    const carousel = document.querySelector('.carousel');
    const totalSlides = document.querySelectorAll('.carousel-item').length;

    if (index >= totalSlides) {
        currentSlide = 0;
    } else if (index < 0) {
        currentSlide = totalSlides - 1;
    } else {
        currentSlide = index;
    }

    carousel.style.transform = `translateX(-${currentSlide * 100}%)`;
}

function nextSlide() {
    showSlide(currentSlide + 1);
    clearInterval(slideInterval);
    slideInterval = setInterval(nextSlide, 5000);
}

function prevSlide() {
    showSlide(currentSlide - 1);
    clearInterval(slideInterval);
    slideInterval = setInterval(nextSlide, 5000);
}

function startSlide() {
    slideInterval = setInterval(nextSlide, 5000);
}

function stopSlide() {
    clearInterval(slideInterval);
}

startSlide();

document.getElementById('buscarBtn').addEventListener('click', () => {
    const filtro = document.getElementById('nomeProduto').value;
    pesquisaProdutos(filtro);
});

async function pesquisaProdutos() {
    const filtroDesktop = document.getElementById('searchInput').value;
    const filtroMobile = document.getElementById('searchInputMobile').value;

    // Usa o valor do input que não está vazio
    const filtro = filtroDesktop || filtroMobile;

    if (!filtro) {
        Swal.fire({
            title: 'Campo de Busca Vazio',
            text: 'Por favor, digite o nome de um produto para pesquisar!',
            icon: 'warning',
            confirmButtonText: 'Ok'
        });
        return;
    }
    try {
        const response = await fetch(`http://localhost:8080/produtos/buscar?filtro=${encodeURIComponent(filtro)}`);

        if (!response.ok) {
            throw new Error('Erro 500 na sua requisição: ' + response.statusText);
        }

        const data = await response.json();
        console.log("Dados recebidos:", data);

       // Renomeei o titulo par Resultados da busca ao buscar produtos
       const titulosProdutos = document.querySelector('.titulos-produtos h1');
       if (titulosProdutos) {
           titulosProdutos.textContent = 'Resultado da busca';
       }

        exibirProdutos(data);
    } catch (error) {
        console.error('Erro ao buscar produtos:', error);
        alert('Erro ao buscar produtos: ' + error.message);
    }
    document.getElementById('paginas').style.display = "none";
    document.querySelector('.hero').style.display = "none";
    document.querySelector('.fundo-slider-icon').style.display = "none";
}


async function fetchProdutos(pagina) {
    try {
        const response = await fetch(`http://localhost:8080/produtos/procurarCategoria?page=${pagina - 1}&size=${produtosPorPagina}&categoriaId=${4}`);


        if (!response.ok) {
            throw new Error('Erro 500 na sua requisição: ' + response.statusText);
        }
        const data = await response.json();
        console.log("Dados recebidos:", data);

        const produtos = data.content;
        const totalPaginas = data.totalPages;

        exibirProdutos(produtos);
        exibirPaginas(pagina, totalPaginas);
    } catch (error) {
        console.error('Erro ao buscar produtos:', error);
        alert('Erro ao buscar produtos: ' + error.message);
    }
}


function exibirProdutos(produtos) {
    const container = document.getElementById('produtos');
    container.innerHTML = '';

    if (produtos.length === 0) {
        container.innerHTML = '<p>Nenhum produto encontrado.</p>';
        return;
    }
    
    produtos.forEach(produto => {
        const produtoDiv = document.createElement('div');
        produtoDiv.className = 'produto';

        produtoDiv.innerHTML = `
            <img src="${produto.imgUrl}" alt="${produto.nome}" width="150px" class="produto-image" />
            <section class="produto-info">
                <h2>${produto.nome}</h2>
                <span class="promo-old-price"><h3><s>R$ 299,99</s></h3></span>
                <span class="produto-price"><h3>R$ ${produto.preco.toFixed(2)}</h3></span>
                <span>Quantidade</span>
                <select name="quantidade" class="quantidade">
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                </select>
                <span>Tamanho</span>
                <select name="tamanho" class="tamanho">
                    <option value="P">P</option>
                    <option value="M">M</option>
                    <option value="G">G</option>
                    <option value="GG">GG</option>
                </select>
            </section>
        `;

        const buttonDiv = document.createElement('div');
        const maxChars = 50;
        const descricao = produto.descricao;
        const shortDesc = descricao.length > maxChars ? descricao.slice(0, maxChars) + '...' : descricao;

        buttonDiv.innerHTML = `
        <section class="produto-info">
            <button class="adicionarCarrinho btn-produto button-hover-background" data-id="${produto.id}" data-nome="${produto.nome}" data-preco="${produto.preco}" data-tamanho="${produto.tamanho}">Carrinho <i class="fa-solid fa-cart-shopping iconProduto"></i></button>
            <button class="comprando btn-produto button-hover-background" data-id="${produto.id}" data-nome="${produto.nome}" data-preco="${produto.preco}" data-tamanho="${produto.tamanho}">Comprar<i class="fa-solid fa-credit-card iconProduto"></i></button>
            <p class="produto-desc">${shortDesc}</p>
            <button class="veja-mais">${descricao.length > maxChars ? "Ver mais" : ""}</button>
        </section>
        `;

        if (descricao.length > maxChars) {
            const fullDesc = document.createElement('p');
            fullDesc.classList.add('full-desc');
            fullDesc.textContent = descricao;
            fullDesc.style.display = 'none';

            buttonDiv.appendChild(fullDesc);

            const vejaMaisButton = buttonDiv.querySelector('.veja-mais');
            vejaMaisButton.addEventListener('click', () => {
                const produtoDesc = buttonDiv.querySelector(".produto-desc");
                produtoDesc.style.display = "none";
                fullDesc.style.display = 'block';
                vejaMaisButton.style.display = 'none';
            });
        }

        produtoDiv.appendChild(buttonDiv);
        container.appendChild(produtoDiv);
    });

    adicionarEventos();
}


function exibirPaginas(paginaAtual, totalPaginas) {
    const paginaContainer = document.getElementById('paginas');
    paginaContainer.innerHTML = '';

    if (paginaAtual > 1) {
        const paginaAnteriorButton = document.createElement('button');
        paginaAnteriorButton.textContent = 'Página Anterior';
        paginaAnteriorButton.addEventListener('click', () => {
            fetchProdutos(paginaAtual - 1);
        });
        paginaContainer.appendChild(paginaAnteriorButton);
    }

    for (let i = 1; i <= totalPaginas; i++) {
        const paginaButton = document.createElement('button');
        paginaButton.textContent = i;
        paginaButton.className = i === paginaAtual ? 'pagina-atual' : '';
        paginaButton.disabled = (i === paginaAtual);
        paginaButton.addEventListener('click', () => {
            fetchProdutos(i);
        });

        paginaContainer.appendChild(paginaButton);
    }

    if (paginaAtual < totalPaginas) {
        const paginaProximaButton = document.createElement('button');
        paginaProximaButton.textContent = 'Próxima Página';
        paginaProximaButton.addEventListener('click', () => {
            fetchProdutos(paginaAtual + 1);
        });
        paginaContainer.appendChild(paginaProximaButton);
    }
}


function adicionarEventos() {
    const buttonsAdicionar = document.querySelectorAll('.adicionarCarrinho');
    const buttonsComprar = document.querySelectorAll('.comprando');

    buttonsAdicionar.forEach(button => {
        button.addEventListener('click', () => {
            const produtoDiv = button.closest('.produto');
            const imgElement = produtoDiv.querySelector('.produto-image');
            const quantidadeSelect = produtoDiv.querySelector('.quantidade');
            const tamanhoSelect = produtoDiv.querySelector('.tamanho');

            const imagem = imgElement ? imgElement.src : '';
            const id = button.getAttribute('data-id');
            const nome = button.getAttribute('data-nome');
            const preco = parseFloat(button.getAttribute('data-preco'));
            const quantidade = parseInt(quantidadeSelect.value);
            const tamanho = tamanhoSelect.value;

            const produto = {
                imagem,
                id,
                nome,
                preco,
                quantidade,
                tamanho
            };

            Swal.fire({
                title: 'Adicionar ao Carrinho',
                text: `Você deseja adicionar ${quantidade} unidade(s) de ${nome} tamanho ${tamanho} ao carrinho?`,
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Sim, adicionar!',
                cancelButtonText: 'Não, cancelar'
            }).then(result => {
                if (result.isConfirmed) {
                    addToCart(produto);
                    updateCartCount();

                    Swal.fire({
                        title: 'Produto Adicionado!',
                        text: `${nome} tamanho ${tamanho} foi adicionado ao seu carrinho com sucesso.`,
                        icon: 'success',
                        confirmButtonText: 'Ok'
                    }).then(() => {
                        Swal.fire({
                            title: 'O que você deseja fazer?',
                            text: 'Deseja continuar comprando ou ir para o carrinho?',
                            icon: 'info',
                            showCancelButton: true,
                            confirmButtonText: 'Ir para o carrinho',
                            cancelButtonText: 'Continuar comprando'
                        }).then(result => {
                            if (result.isConfirmed) {
                                window.location.href = '/publico/carrinho.html';
                            }
                        });
                    });
                } else {
                    Swal.fire({
                        title: 'Produto Não Adicionado',
                        text: `${nome} tamanho ${tamanho} não foi adicionado ao seu carrinho.`,
                        icon: 'info',
                        confirmButtonText: 'Ok'
                    });
                }
            });
        });
    });

    buttonsComprar.forEach(button => {
        button.addEventListener('click', () => {
            const produtoDiv = button.closest('.produto');
            const imgElement = produtoDiv.querySelector('.produto-image');
            const quantidadeSelect = produtoDiv.querySelector('.quantidade');
            const tamanhoSelect = produtoDiv.querySelector('.tamanho');

            const imagem = imgElement ? imgElement.src : '';
            const id = button.getAttribute('data-id');
            const nome = button.getAttribute('data-nome');
            const preco = parseFloat(button.getAttribute('data-preco'));
            const quantidade = parseInt(quantidadeSelect.value);
            const tamanho = tamanhoSelect.value;
            const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};

            if (!usuario.perfil) {
                Swal.fire({
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
                const produto = {
                    imagem,
                    id,
                    nome,
                    preco,
                    quantidade,
                    tamanho
                };

                addToCart(produto);
                updateCartCount();
                window.location.href = '/publico/carrinho.html';
            }
        });
    });
}
