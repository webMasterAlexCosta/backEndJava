async function carregarDados() {
    const token = sessionStorage.getItem('JWT_TOKEN');
    if (!token) {
        console.error('Token não encontrado');
        Swal.fire({
            title: 'Erro!',
            text: 'Você precisa estar logado para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        }).then(() => {
            window.location.href = '../publico/loginAdmin.html';
        });
        return;
    }
    const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};

    if(usuario.perfil[0]!="ADMIN"){
    console.error('Token não encontrado');
            Swal.fire({
                title: 'Erro!',
                text: 'Você precisa estar logado para acessar esta página.',
                icon: 'error',
                confirmButtonText: 'Ir para Login',
            }).then(() => {
                window.location.href = '../publico/loginAdmin.html';
            });
            return;
    }
}
document.addEventListener('DOMContentLoaded', () => {
 carregarDados();

    fetch('/produtos')
        .then(response => response.json())
        .then(data => {
            const produtoSelect = document.getElementById('produto-id2');
            data.forEach(produto => {
                const option = document.createElement('option');
                option.value = produto.id;
                option.textContent = produto.nome;
                produtoSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Erro ao carregar produtos:', error));

    function carregarCategorias() {
           fetch('/categorias')
               .then(response => {
                   if (!response.ok) {
                       throw new Error('Erro na resposta da API: ' + response.statusText);
                   }
                   return response.json();
               })
               .then(categorias => {
                   categoriaSelect.innerHTML = '';

                   if (Array.isArray(categorias)) {
                       categorias.forEach(categoria => {
                           const option = document.createElement('option');
                           option.value = categoria.id;
                           option.textContent = categoria.nome;
                           categoriaSelect.appendChild(option);
                       });
                   } else {
                       console.error('Formato de dados inesperado:', categorias);
                   }
               })
               .catch(error => {
                   console.error('Erro ao carregar categorias:', error);
               });
       }

       carregarCategorias();
   });

function mostrarFormularioProduto() {
    const form = document.getElementById('form-produto');
    form.classList.toggle('hidden');
}

document.getElementById('produto-formulario')?.addEventListener('submit', function(e) {
    e.preventDefault();
    Swal.fire({
        title: 'Produto salvo!',
        text: 'Seu produto foi salvo com sucesso.',
        icon: 'success',
        confirmButtonText: 'OK'
    }).then(() => {
       setTimeout(() => {
            location.reload();
       }, 2000);
    });
});

function editarProduto(id) {
    alert(`Editar produto com ID: ${id}`);
    window.location.href = "adminEditarProduto.html";
}

function excluirProduto(id) {
    if (confirm(`Tem certeza que deseja excluir o produto com ID: ${id}?`)) {
        alert(`Produto com ID: ${id} excluído!`);
    }
}

function filtrarProdutos() {
    const filtro = document.getElementById('filtro-produto')?.value.toLowerCase();
    const linhas = document.querySelectorAll('#produtos-tabela tr');
    linhas.forEach(linha => {
        const nomeProduto = linha.cells[1]?.textContent.toLowerCase();
        linha.style.display = nomeProduto.includes(filtro) ? '' : 'none';
    });
}
document.getElementById('associar-form')?.addEventListener('submit', function(e) {
    e.preventDefault();

    Swal.fire({
        title: 'Associação salva!',
        text: 'Seu produto foi salvo com sucesso.',
        icon: 'success',
        confirmButtonText: 'OK'
    }).then(() => {
        const mensagemDiv = document.getElementById('mensagem');
        if (mensagemDiv) {
            mensagemDiv.classList.remove('hidden');
        }
    });

});
function sair() {
    Swal.fire({
        title: 'Logout',
        text: 'Fazendo Logout com sucesso, aguarde, clique em OK e você será redirecionado!',
        icon: 'success',
        confirmButtonText: 'OK'
    }).then(() => {
        sessionStorage.clear();
        window.location.href = "/";
    });
}
