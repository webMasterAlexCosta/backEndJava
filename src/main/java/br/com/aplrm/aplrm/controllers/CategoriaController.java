package br.com.aplrm.aplrm.controllers;


import br.com.aplrm.aplrm.entities.Categoria;
import br.com.aplrm.aplrm.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Categoria> getCategorias() {
        return categoriaService.listarTodas();
    }
}
