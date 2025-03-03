document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('cep').addEventListener('input', function () {
        let cep = this.value.replace(/\D/g, '');
        if (cep.length > 5) {
            cep = cep.substring(0, 5) + '-' + cep.substring(5);
        }
        this.value = cep;
    });

    document.getElementById('telefone').addEventListener('input', function () {
        let telefone = this.value.replace(/\D/g, '');
        let formatado = '';

        if (telefone.length > 0) {
            formatado += '(' + telefone.substring(0, 2) + ') ';
        }
        if (telefone.length > 2) {
            formatado += telefone.substring(2, 7);
        }
        if (telefone.length >= 7) {
            formatado += '-' + telefone.substring(7, 11);
        }

        this.value = formatado;
    });

    document.getElementById('cpf').addEventListener('input', function () {
        let cpf = this.value.replace(/\D/g, '');
        let formatado = '';

        if (cpf.length > 0) {
            formatado += cpf.substring(0, 3);
        }
        if (cpf.length > 3) {
            formatado += '.' + cpf.substring(3, 6);
        }
        if (cpf.length > 6) {
            formatado += '.' + cpf.substring(6, 9);
        }
        if (cpf.length >= 9) {
            formatado += '-' + cpf.substring(9, 11);
        }

        this.value = formatado;
    });

    const campos = ['cep', 'telefone', 'cpf', 'name', 'email', 'password', 'password2', 'dob', 'logradouro', 'numero', 'complemento', 'bairro', 'cidade', 'estado'];

    campos.forEach(id => {
        const campo = document.getElementById(id);
        if (campo) {
            campo.addEventListener('input', function () {
                if (validarCampo(this)) {
                    this.classList.add('validado');
                } else {
                    this.classList.remove('validado');
                }
            });

            campo.addEventListener('focus', function () {
                this.classList.add('focado');
            });

            campo.addEventListener('blur', function () {
                this.classList.remove('focado');
                if (validarCampo(this)) {
                    this.classList.add('validado');
                }
            });
        }
    });

    function validarCampo(campo) {
        const id = campo.id;
        let valor = campo.value.trim();
        let valido = false;

        switch (id) {
            case 'cep':
                valido = validateCEP(valor);
                break;
            case 'telefone':
                valido = /^\(\d{2}\) \d{5}-\d{4}$/.test(valor);
                break;
            case 'cpf':
                valido = validateCPF(valor);
                break;
            case 'email':
                valido = validateEmail(valor);
                break;
            case 'password':
                const password = document.getElementById('password').value;
                const password2 = document.getElementById('password2').value;
               valido = password === password2 && /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[#$@!%*?&])[A-Za-z\d#$@!%*?&]{8,}$/.test(password);

                break;
            case 'password2':
                const passwordConfirm = document.getElementById('password').value;
                const passwordConfirm2 = valor;
                valido = passwordConfirm === passwordConfirm2;
                break;
            case 'name':
                valido = valor.length >= 3;
                break;
            case 'dob':
                valido = valor !== '';
                break;
            case 'estado':
                valido = /^[A-Z]{2}$/.test(valor);
                break;
            default:
                valido = valor !== '';
        }

        return valido;
    }

    document.getElementById('cadastroForm').addEventListener('submit', function (event) {
        event.preventDefault();

        if (!validarFormulario()) {
            return;
        }

        Swal.fire({
            title: 'Confirmação',
            text: "Você está prestes a realizar o cadastro. Deseja continuar?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sim, realizar cadastro',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                const dadosUsuario = criarJSONFormulario();

                fetch('/usuarios/cadastro', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: dadosUsuario
                })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(data => {
                            mostrarErros(data.errors);
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    Swal.fire({
                        title: 'Cadastro realizado!',
                        text: 'Cadastro realizado com sucesso!',
                        icon: 'success',
                    }).then(() => {
                        window.location.href = './login.html';
                    });
                })
                .catch(error => {
                    console.error('Erro ao enviar dados:', error);
                    Swal.fire('Erro!', 'Ocorreu um erro ao realizar o cadastro. Tente novamente mais tarde.', 'error');
                });
            }
        });
    });


    function mostrarErros(errors) {
        const campos = ['name', 'email', 'password', 'password2', 'dob', 'cpf', 'telefone', 'cep', 'estado'];


        campos.forEach(id => {
            const campo = document.getElementById(id);
            if (campo) {
                campo.classList.remove('input-erro');
            }
        });


        for (const [key, message] of Object.entries(errors)) {
            const campo = document.getElementById(key);
            if (campo) {
                campo.classList.add('input-erro');
                Swal.fire('Erro!', message, 'error');
            }
        }
    }

    document.getElementById('aceitarPoliticas').addEventListener('change', function () {
        const politicasDiv = document.getElementById('politicas');
        if (this.checked) {
            politicasDiv.style.display = 'block';
        } else {
            politicasDiv.style.display = 'none';
        }
    });

    const usuario = localStorage.getItem('usuario');
    if (usuario) {
        const dadosUsuario = JSON.parse(usuario);

        document.getElementById('name').value = dadosUsuario.name || '';
        document.getElementById('email').value = dadosUsuario.email || '';
        document.getElementById('password').value = dadosUsuario.senha || '';
        document.getElementById('dob').value = dadosUsuario.dataNascimento || '';
        document.getElementById('cpf').value = dadosUsuario.cpf || '';
        document.getElementById('telefone').value = dadosUsuario.telefone || '';
        document.getElementById('cep').value = dadosUsuario.endereco?.cep || '';
        document.getElementById('logradouro').value = dadosUsuario.endereco?.logradouro || '';
        document.getElementById('numero').value = dadosUsuario.endereco?.numero || '';
        document.getElementById('complemento').value = dadosUsuario.endereco?.complemento || '';
        document.getElementById('bairro').value = dadosUsuario.endereco?.bairro || '';
        document.getElementById('cidade').value = dadosUsuario.endereco?.cidade || '';
        document.getElementById('estado').value = dadosUsuario.endereco?.estado || '';
    }

    document.getElementById('cep').addEventListener('blur', function () {
        const cep = this.value.replace(/\D/g, '');

        if (cep.length === 8) {
            fetch(`https://viacep.com.br/ws/${cep}/json/`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Não foi possível buscar os dados do CEP.');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.erro) {
                        Swal.fire('Atenção!', 'CEP não encontrado.', 'warning');
                        return;
                    }

                    document.getElementById('logradouro').value = data.logradouro || '';
                    document.getElementById('bairro').value = data.bairro || '';
                    document.getElementById('cidade').value = data.localidade || '';
                    document.getElementById('estado').value = data.uf || '';
                    document.getElementById('complemento').value = data.complemento || '';
                })
                .catch(error => {
                    console.error('Erro ao buscar o endereço:', error);
                        Swal.fire('Erro!', error.message, 'error');

                });
        } else {
            Swal.fire('Atenção!', 'Por favor, insira um CEP válido com 8 dígitos.', 'warning');
        }
    });
});

