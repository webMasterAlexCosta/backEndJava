
const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};
console.log(usuario);

if (usuario.perfil[0] === "CLIENT") {
    const icone = document.querySelector(".iconeLogin");

    if (icone) {
      icone.setAttribute("href", "/usuario/perfil.html");
    } else {
    }
  }
