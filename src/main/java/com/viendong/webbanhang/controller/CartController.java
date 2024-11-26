package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.CartItem;
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

    @PostMapping("/add")
    public String addToCart (@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
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
    public String decreaseQuantity(@PathVariable Long productId) {
        cartService.decreaseQuantity(productId);
        return "redirect:/cart";
    }

    @PostMapping("/updateQuantity/{productId}")
    public String updateQuantity(@PathVariable Long productId, @RequestParam("quantity") int quantity, RedirectAttributes redirectAttributes) {
        if (quantity < 1) {
            redirectAttributes.addFlashAttribute("error", "Quantity must be at least 1");
            return "redirect:/cart";
        }
        cartService.updateQuantity(productId, quantity);
        redirectAttributes.addFlashAttribute("message", "Cart updated successfully!");
        return "redirect:/cart";
    }

    @PostMapping("/updateShipping")
    public String updateShipping(@RequestParam("shippingOption") double shippingFee, RedirectAttributes redirectAttributes) {
        cartService.setShippingFee(shippingFee);
        redirectAttributes.addFlashAttribute("message", "Shipping option updated!");
        return "redirect:/cart";
    }



}
