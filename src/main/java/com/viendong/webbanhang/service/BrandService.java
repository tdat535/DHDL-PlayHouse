package com.viendong.webbanhang.service;

import com.viendong.webbanhang.model.Brand;
import com.viendong.webbanhang.model.Product;
import com.viendong.webbanhang.repository.BrandRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandService {
    private final BrandRepository brandRepository;


    public Page<Brand> searchBrandByName(String keyword, Pageable pageable) {
        return brandRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    public long count(){
        return brandRepository.count();
    }
    // Phân trang sản phẩm
    public Page<Brand> getAllProducts(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }

    public List<Brand> getAllBrand() {
        return brandRepository.findAll();
    }
    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }
    public void addBrand (Brand brand) {
        if (brandRepository.existsByName(brand.getName())) {
            throw new IllegalStateException("Brand with name '" + brand.getName() + "' already exists.");
        }
        brandRepository.save(brand);
    }
    public void updateBrand (@NonNull Brand brand) {
        Brand existingBrand = brandRepository.findById(brand.getId()).orElseThrow(() -> new IllegalStateException("Category with ID " + brand.getId() + " does not exist."));
        existingBrand.setImage(brand.getImage());
        existingBrand.setName(brand.getName());
        brandRepository.save(existingBrand);
    }
    public void deleteBrand(Long id) {
        if(!brandRepository.existsById(id)) {
            throw new IllegalStateException("Category with ID " + id + " does not exist.");
        }
        brandRepository.deleteById(id);
    }
}
