package com.viendong.webbanhang.repository;

import com.viendong.webbanhang.model.Brand;
import com.viendong.webbanhang.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Optional<User> findById(Long id);
    boolean existsByEmail(String email);  // Phương thức kiểm tra sự tồn tại của email

}
