package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.Brand;
import com.viendong.webbanhang.model.Category;
import com.viendong.webbanhang.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class BrandController {
    @Autowired
    private BrandService brandService;


    @PostMapping("/brand/add")
    public String addBrand(@Valid Brand brand, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/brand/add-brand";
        }
        try {
            brandService.addBrand(brand);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/brand/add-brand";
        }
        return "redirect:/brand";
    }

    @GetMapping("/brand")
    public String listBrand(Model model, @RequestParam(value = "id", required = false) Long id) {
        model.addAttribute("brand", new Brand());

        // Lấy danh sách tất cả các thương hiệu
        List<Brand> brands = brandService.getAllBrand();
        model.addAttribute("brands", brands);

        // Số lượng thương hiệu
        long BrandCount = brandService.count();
        model.addAttribute("BrandCount", BrandCount);

        // Xử lý nếu ID được truyền
        if (id != null) {
            Optional<Brand> brandOptional = brandService.getBrandById(id);
            if (brandOptional.isPresent()) {
                Brand brand = brandOptional.get();
                model.addAttribute("brand", brand);
                model.addAttribute("products", brand.getProducts()); // Giả sử Brand có quan hệ với Product
            } else {
                throw new IllegalStateException("Brand with ID " + id + " not found.");
            }
        }

        return "/brand/brand-list";
    }


    @GetMapping("/brand/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Brand brand = brandService.getBrandById(id).orElseThrow(() -> new IllegalArgumentException("Invalid brand Id:" + id));
        model.addAttribute("brand", brand);
        return "/brand/update-brand";
    }

    @PostMapping("/brand/update/{id}")
    public String updateBrand(@PathVariable("id") Long id, @Valid Brand brand, BindingResult result, Model model) {
        if (result.hasErrors()) {
            brand.setId(id);
            return "/brand/update-brand";
        }
        brandService.updateBrand(brand);
        model.addAttribute("brand", brandService.getAllBrand());
        return "redirect:/brand";
    }

    @GetMapping("/brand/delete/{id}")
    public String deleteBrand(@PathVariable("id") Long id, Model model) {
        Brand brand = brandService.getBrandById(id).orElseThrow(() -> new IllegalArgumentException("Invalid brand Id:" + id));
        brandService.deleteBrand(id);
        model.addAttribute("brand", brandService.getAllBrand());
        return "redirect:/brand";
    }
}
