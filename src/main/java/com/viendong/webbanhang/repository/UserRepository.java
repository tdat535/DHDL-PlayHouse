package com.viendong.webbanhang.repository;

import com.viendong.webbanhang.model.Brand;
import com.viendong.webbanhang.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
