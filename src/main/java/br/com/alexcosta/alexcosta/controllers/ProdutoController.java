package br.com.alexcosta.alexcosta.controllers;

import br.com.alexcosta.alexcosta.dto.ProdutoDTO;

import br.com.alexcosta.alexcosta.services.ProdutoService;
import br.com.alexcosta.alexcosta.services.exceptions.DataBaseException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;


    @GetMapping("/procurarCategoria")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<ProdutoDTO>> getProdutos(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "categoriaId", required = false) Integer categoriaId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> dto = service.buscarProdutos(categoriaId, pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/buscar")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ProdutoDTO>> buscarProdutos(@RequestParam(required = false) String nome) {
        List<ProdutoDTO> produtos = service.buscarProdutosString(nome);
        return ResponseEntity.ok(produtos);
    }


    @GetMapping("/{id}")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProdutoDTO> findById(@PathVariable Integer id) {
        ProdutoDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/paginas")
   // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<ProdutoDTO>> findAll(Pageable pageable) {
        Page<ProdutoDTO> dto = service.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/lista")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ProdutoDTO>> getAllProdutos() {
        List<ProdutoDTO> produtos = service.ListfindAll();
        return ResponseEntity.ok(produtos);
    }



    @PostMapping
   // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProdutoDTO> insert(@Valid @RequestBody ProdutoDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{id}/atualizar")
   // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProdutoDTO> update(@PathVariable Integer id, @Valid @RequestBody ProdutoDTO dto) {
        ProdutoDTO dto2 = service.update(id, dto);
        return ResponseEntity.ok(dto2);
    }

    @DeleteMapping("/{id}/deletar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            ProdutoDTO produto = service.findById(id);
            service.delete(id);
            return ResponseEntity.ok(produto);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Falha na Integridade Referencial, o produto está associado a um outro pedido");
        }
    }

    @PatchMapping("/{id}/nome")
   // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProdutoDTO> alterarNome(@PathVariable Integer id, @Valid @RequestBody ProdutoDTO dto) {
        dto = service.alterarNome(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/preco")
   // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProdutoDTO> alterarPreco(@PathVariable Integer id, @Valid @RequestBody ProdutoDTO dto) {
        dto = service.alterarPreco(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/descricao")
   // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProdutoDTO> alterarDescricao(@PathVariable Integer id, @Valid @RequestBody ProdutoDTO dto) {
        dto = service.alterarDescricao(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/img")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProdutoDTO> alterarImagem(@PathVariable Integer id, @Valid @RequestBody ProdutoDTO dto) {
        dto = service.alterarImagem(id, dto);
        return ResponseEntity.ok(dto);
    }



    @PostMapping("/{produtoId}/categorias/{categoriaId}")
 //   @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> associateCategoria(@PathVariable Integer produtoId, @PathVariable Integer categoriaId) {
        service.associateCategoria(produtoId, categoriaId);
        return ResponseEntity.ok().build();
    }

//
@DeleteMapping("/{produtoId}/categorias/{categoriaId}")
public ResponseEntity<Void> removerCategoria(@PathVariable Integer produtoId, @PathVariable Integer categoriaId) {
    service.removerAssociacaoCategoria(produtoId, categoriaId);
    return ResponseEntity.noContent().build();
}
    @GetMapping("/filtro/listaPorCategoriaEPreco")
    public ResponseEntity<List<ProdutoDTO>> getAllProdutos(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(value = "categoriaId", required = false) Integer categoriaId) {

        List<ProdutoDTO> produtos = service.findAllByCategoriaAndPreco(categoriaId, minPrice, maxPrice);
        return ResponseEntity.ok(produtos);
    }


}
