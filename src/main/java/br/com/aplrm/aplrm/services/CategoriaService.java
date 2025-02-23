package br.com.aplrm.aplrm.services;

import br.com.aplrm.aplrm.entities.Categoria;
import br.com.aplrm.aplrm.repositories.CategoriaRepository;
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