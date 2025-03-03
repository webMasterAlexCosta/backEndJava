document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('produto-formulario').addEventListener('submit', async function (event) {
        event.preventDefault();

        const token = sessionStorage.getItem('JWT_TOKEN');
        if (!token) {
            console.error('Token não encontrado');

            if (typeof Swal !== 'undefined') {
                await Swal.fire({
                    title: 'Erro!',
                    text: 'Você precisa estar logado para realizar esta ação.',
                    icon: 'error',
                    confirmButtonText: 'Ir para Login',
                }).then(() => {
                    window.location.href = '../loginAdmin.html';
                });
            } else {
                console.error('Swal não está definido. Certifique-se de incluir a biblioteca SweetAlert2.');
                alert('Você precisa estar logado para realizar esta ação. Redirecionando para a página de login...');
                window.location.href = '../loginAdmin.html';
            }

            return;
        }

        const formData = {
            nome: document.getElementById('nome').value,
            preco: document.getElementById('preco').value,
            descricao: document.getElementById('descricao').value,
            imgUrl: document.getElementById('imgUrl').value
        };

        console.log(formData);

        try {
            const response = await fetch('/produtos', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                if (response.status === 400) {
                    const errorData = await response.json();
                    throw new Error(`Erro na solicitação: ${errorData.message || 'Verifique os dados enviados'}`);
                } else if (response.status === 401) {
                    throw new Error('Erro na solicitação: Não autorizado. Verifique seu token de autenticação.');
                } else {
                    throw new Error('Erro na solicitação. Por favor, tente novamente mais tarde.');
                }
            }

            const data = await response.json();

            if (typeof Swal !== 'undefined') {
                Swal.fire({
                    title: 'Sucesso!',
                    text: 'Produto salvo com sucesso.',
                    icon: 'success',
                    confirmButtonText: 'OK'
                });
            } else {
                console.error('Swal não está definido. Certifique-se de incluir a biblioteca SweetAlert2.');
                alert('Produto salvo com sucesso.');
            }

        } catch (error) {
            console.error('Erro:', error);

            if (typeof Swal !== 'undefined') {
                Swal.fire({
                    title: 'Erro!',
                    text: error.message || 'Não foi possível salvar o produto.',
                    icon: 'error',
                    confirmButtonText: 'OK'
                })
            } else {
                console.error('Swal não está definido. Certifique-se de incluir a biblioteca SweetAlert2.');
                alert('Erro ao salvar o produto. Por favor, tente novamente. Detalhes do erro: ' + (error.message || 'Erro desconhecido'));


            }
        }
    });
});