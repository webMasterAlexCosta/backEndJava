async function carregarDados() {
    const token = sessionStorage.getItem('JWT_TOKEN');
    if (!token) {
        console.error('Token não encontrado');
        Swal.fire({
            title: 'Erro !',
            text: 'Você precisa estar logado para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        }).then(() => {
            window.location.href = '../publico/login.html';
        });
        ocultarDados();
        return;
    }

    const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};

    if (usuario.perfil[0] !== "CLIENT") {
        console.error('Acesso negado: perfil não é CLIENT');
        Swal.fire({
            title: 'Erro !',
            text: 'Você não tem permissão para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        }).then(() => {
            window.location.href = '../publico/login.html';
        });
        ocultarDados();
        return;
    }

    mostrarDados();

}


function ocultarDados() {
    const elementosParaOcultar = document.querySelectorAll('.dados');
    elementosParaOcultar.forEach(tela =>tela.style.display = 'none');
}

function mostrarDados() {
    const elementosParaMostrar = document.querySelectorAll('.dados');
    elementosParaMostrar.forEach(tela => tela.style.display = 'block');
}

document.addEventListener('DOMContentLoaded', () => {
    carregarDados();
    atualizarUI();
    let dadosPedido=[];
    displayUsuarioInfo();

    const paymentForm = document.getElementById('payment-form');

    paymentForm.addEventListener('submit', async (event) => {
        event.preventDefault();


        const cardNumber = document.getElementById('card-number').value.replace(/\s/g, '');
        const cardName = document.getElementById('card-name').value;
        const expiryMonth = document.getElementById('expiry-month').value;
        const expiryYear = document.getElementById('expiry-year').value;
        const cvv = document.getElementById('cvv').value;

        if (!/^\d{16}$/.test(cardNumber)) {
            Swal.fire('Número do cartão inválido. Deve conter 16 dígitos.', '', 'error');
            return;
        }

        if (cardName.trim() === '') {
            Swal.fire('Nome do titular é obrigatório.', '', 'error');
            return;
        }

        if (!/^(0[1-9]|1[0-2])\/?([0-9]{4}|[0-9]{2})$/.test(expiryMonth + '/' + expiryYear)) {
            Swal.fire('Data de validade inválida.', '', 'error');
            return;
        }

        if (!/^\d{3}$/.test(cvv)) {
            Swal.fire('Código de segurança inválido. Deve conter 3 dígitos.', '', 'error');
            return;
        }


        const opcaoFrete = document.getElementById('opcao-frete')?.value;
        const valorFrete = parseFloat(document.getElementById('frete').textContent.replace('R$ ', '')) || 0;

        if (!opcaoFrete || valorFrete <= 0) {
            Swal.fire({
                icon: 'error',
                title: 'Erro !',
                text: 'Por favor, selecione uma opção de frete antes de concluir o pagamento.',
            });
            return;
        }

        const total = calcularTotal();
        const cart = JSON.parse(localStorage.getItem('cart')) || [];
        const items = cart.map(item => ({
            produto: item.id,
            quantidade: item.quantidade,
            tamanho: converterTamanhoParaNumero(item.tamanho)
        }));

        const token = sessionStorage.getItem('JWT_TOKEN');
        const usuario = obterUsuario();

        const dataToSend = {
            items: items,
            frete: {
                tipo: opcaoFrete,
                valor: valorFrete
            },
            endereco: usuario.endereco
        };



        try {
            const response = await fetch('/pedidos', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(dataToSend),
            });

            if (!response.ok) {
                throw new Error('Erro: A resposta do servidor não foi bem-sucedida.');
            }

            const responseData = await response.json();

            await new Promise(resolve => setTimeout(resolve, 2000));
                        dadosPedido= responseData;

            await gerarPDF(dataToSend);

            Swal.fire({
                icon: 'success',
                title: 'Pedido realizado com sucesso!',
                text: 'Você será redirecionado para a página inicial.',
            }).then(() => {
               window.location.href = '/';
            });

        } catch (error) {
            console.error('Erro ao processar pedido:', error);
            Swal.fire({
                icon: 'error',
                title: 'Erro !',
                text: `Erro ao processar pedido: ${error.message}`,
            });
        }
    });

    document.getElementById('calcular-frete').addEventListener('click', async () => {
        const cepDestino = document.getElementById('cep-frete').value.replace("-", "").replace(".", "");

        if (cepDestino.length !== 8) {
            Swal.fire({
                icon: 'error',
                title: 'Erro !',
                text: 'CEP inválido.',
            });
            return;
        }

        try {
            const responseCep = await fetch(`https://viacep.com.br/ws/${cepDestino}/json/`);
            const endereco = await responseCep.json();

            if (endereco.erro) {
                Swal.fire({
                    icon: 'error',
                    title: 'Erro !',
                    text: 'CEP não encontrado.',
                });
                return;
            }

            Swal.fire({
                title: 'Atualizar Endereço',
                html: `
                    <label for="numero">Número:</label><br>
                    <input id="numero" class="swal2-input" placeholder="Número" value="${endereco.numero || ''}">
                    <label for="complemento">Complemento:</label><br>
                    <input id="complemento" class="swal2-input" placeholder="Complemento" value="${endereco.complemento || ''}">
                `,
                focusConfirm: false,
                showCancelButton: true,
                confirmButtonText: 'Salvar',
                cancelButtonText: 'Cancelar',
                preConfirm: () => {
                    const numero = Swal.getPopup().querySelector('#numero').value;
                    const complemento = Swal.getPopup().querySelector('#complemento').value;

                    endereco.numero = numero;
                    endereco.complemento = complemento;

                    atualizarEnderecoNaPagina(endereco);

                    Swal.fire({
                        icon: 'success',
                        title: 'Endereço Atualizado!',
                        html: `
                            <p><strong>CEP:</strong> ${endereco.cep}</p>
                            <p><strong>Logradouro:</strong> ${endereco.logradouro}</p>
                            <p><strong>Número:</strong> ${endereco.numero || 'N/A'}</p>
                            <p><strong>Complemento:</strong> ${endereco.complemento || 'N/A'}</p>
                            <p><strong>Bairro:</strong> ${endereco.bairro || 'N/A'}</p>
                            <p><strong>Cidade:</strong> ${endereco.localidade || 'N/A'}</p>
                            <p><strong>UF:</strong> ${endereco.uf || 'N/A'}</p>
                        `
                    });

                    mostrarEscolhaDeFrete(endereco);
                }
            });

        } catch (error) {
            console.error('Erro ao calcular frete:', error);
            Swal.fire({
                icon: 'error',
                title: 'Erro !',
                text: 'Erro ao calcular frete.',
            });
        }
    });

    function mostrarEscolhaDeFrete(endereco) {
        Swal.fire({
            title: 'Escolha o Tipo de Frete',
            html: `
                <select id="opcao-frete" class="swal2-select" style="width: auto;">
                <option>SELECIONE</option>
                    <option value="sedex">SEDEX</option>
                    <option value="pac">PAC</option>
                    <option value="retirar">Retirar na Loja</option>
                </select>
                <p id="valor-frete" style="margin-top: 10px;">Valor do Frete: R$ 0,00</p>
            `,
            focusConfirm: false,
            showCancelButton: true,
            confirmButtonText: 'Calcular',
            cancelButtonText: 'Cancelar',
            didOpen: () => {
                const selectFrete = Swal.getPopup().querySelector('#opcao-frete');
                const valorFreteDisplay = Swal.getPopup().querySelector('#valor-frete');

                selectFrete.addEventListener('change', async () => {
                    const opcaoSelecionada = selectFrete.value;
                    const quantidade = calcularQuantidadeItens();

                    try {
                        const response = await fetch(`/frete?estadoDestino=${endereco.uf}&quantidade=${quantidade}`);
                        const dadosFrete = await response.json();

                        let valorFrete = 0;
                        if (opcaoSelecionada === 'sedex') {
                            valorFrete = dadosFrete.freteSedex;
                        } else if (opcaoSelecionada === 'pac') {
                            valorFrete = dadosFrete.fretePac;
                        } else if (opcaoSelecionada === 'retirar') {
                            valorFrete = 0.1; // Frete grátis
                        }

                        valorFreteDisplay.textContent = `Valor do Frete: R$ ${valorFrete.toFixed(2)}`;
                    } catch (error) {
                        console.error('Erro ao calcular frete:', error);
                        Swal.fire({
                            icon: 'error',
                            title: 'Erro !',
                            text: 'Erro ao calcular o valor do frete.',
                        });
                    }
                });
            },
            preConfirm: () => {
                const opcaoSelecionada = Swal.getPopup().querySelector('#opcao-frete').value;
                const valorFrete = parseFloat(Swal.getPopup().querySelector('#valor-frete').textContent.replace('Valor do Frete: R$ ', '')) || 0;

                document.getElementById('opcao-frete').value = opcaoSelecionada;
                atualizarTotal(valorFrete);

                Swal.fire({
                    icon: 'success',
                    title: 'Frete Selecionado!',
                    text: `Você escolheu a opção de frete: ${opcaoSelecionada}. O valor do frete é R$ ${valorFrete.toFixed(2)}.`,
                    html: `
                        <p><strong>CEP:</strong> ${endereco.cep}</p>
                        <p><strong>Logradouro:</strong> ${endereco.logradouro}</p>
                        <p><strong>Número:</strong> ${endereco.numero || 'N/A'}</p>
                        <p><strong>Complemento:</strong> ${endereco.complemento || 'N/A'}</p>
                        <p><strong>Bairro:</strong> ${endereco.bairro || 'N/A'}</p>
                        <p><strong>Cidade:</strong> ${endereco.localidade || 'N/A'}</p>
                        <p><strong>UF:</strong> ${endereco.uf || 'N/A'}</p>
                    `
                });
            }
        });
    }
const pdfAlex = {
    items: cart.map(item => ({
        produto: item.nome,
        quantidade: item.quantidade,
        tamanho: converterTamanhoParaNumero(item.tamanho),
        preco: item.preco
    })),
    frete: {
        tipo: opcaoFrete,
        valor: valorFrete
    },
    endereco: {
        cep: usuario.endereco.cep,
        logradouro: usuario.endereco.logradouro,
        numero: usuario.endereco.numero,
        complemento: usuario.endereco.complemento,
        bairro: usuario.endereco.bairro,
        cidade: usuario.endereco.cidade,
        uf: usuario.endereco.uf
    }
};


async function gerarPDF(pdfAlex) {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();

    doc.setFont('Helvetica');

    doc.setFontSize(20);
    doc.setFont('Helvetica', 'bold');
    doc.text('Pedido - Loja Online', 14, 20);

    doc.setFontSize(12);
    doc.setFont('Helvetica', 'normal');
    doc.text('Data do Pedido:', 14, 30);
    doc.text(new Date().toLocaleDateString(), 60, 30);

    doc.setFontSize(12);
    doc.setFont('Helvetica', 'bold');
    doc.text('Informações do Cliente', 14, 40);

    doc.setFontSize(12);
    doc.setFont('Helvetica', 'normal');
    doc.text('Nome:', 14, 50);
    doc.text(String(obterUsuario().nome), 60, 50);

    doc.text('Endereço:', 14, 60);
    const endereco = `${pdfAlex.endereco.logradouro || 'Não especificado'}, ${pdfAlex.endereco.numero || 'Não especificado'} - ${pdfAlex.endereco.bairro || 'Não especificado'}, ${pdfAlex.endereco.cidade || 'Não especificado'} - ${pdfAlex.endereco.uf || 'Não especificado'}`;
    doc.text(endereco, 60, 60, { maxWidth: 140 });

    doc.text('CEP:', 14, 70);
    doc.text(String(pdfAlex.endereco.cep || 'Não especificado'), 60, 70);

    doc.text('Tipo de Frete:', 14, 80);
    doc.text(String(pdfAlex.frete.tipo || 'Não especificado'), 60, 80);

    doc.text('Valor do Frete:', 14, 90);
    doc.text(`R$ ${(pdfAlex.frete.valor || 0).toFixed(2)}`, 60, 90);

    doc.setDrawColor(0);
    doc.setLineWidth(0.5);
    doc.line(14, 100, 196, 100);

    doc.setFontSize(20);
    doc.setFont('Helvetica', 'bold');
    doc.text('Itens do Pedido', 14, 110);

    doc.setFontSize(12);
    const columns = ["Produto", "Quantidade", "Tamanho", "Preço"];
    const columnWidths = [90, 25, 25, 40];
    const startX = 14;
    const columnSpacing = 8;
    let yOffset = 120;

    doc.setFont('Helvetica', 'bold');
    columns.forEach((header, index) => {
        const positionX = startX + columnWidths.slice(0, index).reduce((acc, width) => acc + width, 0) + columnSpacing * index;
        doc.text(header, positionX, yOffset);
    });

    yOffset += 7;
    doc.line(startX, yOffset, startX + columnWidths.reduce((a, b) => a + b, 0) + columnSpacing * (columns.length - 1), yOffset);

    yOffset += 12;
    doc.setFont('Helvetica', 'normal');
    const cart = JSON.parse(localStorage.getItem('cart')) || [];

    for (const item of cart) {
        const produto = item.nome || 'Não especificado';
        const quantidade = item.quantidade || 0;
        const tamanho = item.tamanho || 'Não especificado';
        const preco = item.preco || 0;

        const produtoX = startX;
        const quantidadeX = produtoX + columnWidths[0] + columnSpacing + 10;
        const tamanhoX = quantidadeX + columnWidths[1] + columnSpacing;
        const precoX = tamanhoX + columnWidths[2] + columnSpacing + 6;

        doc.text(produto.substring(0, 40), produtoX, yOffset);
        doc.text(String(quantidade), quantidadeX, yOffset, { align: 'right' });
        doc.text(String(tamanho), tamanhoX, yOffset, { align: 'center' });
        doc.text(`R$ ${preco.toFixed(2)}`, precoX, yOffset, { align: 'right' });

        yOffset += 7;
    }

    doc.setDrawColor(0);
    doc.setLineWidth(0.5);
    yOffset += 5;
    doc.line(startX, yOffset, startX + columnWidths.reduce((a, b) => a + b, 0) + columnSpacing * (columns.length - 1), yOffset);

    doc.setFontSize(12);
    doc.setFont('Helvetica', 'bold');
    yOffset += 10;
    doc.text('Subtotal:', startX, yOffset);
    doc.text(`R$ ${(calcularSubtotal() || 0).toFixed(2)}`, startX + columnWidths.reduce((a, b) => a + b, 0) + columnSpacing * (columns.length - 1) - 160, yOffset, { align: 'right' });

    yOffset += 10;
    doc.text('Total:', startX, yOffset);
    doc.text(`R$ ${(calcularTotal() || 0).toFixed(2)}`, startX + columnWidths.reduce((a, b) => a + b, 0) + columnSpacing * (columns.length - 1) - 160, yOffset, { align: 'right' });


    const qrCodeData = JSON.stringify(`Pedido numero :${dadosPedido.numeroPedido}`);
    const qrCodeImage = await gerarQRCode(qrCodeData);


    doc.addImage(qrCodeImage, 'PNG', 150, 10, 40, 40);


    doc.save('pedido.pdf');
    localStorage.clear();

}

function gerarQRCode(data) {
    return new Promise((resolve, reject) => {
        QRCode.toDataURL(data, { errorCorrectionLevel: 'L' }, (err, url) => {
            if (err) {
                reject(err);
            } else {
                resolve(url);
            }
        });
    });
}

    function converterTamanhoParaNumero(tamanho) {
        const mapeamentoTamanhos = {
            "P": 1,
            "M": 2,
            "G": 3,
            "GG": 4
        };
        return mapeamentoTamanhos[tamanho] || tamanho;
    }

    function calcularQuantidadeItens() {
        const cart = JSON.parse(localStorage.getItem('cart')) || [];
        return cart.reduce((acc, item) => acc + item.quantidade, 0);
    }

    function atualizarUI() {
        const cart = JSON.parse(localStorage.getItem('cart')) || [];
        const checkoutItems = document.getElementById('checkout-items');

        checkoutItems.innerHTML = '';

        cart.forEach(item => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><img src="${item.imagem}" alt="${item.nome}" class="cart-img" width="100px" /></td>
                <td><h4>${item.nome}</h4></td>
                <span>Quantidade:</span><td>${item.quantidade}</td>
                <span>Tamanho:</span><td>${item.tamanho || 'N/A'}</td>
                <span>Subtotal:</span><td>R$ ${item.preco.toFixed(2)}</td>
                <span>Total:</span><td>R$ ${(item.preco * item.quantidade).toFixed(2)}</td>
            `;
            checkoutItems.appendChild(row);
        });

        atualizarTotal(0);
    }

    function atualizarTotal(valorFrete) {
        const subtotal = calcularSubtotal();
        const total = subtotal + valorFrete;

        document.getElementById('subtotal').textContent = `R$ ${subtotal.toFixed(2)}`;
        document.getElementById('frete').textContent = `R$ ${valorFrete.toFixed(2)}`;
        document.getElementById('total').textContent = `R$ ${total.toFixed(2)}`;
    }

    function calcularSubtotal() {
        const cart = JSON.parse(localStorage.getItem('cart')) || [];
        return cart.reduce((acc, item) => acc + (item.preco * item.quantidade), 0);
    }

    function calcularTotal() {
        const subtotal = calcularSubtotal();
        const frete = parseFloat(document.getElementById('frete').textContent.replace('R$ ', '')) || 0;
        return subtotal + frete;
    }

    function atualizarEnderecoNaPagina(endereco) {
        const userFields = ['nome', 'cep', 'logradouro', 'numero', 'complemento', 'bairro', 'cidade', 'uf'];
        userFields.forEach(field => {
            document.getElementById(field).textContent = endereco[field] || '';
        });
    }

    function displayUsuarioInfo() {
        const userFields = ['nome', 'cep', 'logradouro', 'numero', 'complemento', 'bairro', 'cidade', 'uf'];
        const usuario = obterUsuario();
        if (usuario && usuario.endereco) {
            userFields.forEach(field => {
                document.getElementById(field).textContent = usuario.endereco[field] || '';
            });
        } else {
            userFields.forEach(field => {
                document.getElementById(field).textContent = '';
            });
        }document.getElementById('nome').textContent = `${usuario.nome}`;
    }

    function obterUsuario() {
        return JSON.parse(sessionStorage.getItem('usuario')) || {};
    }
});