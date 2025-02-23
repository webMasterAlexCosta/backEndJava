package br.com.aplrm.aplrm.repositories;

import br.com.aplrm.aplrm.entities.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    Page<Produto> findByCategoriasId(Integer categoriaId, Pageable pageable);

    List<Produto> findByNomeContainingIgnoreCase(String filtro);

    @Query("SELECT p FROM Produto p JOIN p.categorias c WHERE c.id = :categoriaId AND (:minPrice IS NULL OR p.preco >= :minPrice) AND (:maxPrice IS NULL OR p.preco <= :maxPrice) ORDER BY p.preco ASC")
    List<Produto> findByCategoriaAndPreco(
            @Param("categoriaId") Integer categoriaId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);



}