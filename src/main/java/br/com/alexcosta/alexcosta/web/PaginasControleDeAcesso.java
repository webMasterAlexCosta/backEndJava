package br.com.alexcosta.alexcosta.web;

import br.com.alexcosta.alexcosta.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PaginasControleDeAcesso {

    @Autowired
    private UserService userService;

//    @RequestMapping("/")
//    public ModelAndView index() {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("redirect:/publico/index.html");
//        return modelAndView;
//    }

    @GetMapping(value = "/codigocadastro/verificarcadastro/{uuid}")
    public String verificarCadastro(@PathVariable("uuid") String uuid, Model model) {
        String mensagem = userService.verificarCadastro(uuid);

        // Adiciona a mensagem ao modelo
        model.addAttribute("mensagem", mensagem);
        try{
        // Retorna a página correta com base na mensagem
        if (mensagem.contains("Usuário verificado")) {
            return "sucesso"; // Nome do arquivo HTML de sucesso
        } else if(mensagem.contains("Usuário já cadastrado")) {
            return "cadastrado"; // Nome do arquivo HTML para usuário cadastrado
        }else{
            return "expirado";
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
