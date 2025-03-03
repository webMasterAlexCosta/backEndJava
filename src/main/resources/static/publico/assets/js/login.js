document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('liberarLogin');
    const togglePassword = document.getElementById('togglePassword');
    const passwordField = document.getElementById('password');

    togglePassword.addEventListener('click', () => {
        const type = passwordField.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordField.setAttribute('type', type);
        togglePassword.textContent = type === 'password' ? 'Mostrar Senha' : 'Ocultar Senha';
    });

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const userEmail = document.getElementById('email').value;
        const senha = passwordField.value;

        Swal.fire({
            title: 'Confirmação de Login',
            text: 'Você tem certeza de que deseja fazer login?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Sim, Fazer Login',
            cancelButtonText: 'Cancelar'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch('http://localhost:8080/login/cliente', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            email: userEmail,
                            senha: senha
                        }),
                    });

                    const data = await response.json();

                    if (response.ok) {
                        sessionStorage.setItem('JWT_TOKEN', data.token);
                        sessionStorage.setItem('usuario', JSON.stringify(data.user));

                        Swal.fire({
                            title: 'Logado com Sucesso!',
                            text: data.message,
                            icon: 'success',
                            confirmButtonText: 'Ir para a Página Inicial',
                            confirmButtonColor: '#007FFF',
                            showCancelButton: true,
                            allowOutsideClick: false,
                            cancelButtonText: 'Ir para o Carrinho',
                            cancelButtonColor: '#b60b0b',
                            
                        }).then(function(result) {
                            if (result.value) {
                                window.location.href = '/publico/index.html'; 
                            } else if(result.dismiss == 'cancel'){
                                window.location.href = '/publico/carrinho.html';
                            }
                            
                        });
                    } else {
                        Swal.fire({
                            title: 'Erro!',
                            text: `Erro na requisição: ${data.error}`,
                            icon: 'error',
                            timer: 3000,
                            allowOutsideClick: false,
                            showConfirmButton: false
                        });
                    }
                } catch (error) {
                    Swal.fire({
                        title: 'Erro!',
                        text: `Erro na requisição: ${error.message}`,
                        icon: 'error',
                        timer: 3000,
                        allowOutsideClick: false,
                        showConfirmButton: false
                    });
                }
            }
        });
    });
});
