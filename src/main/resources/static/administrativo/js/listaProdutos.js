async function validarTokenEPerfil() {
    const token = sessionStorage.getItem('JWT_TOKEN');
    if (!token) {
        console.error('Token não encontrado');
        await Swal.fire({
            title: 'Erro!',
            text: 'Você precisa estar logado para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        });
        window.location.href = '../loginAdmin.html';
        return false;
    }

    const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};
    if (!usuario.perfil || usuario.perfil[0] !== "ADMIN") {
        console.error('Acesso negado: perfil inválido');
        await Swal.fire({
            title: 'Erro!',
            text: 'Você precisa ter um perfil de administrador para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        });
        window.location.href = '../loginAdmin.html';
        return false;
    }

    return true;
}

document.addEventListener("DOMContentLoaded", async () => {
    const autorizado = await validarTokenEPerfil();
    if (autorizado) {
        carregarProdutos();
    }
});

function carregarProdutos(filtro = '') {
    const token = sessionStorage.getItem('JWT_TOKEN');

    fetch(`/produtos/buscar?filtro=${encodeURIComponent(filtro)}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro na resposta da API');
        }
        return response.json();
    })
    .then(produtos => {
        const tabela = document.getElementById('produtos-tabela');
        tabela.innerHTML = '';

        produtos.forEach(produto => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${produto.id}</td>
                <td>${produto.nome}</td>
                <td>${produto.preco.toFixed(2)}</td>
                <td>${produto.descricao}</td>
                <td><img src="${produto.imgUrl}" alt="${produto.nome}" /></td>
                <td class="acoes">
                    <button class="btn-secondary" onclick="editarProduto(${produto.id})">Editar</button>
                    <button class="btn-danger" onclick="excluirProduto(${produto.id})">Excluir</button>
                </td>
            `;
            tabela.appendChild(tr);
        });
    })
    .catch(error => console.error('Erro ao carregar produtos:', error));
}

function filtrarProdutos() {
    const filtro = document.getElementById('filtro-produto').value;
    console.log(`Filtro aplicado: ${filtro}`);
    carregarProdutos(filtro);
}

function editarProduto(id) {
    window.location.href = `./adminEditarProduto.html?id=${id}`;
}

function excluirProduto(id) {
    Swal.fire({
        title: 'Tem certeza que deseja excluir este produto?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sim, excluir!',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            const token = sessionStorage.getItem('JWT_TOKEN');

            fetch(`/produtos/${id}/deletar`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erro ao excluir produto');
                }
                return response.json();
            })
            .then(() => {
                Swal.fire({
                    title: 'Produto excluído com sucesso!',
                    icon: 'success',
                    confirmButtonText: 'OK'
                }).then(() => {
                    carregarProdutos();
                });
            })
            .catch(error => {
                Swal.fire({
                    title: 'Erro ao excluir produto!',
                    text: `Erro: ${error.message}`,
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            });
        }
    });
}
