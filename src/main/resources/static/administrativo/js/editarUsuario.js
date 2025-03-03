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
        const params = new URLSearchParams(window.location.search);
        const id = params.get('id');
        if (id) {
            carregarDadosUsuario(id);
        }
    }
});

async function carregarDadosUsuario(id) {
    const token = sessionStorage.getItem('JWT_TOKEN');
    if (!token) {
        console.error('Token não encontrado');
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/usuarios/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const usuario = await response.json();
        console.log('Dados do usuário carregados:', usuario);
        document.getElementById('usuario-id').value = usuario.id;
        document.getElementById('nome').value = usuario.nome;
        document.getElementById('email').value = usuario.email;
        document.getElementById('telefone').value = usuario.telefone;
        document.getElementById('dataNascimento').value = usuario.dataNascimento;
        document.getElementById('logradouro').value = usuario.endereco.logradouro;
        document.getElementById('cep').value = usuario.endereco.cep;
        document.getElementById('numero').value = usuario.endereco.numero;
        document.getElementById('cidade').value = usuario.endereco.cidade;
        document.getElementById('bairro').value = usuario.endereco.bairro;
        document.getElementById('uf').value = usuario.endereco.uf;

    } catch (error) {
        console.error('Erro ao carregar dados do usuário:', error);
    }
}

document.getElementById('form-edicao-usuario').addEventListener('submit', function(event) {
    event.preventDefault();

    const id = document.getElementById('usuario-id').value;
    const usuario = {
        nome: document.getElementById('nome').value,
        email: document.getElementById('email').value,
        telefone: document.getElementById('telefone').value,
        dataNascimento: document.getElementById('dataNascimento').value,
        endereco: {
            logradouro: document.getElementById('logradouro').value,
            cep: document.getElementById('cep').value.replace("-",""),
            numero: parseInt(document.getElementById('numero').value, 10),
            cidade: document.getElementById('cidade').value,
            bairro: document.getElementById('bairro').value,
            uf: document.getElementById('uf').value
        }
    };

    console.log('Dados do usuário a serem enviados:', usuario);

    const token = sessionStorage.getItem('JWT_TOKEN');
    if (!token) {
        console.error('Token não encontrado');
        return;
    }

    const url = `http://localhost:8080/usuarios/${id}/atualizar`;
    console.log('URL para atualizar usuário:', url);

    Swal.fire({
        title: 'Atualização de Usuário',
        text: 'Você deseja realmente fazer a alteração?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sim, alterar!',
        cancelButtonText: 'Cancelar'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(url, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(usuario)
                });

                if (response.ok) {
                    Swal.fire({
                        title: 'Sucesso!',
                        text: 'Usuário atualizado com sucesso.',
                        icon: 'success',
                        confirmButtonText: 'OK'
                    }).then(() => {
                        window.location.href = './listaUsuarios.html';
                    });
                } else {
                    throw new Error('Erro ao atualizar usuário');
                }
            } catch (error) {
                Swal.fire({
                    title: 'Erro ao atualizar usuário!',
                    text: `Erro: ${error.message}`,
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
            }
        }
    });
});

function buscarEndereco() {
    const cep = document.getElementById('cep').value.replace(/\D/g, '');

    if (cep.length === 8) {
        fetch(`https://viacep.com.br/ws/${cep}/json/`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                if (data.erro) {
                    Swal.fire({
                        title: 'CEP não encontrado',
                        text: 'O CEP informado não foi encontrado.',
                        icon: 'warning',
                        confirmButtonText: 'OK'
                    });
                } else {
                    document.getElementById('logradouro').value = data.logradouro;
                    document.getElementById('bairro').value = data.bairro;
                    document.getElementById('cidade').value = data.localidade;
                    document.getElementById('uf').value = data.uf;
                }
            })
            .catch(error => console.error('Erro ao buscar o endereço:', error));
    }
}

function formatarCep(cep) {
    return cep.replace(/\D/g, '')
              .replace(/^(\d{5})(\d{3})$/, '$1-$2'); //
}

document.getElementById('cep').addEventListener('input', function() {
    const cep = this.value.replace(/\D/g, '');
    if (cep.length <= 8) {
        this.value = formatarCep(cep);
    }
});
