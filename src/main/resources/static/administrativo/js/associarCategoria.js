document.addEventListener('DOMContentLoaded', async function() {
    const associarForm = document.getElementById('associar-form');
    const produtoSelect = document.getElementById('produto-id2');
    const categoriaSelect = document.getElementById('categoria-id');
    const mensagemDiv = document.getElementById('mensagem');
    const imagemProduto = document.getElementById('imagem-produto');

    const token = sessionStorage.getItem('JWT_TOKEN');

    if (!token) {
        console.error('Token não encontrado');
        await Swal.fire({
            title: 'Erro!',
            text: 'Você precisa estar logado para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        }).then(() => {
            window.location.href = '../loginAdmin.html';
        });
        return;
    }

    async function carregarProdutos() {
        try {
            const response = await fetch('/produtos/lista', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            const produtos = await response.json();
            produtoSelect.innerHTML = '';
            produtos.forEach(produto => {
                const option = document.createElement('option');
                option.value = produto.id;
                option.textContent = produto.nome;
                produtoSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Erro ao carregar produtos:', error);
        }
    }

    async function carregarCategorias() {
        try {
            const response = await fetch('/categorias', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (!response.ok) {
                throw new Error('Erro na resposta da API: ' + response.statusText);
            }
            const categorias = await response.json();
            categoriaSelect.innerHTML = '';
            categorias.forEach(categoria => {
                const option = document.createElement('option');
                option.value = categoria.id;
                option.textContent = categoria.nome;
                categoriaSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Erro ao carregar categorias:', error);
        }
    }

    associarForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const produtoId = produtoSelect.value;
        const categoriaId = categoriaSelect.value;

        try {
            const response = await fetch(`/produtos/${produtoId}/categorias/${categoriaId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            const data = await response.json();
            mostrarMensagem('Produto associado à categoria com sucesso!', 'success');
        } catch (error) {
            console.error('Erro ao associar produto à categoria:', error);
            new Noty({
                text: 'Erro ao associar produto à categoria',
                type: 'error',
                timeout: 3000
            }).show();
        }
    });

    produtoSelect.addEventListener('change', async function() {
        const produtoId = produtoSelect.value;
        if (produtoId) {
            try {
                const response = await fetch(`/produtos/${produtoId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                const produto = await response.json();
                if (produto.imgUrl) {
                    imagemProduto.src = produto.imgUrl;
                    imagemProduto.alt = `Imagem do produto ${produto.nome}`;
                }
            } catch (error) {
                console.error('Erro ao carregar a imagem do produto:', error);
                imagemProduto.src = '';
                imagemProduto.alt = 'Imagem não disponível';
            }
        }
    });


    const removerCategoriaButton = document.getElementById('remover-categoria');
    removerCategoriaButton.addEventListener('click', async function() {
        const produtoId = produtoSelect.value;
        const categoriaId = categoriaSelect.value;

        if (!produtoId || !categoriaId) {
            Swal.fire({
                title: 'Erro!',
                text: 'Por favor, selecione um produto e uma categoria.',
                icon: 'error',
                confirmButtonText: 'Ok'
            });
            return;
        }

        try {
            const response = await fetch(`/produtos/${produtoId}/categorias/${categoriaId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                Swal.fire({
                    title: 'Sucesso!',
                    text: 'Categoria removida do produto com sucesso!',
                    icon: 'success',
                    confirmButtonText: 'Ok'
                });

            } else {
                throw new Error('Erro ao remover a categoria');
            }
        } catch (error) {
            console.error('Erro ao remover categoria:', error);
            Swal.fire({
                title: 'Erro!',
                text: 'Não foi possível remover a categoria do produto.',
                icon: 'error',
                confirmButtonText: 'Ok'
            });
        }
    });

    await carregarProdutos();
    await carregarCategorias();

    function mostrarMensagem(mensagem, tipo) {
        mensagemDiv.textContent = mensagem;
        mensagemDiv.className = tipo === 'success' ? 'success' : 'error';
        mensagemDiv.classList.remove('hidden');
    }
});
