package com.viendong.webbanhang.repository;

import com.viendong.webbanhang.model.Brand;
import com.viendong.webbanhang.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Page<Brand> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    boolean existsByName(String name);
}
