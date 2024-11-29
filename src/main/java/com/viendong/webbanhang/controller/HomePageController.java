package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.CartItem;
import com.viendong.webbanhang.model.Product;
import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/homepage")
public class HomePageController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @Autowired
    private UserService userService;  // Đảm bảo bạn có một service người dùng

    @Autowired
    private CartService cartService;

    @GetMapping()
    public String showHomePage(Model model){
        model.addAttribute("products",productService.getAllProducts());
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrand());

        List<CartItem> cartItems = cartService.getCartItems();
        // Tính tổng số lượng các sản phẩm trong giỏ hàng
        int totalItems = (int) cartItems.stream().count();

        // Thêm tổng số lượng vào mô hình
        model.addAttribute("totalItems", totalItems);

        return "homepages/hello";
    }

    @GetMapping("/category/filter")
    public String filterByCategory(@RequestParam(value = "categoryId", required = false) Long categoryId, Model model) {
        List<Product> products;
        model.addAttribute("categories", categoryService.getAllCategories());
        if (categoryId == null) {
            // Nếu không có brandId, lấy tất cả sản phẩm
            products = productService.getAllProducts();
        } else {
            // Nếu có brandId, lấy sản phẩm theo danh mục
            products = productService.getProductsByCategory(categoryId);
        }

        model.addAttribute("products", products);
        model.addAttribute("brands", brandService.getAllBrand());
        model.addAttribute("productCount", products.size()); // Đếm số lượng sản phẩm và truyền vào Model

        List<CartItem> cartItems = cartService.getCartItems();
        // Tính tổng số lượng các sản phẩm trong giỏ hàng
        int totalItems = (int) cartItems.stream().count();

        // Thêm tổng số lượng vào mô hình
        model.addAttribute("totalItems", totalItems);
        return "homepages/filter-page";
    }

    @GetMapping("/brand/filter")
    public String filterByBrand(@RequestParam(value = "brandId", required = false) Long brandId, Model model) {
        List<Product> products;
        model.addAttribute("brands", brandService.getAllBrand());
        if (brandId == null) {
            // Nếu không có brandId, lấy tất cả sản phẩm
            products = productService.getAllProducts();
        } else {
            // Nếu có brandId, lấy sản phẩm theo thương hiệu
            products = productService.getProductsByBrand(brandId);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("productCount", products.size());

        List<CartItem> cartItems = cartService.getCartItems();
        // Tính tổng số lượng các sản phẩm trong giỏ hàng
        int totalItems = (int) cartItems.stream().count();

        // Thêm tổng số lượng vào mô hình
        model.addAttribute("totalItems", totalItems);

        return "homepages/filter-page";
    }

    @GetMapping("/favorite")
    public String showFavoritePage(Model model) {
        // Get the currently logged-in user
        User currentUser = userService.getCurrentUser(); // Get the current user from the service

        // Get the favorite products of the current user
        Set<Product> favoriteProducts = userService.getFavoriteProducts(currentUser); // Pass the current user to the method

        // Add the favorite products to the model
        model.addAttribute("favoriteProducts", favoriteProducts);
        model.addAttribute("brands", brandService.getAllBrand());
        model.addAttribute("categories", categoryService.getAllCategories());

        // Get the count of favorite products
        long productCount = productService.countFavoriteProducts(currentUser.getId()); // Count the favorite products
        model.addAttribute("productCount", productCount); // Add the count to the model

        // Return the view name

        List<CartItem> cartItems = cartService.getCartItems();
        // Tính tổng số lượng các sản phẩm trong giỏ hàng
        int totalItems = (int) cartItems.stream().count();

        // Thêm tổng số lượng vào mô hình
        model.addAttribute("totalItems", totalItems);

        return "homepages/favorite-page";
    }

    @GetMapping("/favorite/add")
    public String addToFavorites(@RequestParam("productId") Long productId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productService.getProductById(productId);
        if (productOptional.isPresent()) {
            // Get the currently logged-in user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                model.addAttribute("error", "You must be logged in to add products to favorites.");
                return "redirect:/login";  // Redirect to login page if user is not logged in
            }
            if (productOptional.isPresent()) {
                Product product = productOptional.get();

                // Xóa sản phẩm khỏi danh sách yêu thích
                userService.addToFavorites(currentUser, productOptional.get());  // Add product to favorites

                // Thêm thông báo thành công
                redirectAttributes.addFlashAttribute("message", "Bạn đã thêm sản phẩm '" + product.getName() + "' vào danh sách yêu thích.");
            } else {
                // Thêm thông báo lỗi nếu sản phẩm không tồn tại
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm để xóa.");
            }
        } else {
            model.addAttribute("error", "Product not found.");
        }
        return "redirect:/homepage";  // Redirect to favorites page
    }

    @GetMapping("/favorite/remove")
    public String removeFromFavorites(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
        // Lấy người dùng hiện tại
        User currentUser = userService.getCurrentUser();

        // Tìm sản phẩm cần xóa
        Optional<Product> productOptional = productService.getProductById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Xóa sản phẩm khỏi danh sách yêu thích
            userService.removeFromFavorites(currentUser, product);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Bạn đã xóa sản phẩm '" + product.getName() + "' khỏi danh sách yêu thích.");
        } else {
            // Thêm thông báo lỗi nếu sản phẩm không tồn tại
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm để xóa.");
        }

        // Điều hướng lại danh sách yêu thích
        return "redirect:/homepage/favorite";
    }

}
