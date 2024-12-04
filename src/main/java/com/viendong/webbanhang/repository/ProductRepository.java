package com.viendong.webbanhang.repository;

import com.viendong.webbanhang.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByBrandId(Long brandId);

    public List<Product> findByNameContainingIgnoreCase(String query);

    boolean existsByName(String name);


    // Phương thức tìm kiếm có phân trang
    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%) AND " +
            "(:categories IS NULL OR p.category.name IN :categories) AND " +
            "(:brands IS NULL OR p.brand.name IN :brands)")
    Page<Product> findByKeywordAndFilters(@Param("keyword") String keyword,
                                          @Param("categories") List<String> categories,
                                          @Param("brands") List<String> brands,
                                          Pageable pageable);

    // Phương thức lọc có phân trang
    @Query("SELECT p FROM Product p WHERE " +
            "(:categories IS NULL OR p.category.name IN :categories) AND " +
            "(:brands IS NULL OR p.brand.name IN :brands)")
    Page<Product> findByFilters(@Param("categories") List<String> categories,
                                @Param("brands") List<String> brands,
                                Pageable pageable);
}


