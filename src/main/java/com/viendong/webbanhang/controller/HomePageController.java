package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.Product;
import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.service.BrandService;
import com.viendong.webbanhang.service.CategoryService;
import com.viendong.webbanhang.service.ProductService;
import com.viendong.webbanhang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping()
    public String showHomePage(Model model){
        model.addAttribute("products",productService.getAllProducts());
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrand());
        return "homepages/hello";
    }

    @GetMapping("/category")
    public String showCategoryPage(Model model) {
        List<Product> products = productService.getAllProducts(); // Lấy danh sách sản phẩm
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("productCount", products.size()); // Đếm số lượng sản phẩm và truyền vào Model
        return "homepages/filter-page";
    }

    @GetMapping("/category/filter")
    public String filterByCategory(@RequestParam("categoryId") Long categoryId, Model model) {
        List<Product> products = productService.getProductsByCategory(categoryId); // Lấy danh sách sản phẩm
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("productCount", products.size()); // Đếm số lượng sản phẩm và truyền vào Model
        return "homepages/filter-page";
    }

    @GetMapping("/brand")
    public String showBrandPage(Model model) {
        List<Product> products = productService.getAllProducts(); // Lấy danh sách sản phẩm
        model.addAttribute("products", products);
        model.addAttribute("brands", brandService.getAllBrand());
        model.addAttribute("productCount", products.size()); // Đếm số lượng sản phẩm và truyền vào Model
        return "homepages/filter-page";
    }

    @GetMapping("/brand/filter")
    public String filterByBrand(@RequestParam("brandId") Long brandId, Model model) {
        List<Product> products = productService.getProductsByBrand(brandId); // Lấy danh sách sản phẩm
        model.addAttribute("products", products);
        model.addAttribute("brands", brandService.getAllBrand());
        model.addAttribute("productCount", products.size()); // Đếm số lượng sản phẩm và truyền vào Model
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

        // Get the count of favorite products
        long productCount = productService.countFavoriteProducts(currentUser.getId()); // Count the favorite products
        model.addAttribute("productCount", productCount); // Add the count to the model

        // Return the view name
        return "homepages/favorite-page";
    }

    @GetMapping("/favorite/add")
    public String addToFavorites(@RequestParam("productId") Long productId, Model model) {
        Optional<Product> productOptional = productService.getProductById(productId);
        if (productOptional.isPresent()) {
            // Get the currently logged-in user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                model.addAttribute("error", "You must be logged in to add products to favorites.");
                return "redirect:/login";  // Redirect to login page if user is not logged in
            }
            userService.addToFavorites(currentUser, productOptional.get());  // Add product to favorites
        } else {
            model.addAttribute("error", "Product not found.");
        }
        return "redirect:/homepage/favorite";  // Redirect to favorites page
    }

//    @GetMapping("/favorite/remove")
//    public String removeFromFavorites(@RequestParam("productId") Long productId, Model model) {
//        // Xóa sản phẩm khỏi danh sách yêu thích của người dùng
//        Optional<Product> productOptional = productService.getProductById(productId);
//        if (productOptional.isPresent()) {
//            userService.removeFromFavorites(productOptional.get());  // Thực hiện phương thức này trong UserService
//        }
//        return "redirect:/homepages/favorite-page";  // Điều hướng lại đến trang yêu thích
//    }

}
