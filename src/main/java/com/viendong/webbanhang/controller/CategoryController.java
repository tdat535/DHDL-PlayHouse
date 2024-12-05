package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.Brand;
import com.viendong.webbanhang.model.Category;
import com.viendong.webbanhang.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class    CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories/add")
    public String addCategory(@Valid Category category, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/categories/add-category";
        }
        categoryService.addCategory(category);
        return "redirect:/categories";
    }

    // Hiển thị danh sách danh mục
    @GetMapping("/categories")
    public String listBrand(Model model, @RequestParam(value = "id", required = false) Long id) {
        model.addAttribute("category", new Category());


        // Lấy danh sách tất cả các thương hiệu
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        // Số lượng thương hiệu
        long CategoryCount = categoryService.countAllCategory();
        model.addAttribute("CategoryCount", CategoryCount);

        // Xử lý nếu ID được truyền
        if (id != null) {
            Optional<Category> categoryOptional = categoryService.getCategoryById(id);
            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();
                model.addAttribute("category", category);
                model.addAttribute("products", category.getProducts()); // Giả sử Brand có quan hệ với Product
            } else {
                throw new IllegalStateException("Category with ID " + id + " not found.");
            }
        }

        return "/categories/categories-list";
    }

    // GET request to show category edit form
    @GetMapping("/categories/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id).orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "/categories/update-category";
    }

    // POST request to update category
    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable("id") Long id, @Valid Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            return "/categories/update-category";
        }
        categoryService.updateCategory(category);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }

    // GET request for deleting category
    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id).orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        categoryService.deleteCategory(id);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }

}
