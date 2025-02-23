package br.com.aplrm.aplrm.services;

import br.com.aplrm.aplrm.entities.Perfil;
import br.com.aplrm.aplrm.repositories.PerfilRepository;
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
