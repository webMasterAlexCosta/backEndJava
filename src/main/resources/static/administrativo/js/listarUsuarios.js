async function validarTokenEPerfil() {
    const token = sessionStorage.getItem('JWT_TOKEN');
    if (!token) {
        console.error('Token não encontrado');
        await Swal.fire({
            title: 'Erro!',
            text: 'Você precisa estar logado para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Sair',
        });
        window.location.href = '/';
        return false;
    }

    const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};
    if (!usuario.perfil || usuario.perfil[0] !== "ADMIN") {
        console.error('Acesso negado: perfil inválido');
        await Swal.fire({
            title: 'Erro!',
            text: 'Você precisa ter um perfil de administrador para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Sair',
        });
        window.location.href = '/';
        return false;
    }

    return true;
}

document.addEventListener('DOMContentLoaded', async () => {
    const autorizado = await validarTokenEPerfil();
    if (autorizado) {
        fetchUsuarios();
    } else {

        return;
    }
});

let user;

function fetchUsuarios() {
    fetch('/usuarios')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const tabela = document.getElementById('usuarios-tabela');
            tabela.innerHTML = '';

            if (!Array.isArray(data.content)) {
                console.error('Formato de dados inesperado:', data);
                return;
            }
            user = data;
            data.content.forEach(usuario => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${usuario.id}</td>
                    <td>${usuario.nome}</td>
                    <td>${usuario.email}</td>
                    <td>${usuario.telefone}</td>
                    <td>${usuario.dataNascimento}</td>
                    <td>${usuario.endereco.logradouro}</td>
                    <td>${usuario.endereco.cep}</td>
                    <td>${usuario.endereco.numero}</td>
                    <td>${usuario.endereco.cidade}</td>
                    <td>${usuario.endereco.bairro}</td>
                    <td>${usuario.endereco.uf}</td>
                    <td class="acoes">
                        <button class="btn-secondary" onclick="editarUsuario('${usuario.id}')">Editar</button>
                        <button class="btn-danger" onclick="excluirUsuario('${usuario.id}')">Excluir</button>
                    </td>
                `;
                tabela.appendChild(tr);
            });
        })
        .catch(error => console.error('Erro ao buscar usuários:', error));
}

function editarUsuario(meuId) {
    const editarUrl = `./adminEditarUsuario.html?id=${meuId}`;
    console.log('Redirecionando para:', editarUrl);
    window.location.href = editarUrl;
}

function excluirUsuario(id) {
    Swal.fire({
        title: 'Tem certeza que deseja excluir este usuário?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sim, excluir!',
        cancelButtonText: 'Cancelar'
    }).then(result => {
        const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};

        if (result.isConfirmed) {
            fetch(`/usuarios/${id}/deletar`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'

                }
            })
            .then(response => {
                if (response.ok) {
                    Swal.fire({
                        title: 'Usuário excluído com sucesso!',
                        icon: 'success',
                        confirmButtonText: 'OK'
                    }).then(() => {
                        fetchUsuarios();
                    });
                } else {
                    throw new Error('Falha ao excluir usuário.');
                }
            })
            .catch(error => {
                Swal.fire({
                    title: 'Erro ao excluir usuário!',
                    text: `Erro: ${error.message}`,
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            });
        }
    });
}

function filtrarUsuarios() {
    const filtro = document.getElementById('filtro-usuario').value.toLowerCase();
    const linhas = document.querySelectorAll('#usuarios-tabela tr');
    linhas.forEach(linha => {
        const nome = linha.cells[2].textContent.toLowerCase();
        linha.style.display = nome.includes(filtro) ? '' : 'none';
    });
}
