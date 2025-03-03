const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};
const token = sessionStorage.getItem('JWT_TOKEN');

async function carregarDados() {
    if (!token) {
        window.location.href = '../login.html';
        return;
    }

    if (!usuario.perfil || usuario.perfil[0] !== "CLIENT") {
        window.location.href = '../login.html';
        return;
    }
}

async function validarTokenEPerfil() {
    if (!token) {
        console.error('Token não encontrado');
        await Swal.fire({
            title: 'Erro!',
            text: 'Você precisa estar logado para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        });
        window.location.href = '../login.html';
        return false;
    }

    if (!usuario.perfil || usuario.perfil[0] !== "CLIENT") {
        console.error('Acesso negado: perfil inválido');
        await Swal.fire({
            title: 'Erro!',
            text: 'Você precisa ter um perfil de cliente para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        });
        window.location.href = '../login.html';
        return false;
    }

    return true;
}

document.addEventListener('DOMContentLoaded', async () => {
    carregarDados();
    const autorizado = await validarTokenEPerfil();
    if (autorizado) {
        await buscarHistoricoPedidos();
        preencherDadosUsuario();
    }
});

async function buscarHistoricoPedidos() {
    try {
        console.log('Token utilizado:', token);
        const resposta = await fetch('http://localhost:8080/pedidos/usuario', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!resposta.ok) {
            const errorResponse = await resposta.json();
            console.error('Erro ao buscar dados:', errorResponse);
            throw new Error('Erro ao buscar dados: ' + resposta.statusText);
        }

        const pedidos = await resposta.json();
        console.log('Dados recebidos:', pedidos);
        exibirHistoricoPedidos(pedidos);
    } catch (erro) {
        console.error('Erro ao buscar meus pedidos:', erro);
    }
}

function exibirHistoricoPedidos(pedidos) {
    const ul = document.getElementById('historico-pedidos').querySelector('ul');
    ul.innerHTML = '';

    pedidos.forEach(pedido => {
        const li = document.createElement('li');
        li.innerHTML = `
            <strong>Pedido #${pedido.numeroPedido}</strong><br>
            Data: ${new Date(pedido.momento).toLocaleString()}<br>
            Status: ${pedido.statusPedido}<br>
            Total: R$ ${pedido.total.toFixed(2)}<br>
            <strong>Cliente:</strong><br>
            Nome: ${pedido.client.nome}<br>
            Email: ${pedido.client.email}<br>
            Telefone: ${pedido.client.telefone}<br>
            Data de Nascimento: ${pedido.client.dataNascimento}<br>
            Endereço:<br>
            Logradouro: ${pedido.client.endereco.logradouro}<br>
            CEP: ${pedido.client.endereco.cep}<br>
            Número: ${pedido.client.endereco.numero}<br>
            Bairro: ${pedido.client.endereco.bairro}<br>
            Cidade: ${pedido.client.endereco.cidade}<br>
            Complemento: ${pedido.client.endereco.complemento}<br>
            Estado: ${pedido.client.endereco.uf}<br>
            <ul class="product-list">
                ${pedido.items.map(item => `
                    <li class="product-item">
                        <strong>Produto:</strong><br>
                        <img src="${item.imgUrl}" alt="${item.nome}" class="product-image"><br>
                        Preço: R$ ${item.preco.toFixed(2)}<br>
                        Quantidade: ${item.quantidade}<br>
                        Tamanho: ${item.tamanho}<br>
                        Subtotal: R$ ${item.subTotal.toFixed(2)}
                    </li>
                `).join('')}
            </ul>
        `;
        ul.appendChild(li);
    });
}

function preencherDadosUsuario() {
    document.getElementById('nome').value = usuario.nome || '';
    document.getElementById('email').value = usuario.email || '';
    document.getElementById('cep').value = usuario.endereco?.cep || '';
    document.getElementById('logradouro').value = usuario.endereco?.logradouro || '';
    document.getElementById('numero').value = usuario.endereco?.numero || '';
    document.getElementById('complemento').value = usuario.endereco?.complemento || '';
    document.getElementById('bairro').value = usuario.endereco?.bairro || '';
    document.getElementById('cidade').value = usuario.endereco?.cidade || '';
    document.getElementById('estado').value = usuario.endereco?.uf || '';
    document.getElementById('data-nascimento').value = usuario.dataNascimento || '';
}

function sair() {
    Swal.fire({
        title: 'Tem certeza?',
        text: "Você deseja realmente sair do sistema?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sim, sair',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: 'Desconectando...',
                text: "Você será desconectado do sistema.",
                icon: 'info',
                timer: 2000,
                showConfirmButton: false
            }).then(() => {
                sessionStorage.clear();
                window.location.href = "/";
            });
        } else {
            Swal.fire({
                title: 'Ação cancelada',
                text: 'Você cancelou a operação de saída.',
                icon: 'info',
                timer: 2000,
                showConfirmButton: false
            });
        }
    });
}
