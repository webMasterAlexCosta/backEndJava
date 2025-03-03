document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('liberarLogin');
    const errorMessageElement = document.getElementById('errorMessage');

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const userEmail = document.getElementById('email').value;
        const userSenha = document.getElementById('password').value;

        const confirmResult = await Swal.fire({
            title: 'Confirmação de Login',
            text: 'Você tem certeza de que deseja fazer login?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Sim, Fazer Login',
            cancelButtonText: 'Cancelar'
        });

        if (confirmResult.isConfirmed) {
            try {

                const response = await fetch('https://solan4681.c44.integrator.host/login/admin', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        email: userEmail,
                        senha: userSenha
                    })
                });

                const data = await response.json();



                if (response.ok) {
                    sessionStorage.setItem('JWT_TOKEN', data.token);
                    sessionStorage.setItem('usuario', JSON.stringify(data.user));

                    await Swal.fire({
                        title: 'Sucesso!',
                        text: data.message,
                        icon: 'success',
                        timer: 3000,
                        showConfirmButton: false
                    });


                    const nextStep = await Swal.fire({
                        title: 'O que você gostaria de fazer agora?',
                        text: 'Escolha uma das opções abaixo:',
                        icon: 'question',
                        showCancelButton: true,
                        confirmButtonText: 'Ir para o ADMIN?',
                        cancelButtonText: 'Ir para a Lista de Páginas'
                    });

                    if (nextStep.isConfirmed) {
                        window.location.href = '/administrativo/adminIndex.html';
                    } else if (nextStep.dismiss === Swal.DismissReason.cancel) {
                        const pageChoice = await Swal.fire({
                            title: 'Escolha uma página:',
                            icon: 'info',
                            showCancelButton: true,
                            confirmButtonText: 'Voltar ao Inicio',
                            cancelButtonText: 'Editar Produtos',
                            footer: `
                                <button type="button" class="swal2-confirm swal2-styled" onclick="window.location.href='/administrativo/listaProdutos.html'">listar Produtos</button>
                                <button type="button" class="swal2-confirm swal2-styled" onclick="window.location.href='/administrativo/listaUsuarios.html'">listar Usuarios</button>
                            `
                        });

                        if (pageChoice.isConfirmed) {
                            window.location.href = '/';
                        } else if (pageChoice.dismiss === Swal.DismissReason.cancel) {
                            window.location.href = '/administrativo/adminEditarProduto.html';
                        }
                    }
                } else {
                    handleError(data.error || 'Desconhecido');
                }
            } catch (error) {
                handleError(error.message);
            }
        }
    });

    function handleError(message) {
        errorMessageElement.textContent = `Erro na requisição: ${message}`;
        errorMessageElement.classList.remove('hidden');
        Swal.fire({
            title: 'Erro!',
            text: `Erro na requisição: ${message}`,
            icon: 'error',
            timer: 3000,
            showConfirmButton: false
        });
    }

    async function acessarRecursoProtegido() {
        const token = sessionStorage.getItem('JWT_TOKEN');

        if (!token) {
            console.error('Token não encontrado');
            alert('Token não encontrado');
            return;
        }

        try {
            const response = await fetch('https://solan4681.c44.integrator.host/administrativo', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            const data = await response.json();


            if (response.ok) {
                console.log(data);
            } else {

            }
        } catch (error) {

        }
    }
});

