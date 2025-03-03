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
            window.location.href = '../publico/login.html';
        });
        return;
    }
}
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('change-password-form');
    const togglePassword = document.getElementById('togglePassword');
    const currentPasswordField = document.getElementById('current-password');
    const newPasswordField = document.getElementById('new-password');
    const confirmPasswordField = document.getElementById('confirm-password');
    const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};
    const token = sessionStorage.getItem('JWT_TOKEN');

    togglePassword.addEventListener('click', () => {
        const type = currentPasswordField.getAttribute('type') === 'password' ? 'text' : 'password';
        currentPasswordField.setAttribute('type', type);
        newPasswordField.setAttribute('type', type);
        confirmPasswordField.setAttribute('type', type);
        togglePassword.textContent = type === 'password' ? 'Mostrar Senha' : 'Ocultar Senha';
    });
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const currentPassword = currentPasswordField.value;
        const newPassword = newPasswordField.value;
        const confirmPassword = confirmPasswordField.value;

        if (newPassword !== confirmPassword) {
            Swal.fire({
                icon: 'error',
                title: 'Erro',
                text: 'A nova senha e a confirmação não coincidem.'
            });
            return;
        }

        try {
            if (!usuario || !usuario.id) {
                Swal.fire({
                    icon: 'error',
                    title: 'Erro',
                    text: 'ID do usuário não encontrado.'
                });
                return;
            }

            const response = await fetch(`http://localhost:8080/usuarios/${usuario.id}/senha`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    senhaAntiga: currentPassword,
                    senhaNova: newPassword
                })
            });

            if (response.ok) {
                Swal.fire({
                    icon: 'success',
                    title: 'Sucesso',
                    text: 'Senha alterada com sucesso!'
                });
                form.reset();

            } else {
                const error = await response.json();
                Swal.fire({
                    icon: 'error',
                    title: 'Erro',
                    text: 'Erro ao alterar a senha: ' + (error.message || 'Erro desconhecido')
                });
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Erro',
                text: 'Erro na conexão com o servidor: ' + error.message
            });
        }
    });

    carregarDados();
});
