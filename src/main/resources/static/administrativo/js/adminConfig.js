document.addEventListener("DOMContentLoaded", function() {
    const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};
    const usuarioId = usuario.id;

    // Carregar informações do usuário (opcional)
    fetch(`/usuarios/${usuarioId}`)
        .then(response => {
            if (!response.ok) throw new Error('Erro ao carregar usuário: ' + response.statusText);
            return response.json();
        })
        .then(data => {
            // Aqui você pode adicionar código para preencher informações do usuário, se necessário.
        })
        .catch(error => {
            console.error(error);
            Swal.fire('Erro', error.message, 'error');
        });

    // Manipulação do formulário de senha
    const senhaForm = document.getElementById('senha-form');
    const loading = document.getElementById('loading');

    senhaForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const email = document.getElementById('email').value;
        const cpf = document.getElementById('cpf').value;

        // Verificação se os campos foram preenchidos
        if (!email || !cpf) {
            Swal.fire('Erro', 'Por favor, preencha todos os campos.', 'error');
            return;
        }

        // Mostra o efeito de carregamento
        loading.style.display = 'flex';

        // Envio da solicitação para alterar a senha
        fetch('/api/recuperacao/solicitar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, cpf })
        })
        .then(response => {
            if (!response.ok) throw new Error('Erro ao alterar senha: ' + response.statusText);
            Swal.fire('Sucesso', 'Senha alterada com sucesso!', 'success');
            senhaForm.reset(); // Limpa o formulário após o sucesso
        })
        .catch(error => {
            console.error(error);
            Swal.fire('Erro', error.message, 'error');
        })
        .finally(() => {
            // Esconde o efeito de carregamento após a resposta
            loading.style.display = 'none';
        });
    });
});
