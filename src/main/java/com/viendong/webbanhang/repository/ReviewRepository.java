package com.viendong.webbanhang.repository;

import com.viendong.webbanhang.model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    List<Reviews> findByProductId(Long productId);
}

