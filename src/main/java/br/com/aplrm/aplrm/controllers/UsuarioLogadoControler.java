package br.com.aplrm.aplrm.controllers;


import br.com.aplrm.aplrm.dto.UserPerfilDTO;
import br.com.aplrm.aplrm.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UsuarioLogadoControler {

    @Autowired
    private UserService userService;

   // @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping("/me")
    public ResponseEntity<UserPerfilDTO> getMe() {
        UserPerfilDTO dto = userService.getMe();
        return ResponseEntity.ok(dto);
    }
}