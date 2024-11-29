package com.viendong.webbanhang.service;

import com.viendong.webbanhang.Role;
import com.viendong.webbanhang.model.Product;
import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.repository.IRoleRepository;
import com.viendong.    webbanhang.repository.IUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;

    public long countUser() {
        return userRepository.count();
    }

    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
            userRepository.save(user);
        }, () -> {
            throw new UsernameNotFoundException("User not found");
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities(user.getAuthorities()).accountExpired(!user.isAccountNonExpired()).accountLocked(!user.isAccountNonLocked()).credentialsExpired(!user.isCredentialsNonExpired()).disabled(!user.isEnabled()).build();
    }


    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(String.valueOf(id));
    }
    public Page<User> findUsersWithPagination(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getCurrentUser() {
        // Get the username of the currently authenticated user
        String username = getCurrentUsername();

        // Retrieve and return the User entity from the repository based on the username
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private String getCurrentUsername() {
        // Get the currently authenticated user from SecurityContextHolder
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        throw new RuntimeException("User not authenticated");
    }


    private Set<Product> favoriteProducts = new HashSet<>();

    public Set<Product> getFavoriteProducts(User user) {
        return user.getFavoriteProducts();
    }

    public void addToFavorites(User user, Product product) {
        user.addToFavorites(product);
        userRepository.save(user);  // Save the user with the updated favorites list
    }

    public void removeFromFavorites(User user, Product product) {
        user.getFavoriteProducts().remove(product);
        userRepository.save(user); // Cập nhật danh sách yêu thích trong cơ sở dữ liệu
    }

}
