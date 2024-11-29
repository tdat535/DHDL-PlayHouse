package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.CartItem;
import com.viendong.webbanhang.model.Product;
import com.viendong.webbanhang.service.BrandService;
import com.viendong.webbanhang.service.CartService;
import com.viendong.webbanhang.service.CategoryService;
import com.viendong.webbanhang.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @GetMapping
    public String showCart(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        // Tính tổng số lượng các sản phẩm trong giỏ hàng
        int totalItems = (int) cartItems.stream().count();

        // Thêm tổng số lượng vào mô hình
        model.addAttribute("totalItems", totalItems);
        // Tổng giá trị sản phẩm
        double totalProduct = cartService.calculateTotalProduct();

        // Tổng giá trị sản phẩm cộng phí vận chuyển
        double total = cartService.calculateTotal();
        double shippingFee = cartService.getShippingFee(); // Lấy phí vận chuyển hiện tại

        model.addAttribute("totalProduct", totalProduct); // Tổng tiền sản phẩm
        model.addAttribute("total", total); // Tổng tiền bao gồm phí vận chuyển
        model.addAttribute("shippingFee", shippingFee); // Phí vận chuyển

        // Các thông tin khác
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("brands", brandService.getAllBrand());

        return "/cart/cart";
    }


    // Controller method for Buy Now
    // Controller method for Buy Now (POST)
    @PostMapping("/add")
    public String buyNow(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addToCart(productId, quantity);  // Add product to cart
        return "redirect:/cart";  // Redirect to the cart page
    }

    // Controller method for Add to Cart (GET)
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity, RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productService.getProductById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            cartService.addToCart(productId, quantity);
            redirectAttributes.addFlashAttribute("message", "Bạn đã thêm " + quantity + " sản phẩm '" + product.getName() + "' vào giỏ hàng.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm.");
        }
        return "redirect:/products/productDetail/" + productId;  // Chuyển hướng lại trang chi tiết sản phẩm
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId){
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }
    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }

    // Tăng số lượng sản phẩm
    @GetMapping("/increaseQuantity/{productId}")
    public String increaseQuantity(@PathVariable Long productId) {
        cartService.increaseQuantity(productId);
        return "redirect:/cart";
    }

    // Giảm số lượng sản phẩm
    @GetMapping("/decreaseQuantity/{productId}")
    public String decreaseQuantity(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        cartService.decreaseQuantity(productId, redirectAttributes);
        return "redirect:/cart";
    }

    @PostMapping("/updateQuantity/{productId}")
    public String updateQuantity(@PathVariable Long productId, @RequestParam("quantity") int quantity, RedirectAttributes redirectAttributes) {
        if (quantity < 1) {
            // Nếu quantity < 1, xóa sản phẩm khỏi giỏ hàng và quay lại trang giỏ hàng
            cartService.removeFromCart(productId);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã bị xóa khỏi giỏ hàng.");
            return "redirect:/cart";
        }

        // Kiểm tra sự tồn tại của sản phẩm
        Optional<Product> productOptional = productService.getProductById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Cập nhật số lượng trong giỏ hàng
            cartService.updateQuantity(productId, quantity);

            // Thêm thông báo thành công sau khi cập nhật giỏ hàng
            redirectAttributes.addFlashAttribute("message", "Bạn đã cập nhật số lượng của sản phẩm ' " + product.getName() + " ' là: " + quantity);
        } else {
            // Thông báo lỗi nếu không tìm thấy sản phẩm
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm.");
        }

        // Quay lại trang giỏ hàng sau khi cập nhật
        return "redirect:/cart";
    }



    @PostMapping("/updateShipping")
    public String updateShipping(@RequestParam("shippingOption") double shippingFee, RedirectAttributes redirectAttributes) {
        cartService.setShippingFee(shippingFee);
        redirectAttributes.addFlashAttribute("message", "Shipping option updated!");
        return "redirect:/cart";
    }



}
