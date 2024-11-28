package com.viendong.webbanhang.repository;

import com.viendong.webbanhang.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByBrandId(Long brandId);

    @Query("SELECT p FROM Product p WHERE " +
            "(:categories IS NULL OR p.category.name IN :categories) " +
            "AND (:priceRanges IS NULL OR " +
            "     (p.price >= 0.0 AND p.price <= 20.0 AND '0$-20$' IN :priceRanges) OR " +
            "     (p.price > 20 AND p.price <= 40 AND '20$-40$' IN :priceRanges) OR " +
            "     (p.price > 40 AND '40$-∞' IN :priceRanges)) " +
            "AND (:brands IS NULL OR p.brand.name IN :brands)")
    Page<Product> findByCategoryAndPriceRangeAndBrand(@Param("categories") List<String> categories,
                                                      @Param("priceRanges") List<String> priceRanges,
                                                      @Param("brands") List<String> brands,
                                                      Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    boolean existsByName(String name);
}

