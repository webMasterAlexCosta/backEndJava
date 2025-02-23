package br.com.aplrm.aplrm.services;

import br.com.aplrm.aplrm.dto.ProdutoDTO;
import br.com.aplrm.aplrm.entities.Categoria;
import br.com.aplrm.aplrm.entities.Produto;
import br.com.aplrm.aplrm.repositories.CategoriaRepository;
import br.com.aplrm.aplrm.repositories.ProdutoRepository;
import br.com.aplrm.aplrm.services.exceptions.ResourceNotFoundExceptions;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    ProdutoRepository repository;



    @Autowired
    private CategoriaRepository categoriaRepository;


    @Transactional(readOnly = true)
    public ProdutoDTO findById(Integer id) {
//        Optional<Produto> resul = repository.findById(id);
//        if(resul.isPresent()){
//            Produto produto=resul.get();
//            return new ProdutoDTO(produto);
//        }throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        Produto produto = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundExceptions("Recurso nao encontrado"));
        return new ProdutoDTO(produto);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoDTO> findAll(Pageable page) {
        Page<Produto> resul = repository.findAll(page);
        return resul.map(x -> new ProdutoDTO(x));


    }

    public void removerAssociacaoCategoria(Integer produtoId, Integer categoriaId) {

        Produto produto = repository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
        produto.removeCategoria(categoria);
        repository.save(produto);
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> ListfindAll() {
        List<Produto> produtos = repository.findAll();
        return produtos.stream()
                .map(ProdutoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProdutoDTO insert(ProdutoDTO dto) {
        Produto produto = new Produto(dto);
        produto = repository.save(produto);
        return new ProdutoDTO(produto);
    }

    @Transactional
    public ProdutoDTO update(Integer id, ProdutoDTO produtoAtualizado) {
        try {
            Produto produtoExistente = repository.getReferenceById(id);
            produtoExistente.setNome(produtoAtualizado.getNome());
            produtoExistente.setPreco(produtoAtualizado.getPreco());
            produtoExistente.setDescricao(produtoAtualizado.getDescricao());
            produtoExistente.setImgUrl(produtoAtualizado.getImgUrl());
            produtoExistente = repository.save(produtoExistente);
            return new ProdutoDTO(produtoExistente);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundExceptions("Recurso não encontrado para atualização");

        }
    }

    @Transactional
    public void delete(Integer id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundExceptions("Recurso nao encontado com id*" + id);
        }
    }

    @Transactional
    public @Valid ProdutoDTO alterarNome(Integer id, @Valid ProdutoDTO dto) {
        Produto produto = repository.getReferenceById(id);
        produto.setNome(dto.getNome());
        repository.save(produto);
        return new ProdutoDTO(produto);
    }

    @Transactional
    public @Valid ProdutoDTO alterarPreco(Integer id, @Valid ProdutoDTO dto) {
        Produto produto = repository.getReferenceById(id);
        produto.setPreco(dto.getPreco());
        repository.save(produto);
        return new ProdutoDTO(produto);
    }

    @Transactional
    public @Valid ProdutoDTO alterarDescricao(Integer id, @Valid ProdutoDTO dto) {
        Produto produto = repository.getReferenceById(id);
        produto.setDescricao(dto.getDescricao());
        repository.save(produto);
        return new ProdutoDTO(produto);
    }

    @Transactional
    public @Valid ProdutoDTO alterarImagem(Integer id, @Valid ProdutoDTO dto) {
        Produto produto = repository.getReferenceById(id);
        produto.setImgUrl(dto.getImgUrl());
        repository.save(produto);
        return new ProdutoDTO(produto);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoDTO> buscarProdutos(Integer categoriaId, Pageable pageable) {
        Page<Produto> produto = (categoriaId != null) ?
                repository.findByCategoriasId(categoriaId, pageable) :
                repository.findAll(pageable);
        return produto.map(x -> new ProdutoDTO(x));
    }

    //------------------
    @Transactional
    public void associateCategoria(Integer produtoId, Integer categoriaId) {
        Produto produto = repository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundExceptions("Produto não encontrado"));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundExceptions("Categoria não encontrada"));

        produto.getCategorias().add(categoria);
        repository.save(produto);
    }

    @Transactional
    public List<ProdutoDTO> buscarProdutosString(String filtro) {
        List<Produto> produtos;
        if (filtro == null || filtro.isEmpty()) {
            produtos = repository.findAll();
        } else {
            produtos = repository.findByNomeContainingIgnoreCase(filtro);
        }
        return produtos.stream().map(ProdutoDTO::new).collect(Collectors.toList());
    }

    public List<ProdutoDTO> findAllByCategoriaAndPreco(Integer categoriaId, Double minPrice, Double maxPrice) {
        List<Produto> produtos = repository.findByCategoriaAndPreco(categoriaId, minPrice, maxPrice);
        return produtos.stream().map(ProdutoDTO::new).collect(Collectors.toList());
    }



}
