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

async function carregarProduto(id) {
    const token = sessionStorage.getItem('JWT_TOKEN');

    fetch(`/produtos/${id}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro ao carregar produto: ' + response.statusText);
        }
        return response.json();
    })
    .then(produto => {
        document.getElementById('produto-id').value = produto.id;
        document.getElementById('nome').value = produto.nome;
        document.getElementById('preco').value = produto.preco;
        document.getElementById('descricao').value = produto.descricao;
        document.getElementById('imgUrl').value = produto.imgUrl;
    })
    .catch(error => console.error('Erro ao carregar produto:', error));
}

document.addEventListener('DOMContentLoaded', async () => {
    const autorizado = await validarTokenEPerfil();
    if (autorizado) {
        const params = new URLSearchParams(window.location.search);
        const id = params.get('id');
        if (id) {
            carregarProduto(id);
            carregarDadosUsuario(id);
        }
    }
});

document.getElementById('form-editar').addEventListener('submit', function (event) {
    event.preventDefault();

    const id = document.getElementById('produto-id').value;
    const nome = document.getElementById('nome').value;
    const preco = parseFloat(document.getElementById('preco').value);
    const descricao = document.getElementById('descricao').value;
    const imgUrl = document.getElementById('imgUrl').value;

    const produtoAtualizado = {
        nome: nome,
        preco: preco,
        descricao: descricao,
        imgUrl: imgUrl
    };

    const token = sessionStorage.getItem('JWT_TOKEN');

    fetch(`/produtos/${id}/atualizar`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(produtoAtualizado)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro ao atualizar produto: ' + response.statusText);
        }
        return response.json();
    })
    .then(() => {
        Swal.fire({
            title: 'Dados do produto alterados com sucesso!',
            text: 'Clique em OK para voltar à página dos produtos.',
            icon: 'success',
            confirmButtonText: 'OK'
        }).then(() => {
            window.location.href = '../administrativo/listaProdutos.html';
        });
    })
    .catch(error => {
        Swal.fire({
            title: 'Erro ao alterar dados do produto!',
            text: `Clique em OK e insira os dados novamente. Erro: ${error.message}`,
            icon: 'error',
            confirmButtonText: 'OK'
        }).then(() => {
            location.reload();
        });
    });
});
