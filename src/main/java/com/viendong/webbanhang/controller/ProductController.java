package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.CartItem;
import com.viendong.webbanhang.model.Product;
import com.viendong.webbanhang.model.Reviews;
import com.viendong.webbanhang.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CartService cartService;

    public ProductController(ProductService productService, CategoryService categoryService, ReviewService reviewService ) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;

    }


    @GetMapping
    public String showProducts(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categories", required = false) List<String> categories,
                               @RequestParam(value = "priceRanges", required = false) List<String> priceRanges,
                               @RequestParam(value = "brands", required = false) List<String> brands) {
        page = Math.max(0, (page + 1) - 1);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.searchProductsByName(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else if ((categories != null && !categories.isEmpty()) || (priceRanges != null && !priceRanges.isEmpty()) || (brands != null && !brands.isEmpty())) {
            productPage = productService.filterProducts(categories, priceRanges, brands, pageable);
        } else {
            productPage = productService.getAllProducts(pageable);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage);
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("selectedPriceRanges", priceRanges);
        model.addAttribute("selectedBrands", brands); // Thêm danh sách brands được chọn
        model.addAttribute("brands", brandService.getAllBrand());// Danh sách tất cả brands
        long totalProducts = productService.countAllProducts(); // Thêm dòng này
        model.addAttribute("totalProducts", totalProducts);
        return "/products/products-list";
    }
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brand", brandService.getAllBrand());
        return "/products/add-product";
    }
    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brand", brandService.getAllBrand());
            return "/products/add-product";
        }
        try {
            productService.addProduct(product);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brand", brandService.getAllBrand());
            return "/products/add-product";
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brand", brandService.getAllBrand());
        return "/products/edit-product"; // This should point to the edit product view
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @Valid Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brand", brandService.getAllBrand());
            return "/products/edit-product"; // Redirect back to the edit page if there are errors
        }

        // Ensure the ID of the product being updated matches the path variable
        product.setId(id);
        productService.updateProduct(product); // Save the updated product
        return "redirect:/products";
    }
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    @GetMapping("/productDetail/{id}")
    public String getProductDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        List<Reviews> reviews = reviewService.getReviewsByProductId(id);

        if (reviews == null) {
            reviews = new ArrayList<>();
        }

        // Logging dữ liệu kiểm tra
        System.out.println("Product: " + product);
        System.out.println("Reviews: " + reviews);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrand());

        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        // Tính tổng số lượng các sản phẩm trong giỏ hàng
        int totalItems = (int) cartItems.stream().count();

        // Thêm tổng số lượng vào mô hình
        model.addAttribute("totalItems", totalItems);

        return "products/productDetail";
    }

    @PostMapping("/productDetail/{id}/addReview")
    public String addReview(@PathVariable("id") Long productId,
                            @RequestParam("name") String name,
                            @RequestParam("comment") String comment,
                            @RequestParam(value = "rating", defaultValue = "0") int rating) {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Invalid rating value: " + rating);
        }

        // Create review object
        Reviews review = new Reviews();
        review.setName(name != null && !name.isEmpty() ? name : "Anonymous");
        review.setComment(comment != null && !comment.isEmpty() ? comment : "No comment provided");
        review.setRating(rating);

        // Ensure product exists
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + productId));
        review.setProduct(product);

        // Log review data
        System.out.println("New Review: " + review);

        // Save review
        reviewService.addReview(review);

        return "redirect:/products/productDetail/" + productId;
    }

}