package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.service.BrandService;
import com.viendong.webbanhang.service.CategoryService;
import com.viendong.webbanhang.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/homepage")
public class HomePageController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @GetMapping()
    public String showHomePage(Model model){
        model.addAttribute("products",productService.getAllProducts());
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrand());
        return "homepages/hello";
    }

    @GetMapping("/category")
    public String showCategoryPage(Model model){
        model.addAttribute("products",productService.getAllProducts());
        model.addAttribute("categories",categoryService.getAllCategories());
        return "homepages/category-page";
    }
}
