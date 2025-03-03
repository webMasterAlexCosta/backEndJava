const token = sessionStorage.getItem('JWT_TOKEN');
carregarDados();

function carregarDados() {
    if (!token) {
        window.location.href = '../publico/login.html';
        return;
    }

    const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};
    if (!usuario.perfil || usuario.perfil[0] !== "CLIENT") {
        window.location.href = '../publico/login.html';
        return;
    }
}

function formatarCEP(cep) {
    cep = cep.replace(/\D/g, '');
    return cep.replace(/(\d{5})(\d{3})/, '$1-$2');
}

document.getElementById("cep").addEventListener("input", function() {
    this.value = formatarCEP(this.value);
    if (this.value.length === 9) {
        buscarEndereco();
    }
});

async function buscarEndereco() {
    const cep = document.getElementById("cep").value.replace(/\D/g, '');

    if (cep.length === 8) {
        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await response.json();

            if (!data.erro) {
                document.getElementById("logradouro").value = data.logradouro;
                document.getElementById("bairro").value = data.bairro;
                document.getElementById("cidade").value = data.localidade;
                document.getElementById("estado").value = data.uf;
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'CEP não encontrado',
                    text: 'Por favor, verifique o CEP informado.'
                });
            }
        } catch (error) {
            console.error('Erro ao buscar o endereço:', error);
            Swal.fire({
                icon: 'error',
                title: 'Erro',
                text: 'Não foi possível buscar o endereço. Tente novamente mais tarde.'
            });
        }
    } else {
        Swal.fire({
            icon: 'warning',
            title: 'CEP inválido',
            text: 'Por favor, informe um CEP válido.'
        });
    }
}

document.querySelector("form").addEventListener("submit", async function(event) {
    event.preventDefault();

    const endereco = {
        logradouro: document.getElementById("logradouro").value,
        cep: document.getElementById("cep").value.replace("-", ""),
        numero: document.getElementById("numero").value,
        cidade: document.getElementById("cidade").value,
        bairro: document.getElementById("bairro").value,
        complemento: document.getElementById("complemento").value,
        uf: document.getElementById("estado").value
    };

    try {
        const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};
        const response = await fetch(`http://localhost:8080/usuarios/${usuario.id}/endereco`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(endereco)
        });

        if (response.ok) {
            const result = await response.json();
            const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};
            usuario.endereco = result;
            sessionStorage.setItem('usuario', JSON.stringify(usuario));

            Swal.fire({
                icon: 'success',
                title: 'Endereço atualizado!',
                text: 'Seu endereço foi atualizado com sucesso.'
            });
        } else {
            throw new Error('Erro ao atualizar o endereço');
        }
    } catch (error) {
        Swal.fire({
            icon: 'error',
            title: 'Erro',
            text: error.message
        });
    }
});
