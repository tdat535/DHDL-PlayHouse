package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.Brand;
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

@Controller
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/brand/add")
    public String showAddForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "/brand/add-brand";
    }

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
    public String listBrand(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "7") int size,
                            @RequestParam(value = "keyword", required = false) String keyword) {
        page = Math.max(0, page);
        Pageable pageable = PageRequest.of(page, size);
        Page<Brand> brandPage;

        if (keyword != null && !keyword.isEmpty()) {
            brandPage = brandService.searchBrandByName(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            brandPage = brandService.getAllProducts(pageable);
        }

        long brandCount = brandService.count();
        model.addAttribute("brandCount", brandCount);
        model.addAttribute("brandPage", brandPage);
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