function validarFormulario() {
    const form = document.getElementById('cadastroForm');
    const name = form['name'];
    const email = form['email'];
    const password = form['password'];
    const password2 = form['password2'];
    const dob = form['dob'];
    const cpf = form['cpf'];
    const telefone = form['telefone'];
    const cep = form['cep'];
    const estado = form['estado'];

    const validacoes = [
        name && name.value.trim().length >= 3,
        email && validateEmail(email.value),
        password && password.value.length >= 8,
        password2 && password2.value === password.value,
        dob && dob.value !== '',
        cpf && validateCPF(cpf.value),
        telefone && validateTelefone(telefone.value),
        cep && cep.value.trim().length === 9,
        estado && /^[A-Z]{2}$/.test(estado.value)
    ];

    return validacoes.every(v => v);
}

function criarJSONFormulario() {
    const form = document.getElementById('cadastroForm');
    const formData = new FormData(form);
    const jsonData = {};

    formData.forEach((value, key) => {
        jsonData[key] = value;
    });

    return JSON.stringify(jsonData);
}


function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
}

function validateCPF(cpf) {

    return true;
}
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('cep').addEventListener('input', function () {
        let cep = this.value.replace(/\D/g, '');
        if (cep.length > 5) {
            cep = cep.substring(0, 5) + '-' + cep.substring(5);
        }
        this.value = cep;
    });

    document.getElementById('telefone').addEventListener('input', function () {
        let telefone = this.value.replace(/\D/g, '');
        let formatado = '';

        if (telefone.length > 0) {
            formatado += '(' + telefone.substring(0, 2) + ') ';
        }
        if (telefone.length > 2) {
            formatado += telefone.substring(2, 7);
        }
        if (telefone.length >= 7) {
            formatado += '-' + telefone.substring(7, 11);
        }

        this.value = formatado;
    });

    document.getElementById('cpf').addEventListener('input', function () {
        let cpf = this.value.replace(/\D/g, '');
        let formatado = '';

        if (cpf.length > 0) {
            formatado += cpf.substring(0, 3);
        }
        if (cpf.length > 3) {
            formatado += '.' + cpf.substring(3, 6);
        }
        if (cpf.length > 6) {
            formatado += '.' + cpf.substring(6, 9);
        }
        if (cpf.length >= 9) {
            formatado += '-' + cpf.substring(9, 11);
        }

        this.value = formatado;
    });

    const campos = ['cep', 'telefone', 'cpf', 'name', 'email', 'password', 'password2', 'dob', 'logradouro', 'numero', 'complemento', 'bairro', 'cidade', 'estado'];

    campos.forEach(id => {
        const campo = document.getElementById(id);
        if (campo) {
            campo.addEventListener('input', function () {
                if (validarCampo(this)) {
                    this.classList.add('validado');
                } else {
                    this.classList.remove('validado');
                }
            });

            campo.addEventListener('focus', function () {
                this.classList.add('focado');
            });

            campo.addEventListener('blur', function () {
                this.classList.remove('focado');
                if (validarCampo(this)) {
                    this.classList.add('validado');
                }
            });
        }
    });

    function validarCampo(campo) {
        const id = campo.id;
        let valor = campo.value.trim();
        let valido = false;

        switch (id) {
            case 'cep':
                valido = validateCEP(valor);
                break;
            case 'telefone':
                valido = /^\(\d{2}\) \d{5}-\d{4}$/.test(valor);
                break;
            case 'cpf':
                valido = validateCPF(valor);
                break;
            case 'email':
                valido = validateEmail(valor);
                break;
            case 'password':
                const password = document.getElementById('password').value;
                const password2 = document.getElementById('password2').value;
                valido = password === password2 && /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(password);
                break;
            case 'password2':
                const passwordConfirm = document.getElementById('password').value;
                const passwordConfirm2 = valor;
                valido = passwordConfirm === passwordConfirm2;
                break;
            case 'name':
                valido = valor.length >= 3;
                break;
            case 'dob':
                valido = valor !== '';
                break;
            case 'estado':
                valido = /^[A-Z]{2}$/.test(valor);
                break;
            default:
                valido = valor !== '';
        }

        return valido;
    }

    document.getElementById('cadastroForm').addEventListener('submit', function (event) {
        event.preventDefault();

      if (!validarFormulario()) {
          return;
      }

      Swal.fire({
          title: 'Confirmação',
          text: "Você está prestes a realizar o cadastro. Deseja continuar?",
          icon: 'warning',
          showCancelButton: true,
          confirmButtonText: 'Sim, realizar cadastro',
          cancelButtonText: 'Cancelar'
      }).then((result) => {
          if (result.isConfirmed) {
              const dadosUsuario = criarJSONFormulario();

              fetch('/usuarios/cadastro', {
                  method: 'POST',
                  headers: {
                      'Content-Type': 'application/json'
                  },
                  body: dadosUsuario
              })
             .then(response => {
                 if (!response.ok) {
                     return response.json().then(errData => {
                         throw new Error(errData.message || 'Erro ao enviar dados para o servidor.');
                     });
                 }
                 return response.json();
             })

              .then(data => {
                  Swal.fire({
                      title: 'Cadastro realizado!',
                      text: 'Instruções de acesso foram enviadas em seu e-mail!',
                      icon: 'success',
                      showCancelButton: true,
                      confirmButtonText: 'Fazer Login',
                      cancelButtonText: 'Contate-nos'
                  }).then((result) => {
                      if (result.isConfirmed) {
                          window.location.href = './login.html';
                      } else {
                          Swal.fire({
                              title: 'Contate-nos',
                              html: `
                                  <p>Você pode entrar em contato conosco através das seguintes redes sociais:</p>
                                  <div style="display: flex; gap: 10px; justify-content: center;">
                                      <a href="https://facebook.com" target="_blank" class="social-link">
                                          <img src="https://upload.wikimedia.org/wikipedia/commons/5/51/Facebook_f_logo_%282019%29.svg" alt="Facebook" style="width: 40px; height: 40px;">
                                      </a>
                                      <a href="https://www.instagram.com/vpc.sport?igsh=YzljYTk1ODg3Zg==" target="_blank" class="social-link">
                                          <img src="https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png" alt="Instagram" style="width: 40px; height: 40px;">
                                      </a>
                                      <a href="https://wa.me/your-number" target="_blank" class="social-link">
                                          <img src="https://upload.wikimedia.org/wikipedia/commons/6/6b/WhatsApp.svg" alt="WhatsApp" style="width: 40px; height: 40px;">
                                      </a>
                                  </div>
                              `,
                              icon: 'info',
                              showConfirmButton: false
                          }).then(() => {

                              document.querySelectorAll('.social-link').forEach(link => {
                                  link.addEventListener('click', () => {
                                      setTimeout(() => {
                                          window.location.href = 'index.html';
                                      }, 5000);
                                  });
                              });
                          });


                          document.getElementById('cadastroForm').reset();
                          document.getElementById('aceitarPoliticas').checked = false;
                      }
                  });
              })
              .catch(error => {
                  console.error('Erro ao enviar dados:', error);
                  Swal.fire('Erro!', error.message, 'error');

              });
          }
      });

           });

    document.getElementById('aceitarPoliticas').addEventListener('change', function () {
        const politicasDiv = document.getElementById('politicas');
        if (this.checked) {
            politicasDiv.style.display = 'block';
        } else {
            politicasDiv.style.display = 'none';
        }
    });

    const usuario = localStorage.getItem('usuario');
    if (usuario) {
        const dadosUsuario = JSON.parse(usuario);

        document.getElementById('name').value = dadosUsuario.name || '';
        document.getElementById('email').value = dadosUsuario.email || '';
        document.getElementById('password').value = dadosUsuario.senha || '';
        document.getElementById('dob').value = dadosUsuario.dataNascimento || '';
        document.getElementById('cpf').value = dadosUsuario.cpf || '';
        document.getElementById('telefone').value = dadosUsuario.telefone || '';
        document.getElementById('cep').value = dadosUsuario.endereco?.cep || '';
        document.getElementById('logradouro').value = dadosUsuario.endereco?.logradouro || '';
        document.getElementById('numero').value = dadosUsuario.endereco?.numero || '';
        document.getElementById('complemento').value = dadosUsuario.endereco?.complemento || '';
        document.getElementById('bairro').value = dadosUsuario.endereco?.bairro || '';
        document.getElementById('cidade').value = dadosUsuario.endereco?.cidade || '';
        document.getElementById('estado').value = dadosUsuario.endereco?.estado || '';
    }

    document.getElementById('cep').addEventListener('blur', function () {
        const cep = this.value.replace(/\D/g, '');

        if (cep.length === 8) {
            fetch(`https://viacep.com.br/ws/${cep}/json/`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Não foi possível buscar os dados do CEP.');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.erro) {
                        Swal.fire('Atenção!', 'CEP não encontrado.', 'warning');
                        return;
                    }

                    document.getElementById('logradouro').value = data.logradouro || '';
                    document.getElementById('bairro').value = data.bairro || '';
                    document.getElementById('cidade').value = data.localidade || '';
                    document.getElementById('estado').value = data.uf || '';
                    document.getElementById('complemento').value = data.complemento || '';
                })
                .catch(error => {
                    console.error('Erro ao buscar o endereço:', error);
                    Swal.fire('Erro!', 'Ocorreu um erro ao buscar o endereço. Tente novamente mais tarde.', 'error');
                });
        } else {
            Swal.fire('Atenção!', 'Por favor, insira um CEP válido com 8 dígitos.', 'warning');
        }
    });
});

