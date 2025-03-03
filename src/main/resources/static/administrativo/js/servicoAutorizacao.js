export async function obterToken() {
    const token = sessionStorage.getItem('JWT_TOKEN');

    if (!token) {
        console.error('Token não encontrado');
        await Swal.fire({
            title: 'Erro!',
            text: 'Você precisa estar logado para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        }).then(() => {
            window.location.href = '../loginAdmin.html';
        });
        return null;
    }

    const usuario = JSON.parse(sessionStorage.getItem('usuario')) || {};

    if (usuario.perfil[0] !== "ADMIN") {
        console.error('Perfil inválido');
        await Swal.fire({
            title: 'Erro!',
            text: 'Você precisa ter um perfil de administrador para acessar esta página.',
            icon: 'error',
            confirmButtonText: 'Ir para Login',
        }).then(() => {
            window.location.href = '../loginAdmin.html';
        });
        return null;
    }

    return token;
}
