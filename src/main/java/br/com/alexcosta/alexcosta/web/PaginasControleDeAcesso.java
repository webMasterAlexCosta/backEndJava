package br.com.alexcosta.alexcosta.web;

import br.com.alexcosta.alexcosta.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaginasControleDeAcesso {

    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


//        @RequestMapping("/api")
//    public ModelAndView index() {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("redirect:/publico/index.html");
//        return modelAndView;
//    }

    @GetMapping(value = "/api/codigocadastro/verificarcadastro/{uuid}")
    public String verificarCadastro(@PathVariable("uuid") String uuid, Model model) {
        String mensagem = userService.verificarCadastro(uuid);

        // Adiciona a mensagem ao modelo
        model.addAttribute("mensagem", mensagem);

        try {

            if (mensagem.contains("Usuário verificado")) {
                return "sucesso";// Nome do arquivo HTML de sucesso
            } else if (mensagem.contains("Usuário já cadastrado")) {
                return "cadastrado";
            } else if (mensagem.contains("Tempo de verificação expirado. Um novo link foi enviado para o seu e-mail.")) {
                return "expirado";
            } else {
                return "erro";
            }
        } catch (Exception e) {
            // Loga a exceção para facilitar o diagnóstico
            logger.error("Erro ao verificar cadastro para o uuid: " + uuid, e);
            model.addAttribute("erro", "Ocorreu um erro ao verificar o cadastro. Tente novamente.");
            return "erro";
        }
    }


    // @PreAuthorize("hasAuthority('ADMIN')")
    public String administrativo(){
        return "/adminIndex.html";
    }

    @GetMapping("/loginAdmin")
    public String login(){
        return "loginAdmin.html";
    }
}
