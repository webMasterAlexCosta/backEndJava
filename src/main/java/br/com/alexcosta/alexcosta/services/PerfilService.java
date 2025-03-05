package br.com.alexcosta.alexcosta.services;

import br.com.alexcosta.alexcosta.entities.Perfil;
import br.com.alexcosta.alexcosta.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerfilService {
    @Autowired
    private PerfilRepository perfilRepository;

    public List<Perfil> listarTodas() {
        return perfilRepository.findAll();
    }

}
