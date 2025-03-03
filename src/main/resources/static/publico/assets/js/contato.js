
window.onload = function() {
    const mainElement = document.querySelector('main');
    mainElement.classList.add('loaded');
};


document.addEventListener('DOMContentLoaded', () => {
    const submenuIcon = document.querySelector('.nav-submenu-icon');
    const dropdownMenu = document.querySelector('.dropdown-menu');

    submenuIcon.addEventListener('click', (event) => {
        event.stopPropagation();
        dropdownMenu.classList.toggle('visible');
    });

    document.addEventListener('click', () => {
        dropdownMenu.classList.remove('visible');
    });

    const sendButton = document.getElementById('send-button');
    const userInput = document.getElementById('user-input');
    const chatBox = document.getElementById('chat-box');

    const sendMessage = async () => {
        const message = userInput.value;
        if (!message) return;

        chatBox.innerHTML += `<div class="user-message"><strong>Você:</strong> ${message}</div>`;
        userInput.value = '';

        try {
            const response = await fetch('https://api.openai.com/v1/chat/completions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer sk-proj-VAK2wUr3TVVF8wWPqLkAIdhbnSGA8qgzb5QxKKklStO85rTaZL4aLfc6ChYQzWWHuSLTnuI8T3BlbkFJ63npHgCna7JRaQtvpRJzuW_S9WpaEOglusSX70Ck7s_83yEVTVtIa_AZqJaBbGMWwRsrFcA`
                },
                body: JSON.stringify({
                    model: 'gpt-3.5-turbo',
                    messages: [{ role: 'user', content: message }]
                })
            });

            if (!response.ok) {
                const errorDetails = await response.text();
                throw new Error(`Erro na resposta da API: ${errorDetails}`);
            }

            const data = await response.json();
            const botMessage = data.choices[0].message.content;

            chatBox.innerHTML += `<div class="bot-message"><strong>Bot:</strong> ${botMessage}</div>`;
        } catch (error) {
            console.error(error);
            chatBox.innerHTML += `<div class="bot-message"><strong>Bot:</strong> Alex Expirou os creditos adicione mais</div>`;
        }
    };

    sendButton.addEventListener('click', sendMessage);

    userInput.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            sendMessage();
        }
    });
});