function validarFormulario() {
    const form = document.getElementById('cadastroForm');
    const name = form['name'];
    const email = form['email'];
    const password = form['password'];
    const password2 = form['password2'];
    const dob = form['dob'];
    const cpf = form['cpf'];
    const telefone = form['telefone'];
    const cep = form['cep'];
    const estado = form['estado'];
    const aceitarPoliticas = form['aceitarPoliticas'];

    const inputs = form.querySelectorAll('input');
    inputs.forEach(input => {
        input.classList.remove('input-erro', 'validado', 'focado');
    });

    let isValid = true;

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[#@$!%*?&])[A-Za-z\d#$@!%*?&]{8,}$/;
    const telefoneRegex = /^\(\d{2}\) \d{5}-\d{4}$/;

    if (!aceitarPoliticas.checked) {
        Swal.fire('Atenção!', 'Você deve aceitar as políticas de privacidade para continuar.', 'warning');
        isValid = false;
    }

    if (name.value.trim().length < 3) {
        name.classList.add('input-erro');
        Swal.fire('Erro!', 'Nome deve ter pelo menos 3 caracteres.', 'error');
        isValid = false;
    }

    if (!validateEmail(email.value.trim())) {
        email.classList.add('input-erro');
        Swal.fire('Erro!', 'E-mail inválido.', 'error');
        isValid = false;
    }

    if (!passwordRegex.test(password.value)) {
        password.classList.add('input-erro');
        Swal.fire('Erro!', 'Senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma letra minúscula, um número e um caractere especial.', 'error');
        isValid = false;
    }

    if (password.value !== password2.value) {
        password.classList.add('input-erro');
        password2.classList.add('input-erro');
        Swal.fire('Erro!', 'Senhas não conferem.', 'error');
        isValid = false;
    }

    if (!validateCPF(cpf.value.trim())) {
        cpf.classList.add('input-erro');
        Swal.fire('Erro!', 'CPF inválido.', 'error');
        isValid = false;
    }

    if (!validateCEP(cep.value.trim())) {
        cep.classList.add('input-erro');
        Swal.fire('Erro!', 'CEP inválido.', 'error');
        isValid = false;
    }

    if (!telefoneRegex.test(telefone.value.trim())) {
        telefone.classList.add('input-erro');
        Swal.fire('Erro!', 'Telefone inválido. Use o formato (xx) xxxxx-xxxx.', 'error');
        isValid = false;
    }

    if (estado.value.trim().length !== 2) {
        estado.classList.add('input-erro');
        Swal.fire('Erro!', 'Sigla do estado deve ter exatamente 2 caracteres.', 'error');
        isValid = false;
    }

    return isValid;
}

function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function validateCPF(cpf) {
    cpf = cpf.replace(/[^\d]+/g, '');

    if (cpf.length !== 11) return false;

    let sum = 0;
    let remainder;

    for (let i = 1; i <= 9; i++) {
        sum += parseInt(cpf.substring(i - 1, i)) * (11 - i);
    }

    remainder = (sum * 10) % 11;

    if (remainder === 10 || remainder === 11) remainder = 0;
    if (remainder !== parseInt(cpf.substring(9, 10))) return false;

    sum = 0;

    for (let i = 1; i <= 10; i++) {
        sum += parseInt(cpf.substring(i - 1, i)) * (12 - i);
    }

    remainder = (sum * 10) % 11;

    if (remainder === 10 || remainder === 11) remainder = 0;
    if (remainder !== parseInt(cpf.substring(10, 11))) return false;

    return true;
}

function validateCEP(cep) {
    return /^[0-9]{8}$/.test(cep.replace('-', ''));
}

function criarJSONFormulario() {
    const form = document.getElementById('cadastroForm');
    const nome = form['name'].value.trim();
    const email = form['email'].value.trim();
    const telefone = form['telefone'].value.replace(/\D/g, '');
    const dataNascimento = form['dob'].value;
    const senha = form['password'].value;
    const cpf = form['cpf'].value.replace(/\D/g, '');
    const logradouro = form['logradouro'].value.trim();
    const numero = form['numero'].value.trim();
    const complemento = form['complemento'].value.trim();
    const bairro = form['bairro'].value.trim();
    const cidade = form['cidade'].value.trim();
    const estado = form['estado'].value.trim();

    return JSON.stringify({
        nome: nome,
        email: email,
        telefone: telefone,
        dataNascimento: dataNascimento,
        senha: senha,
        cpf: cpf,
        endereco: {
            logradouro: logradouro,
            cep: form['cep'].value.replace('-', ''),
            numero: numero ? parseInt(numero, 10) : null,
            cidade: cidade,
            bairro: bairro,
            complemento: complemento,
            uf: estado.toUpperCase()
        }
    });
}

function validateCEP(cep) {
    return /^\d{5}-\d{3}$/.test(cep);
}

function validateTelefone(telefone) {
    return /^\(\d{2}\) \d{5}-\d{4}$/.test(telefone);
}
