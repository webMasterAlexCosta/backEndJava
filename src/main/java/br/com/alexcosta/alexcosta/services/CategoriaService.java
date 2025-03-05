package br.com.alexcosta.alexcosta.services;

import br.com.alexcosta.alexcosta.entities.Categoria;
import br.com.alexcosta.alexcosta.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }


}