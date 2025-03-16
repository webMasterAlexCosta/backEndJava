package br.com.alexcosta.alexcosta.controllers;


import br.com.alexcosta.alexcosta.entities.Categoria;
import br.com.alexcosta.alexcosta.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Categoria> getCategorias() {
        return categoriaService.listarTodas();
    }
}
