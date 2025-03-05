package br.com.alexcosta.alexcosta.controllers;


import br.com.alexcosta.alexcosta.dto.UserPerfilDTO;
import br.com.alexcosta.alexcosta.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
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