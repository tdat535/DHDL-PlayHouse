package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.*;
import com.viendong.webbanhang.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ReviewService reviewService;
    private final CartService cartService;

    public ProductController(ProductService productService, CategoryService categoryService, BrandService brandService, ReviewService reviewService, CartService cartService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.reviewService = reviewService;
        this.cartService = cartService;
    }

    @GetMapping
    public String showProducts(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 5); // Mỗi trang 5 sản phẩm

        model.addAttribute("product", new Product());

        // Sử dụng phương thức getProducts để lấy tất cả sản phẩm
        Page<Product> productsPage = productService.getProducts(pageable); // Lấy sản phẩm theo trang
        model.addAttribute("products", productsPage.getContent()); // Thêm danh sách sản phẩm vào model
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrand());
        model.addAttribute("totalProducts", productsPage.getTotalElements()); // Tổng số sản phẩm
        model.addAttribute("currentPage", page); // Lưu lại trang hiện tại

        return "/products/products-list";
    }

    @GetMapping("/filter")
    public String filterProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categories", required = false) List<String> categories,
            @RequestParam(value = "brands", required = false) List<String> brands,
            @RequestParam(value = "page", defaultValue = "0") int page, Model model) {

        Pageable pageable = PageRequest.of(page, 5); // Mỗi trang 5 sản phẩm

        Page<Product> filteredProducts;

        if (keyword != null && !keyword.isEmpty()) {
            filteredProducts = productService.searchProducts(keyword, categories, brands, pageable);
        } else {
            filteredProducts = productService.filterProducts(categories, brands, pageable);
        }

        model.addAttribute("product", new Product());
        model.addAttribute("products", filteredProducts.getContent()); // Thêm danh sách sản phẩm vào model
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrand());
        model.addAttribute("totalProducts", filteredProducts.getTotalElements()); // Tổng số sản phẩm
        model.addAttribute("currentPage", page); // Lưu lại trang hiện tại
        model.addAttribute("keyword", keyword); // Truyền từ khóa tìm kiếm vào model

        return "/products/products-list";
    }


    @PostMapping("/add")
    public String addProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrand());
            return "/products/products-list";
        }

        try {
            // Lưu file hình ảnh
            if (!imageFile.isEmpty()) {
                String imageUrl = productService.saveImage(imageFile); // Lưu file và trả về đường dẫn
                product.setImage(imageUrl); // Lưu đường dẫn ảnh vào cơ sở dữ liệu
            }

            product.setRating(null); // Đảm bảo rating là null nếu không cung cấp
            redirectAttributes.addFlashAttribute("message",
                    "Thêm sản phẩm '" + product.getName() + "' thành công");
            productService.addProduct(product, redirectAttributes);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrand());
            return "/products/products-list";
        }
        return "redirect:/products";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            // Lấy sản phẩm hiện tại từ cơ sở dữ liệu
            Product existingProduct = productService.getProductById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            // Nếu không có hình ảnh mới, giữ nguyên hình ảnh cũ
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = productService.saveImage(imageFile);
                product.setImage(imageUrl); // Cập nhật hình ảnh mới
            } else {
                product.setImage(existingProduct.getImage()); // Giữ lại hình ảnh cũ
            }

            // Cập nhật các thông tin khác của sản phẩm
            redirectAttributes.addFlashAttribute("message",
                    "Cập nhật sản phẩm '" + product.getName() + "' thành công");
            productService.updateProduct(id, product);

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/products";
        }

        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xóa thành công!");
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