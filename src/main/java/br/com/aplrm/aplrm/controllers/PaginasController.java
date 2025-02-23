package br.com.aplrm.aplrm.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.print.attribute.standard.PresentationDirection;

@Controller
public class PaginasController {

   // @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/administrativo")
    public String administrativo(){
        return "/adminIndex.html";
    }

    @GetMapping("/loginAdmin")
    public String login(){
        return "loginAdmin.html";
    }
}
