package br.com.alexcosta.alexcosta.controllers;

import br.com.alexcosta.alexcosta.dto.PedidoDTO;
import br.com.alexcosta.alexcosta.dto.PedidoRequest;
import br.com.alexcosta.alexcosta.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> findById(@PathVariable Integer id) {
        PedidoDTO dto = pedidoService.findById(id);
        return ResponseEntity.ok(dto);
    }

//    @PostMapping(value="salvar")
//    public ResponseEntity<PedidoDTO> insert(@Valid @RequestBody PedidoRequest pedidoRequest) {
//        PedidoDTO dto = pedidoService.insert(pedidoRequest);
//        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
//        return ResponseEntity.created(uri).body(dto);
//    }

    @PostMapping(value = "salvar")
    public ResponseEntity<PedidoDTO> insert(
            @Valid @RequestBody PedidoRequest pedidoRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
         //   log.warn("Usuário autenticado é nulo!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        //log.info("Usuário autenticado no controller: {}", userDetails.getUsername());

        String emailUsuario = userDetails.getUsername();
        PedidoDTO dto = pedidoService.insert(pedidoRequest);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping(value="/usuario")
    public ResponseEntity<List<PedidoDTO>> findAll() {
        List<PedidoDTO> pedidos = pedidoService.meusPedidos();
        System.out.println(pedidos);
        return ResponseEntity.ok(pedidos);
    }

//    @PostMapping(value="/finalizar")
//    public ResponseEntity<PedidoDTO> insert2(@Valid @RequestBody PedidoRequest pedidoRequest) {
//        PedidoDTO dto = pedidoService.insert(pedidoRequest, emailUsuario);
//        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
//        return ResponseEntity.created(uri).body(dto);
//    }
    }

