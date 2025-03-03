document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('recuperacaoForm').addEventListener('submit', function(event) {
                event.preventDefault();

                const email = document.getElementById('email').value;
                const cpf = document.getElementById('cpf').value.replace("-","").replace(".","");

                // Exibe o carregamento
                document.getElementById('loading').style.display = 'block';
                document.getElementById('mensagem').innerText = '';

                fetch('/api/recuperacao/solicitar', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, cpf })
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Usuário não encontrado ou erro ao enviar email');
                    }
                    return response.text();
                })
                .then(data => {
                    document.getElementById('mensagem').innerText = data;
                    document.getElementById('recuperacaoForm').reset();
                })
                .catch(error => {
                    document.getElementById('mensagem').innerText = error.message;
                })
                .finally(() => {
                    // Esconde o carregamento
                    document.getElementById('loading').style.display = 'none';
                });
            });

            document.getElementById('voltar').addEventListener('click', function() {
                // Lógica para voltar (pode redirecionar ou fechar a página atual)
                window.history.back(); // Exemplo de voltar para a página anterior
            });
        });