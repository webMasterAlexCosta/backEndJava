package br.com.alexcosta.alexcosta.controllers;

import br.com.alexcosta.alexcosta.dto.PedidoDTO;
import br.com.alexcosta.alexcosta.dto.PedidoRequest;
import br.com.alexcosta.alexcosta.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/{id}")
    public ResponseEntity<PedidoDTO> findById(@PathVariable Integer id) {
        PedidoDTO dto = pedidoService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> insert(@Valid @RequestBody PedidoRequest pedidoRequest) {
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
    }

