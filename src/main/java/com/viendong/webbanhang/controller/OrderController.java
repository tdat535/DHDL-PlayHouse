    package com.viendong.webbanhang.controller;

    import com.viendong.webbanhang.model.CartItem;
    import com.viendong.webbanhang.model.Order;
    import com.viendong.webbanhang.service.CartService;
    import com.viendong.webbanhang.service.OrderService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @Controller
    @RequestMapping("/order")
    public class OrderController {

        private final OrderService orderService;
        private final CartService cartService;

        @Autowired
        public OrderController(OrderService orderService, CartService cartService) {
            this.orderService = orderService;
            this.cartService = cartService;
        }

        @GetMapping("/checkout")
        public String checkout(Model model) {

            double totalProduct = cartService.calculateTotalProduct();
            double total = cartService.calculateTotal();
            double shippingFee = cartService.getShippingFee();

            System.out.println("Total Product: " + totalProduct);
            System.out.println("Shipping Fee: " + shippingFee);
            System.out.println("Total: " + total);


            List<CartItem> cartItems = cartService.getCartItems();
            if (cartItems.isEmpty()) {
                return "redirect:/cart"; // Giỏ hàng trống, chuyển về giỏ hàng
            }

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalProduct", totalProduct); // Tổng tiền sản phẩm
            model.addAttribute("total", total); // Truyền tổng tiền sang view
            model.addAttribute("shippingFee", shippingFee);
            return "cart/checkout";
        }

        @PostMapping("/submit")
        public String submitOrder(@RequestParam String customer_name,
                                  @RequestParam String email,
                                  @RequestParam String phone_number,
                                  @RequestParam String address,
                                  @RequestParam String payment_method,
                                  @RequestParam(required = false, defaultValue = "") String note,
                                  Model model) {

            // Kiểm tra nếu giỏ hàng trống
            List<CartItem> cartItems = cartService.getCartItems();
            if (cartItems.isEmpty()) {
                return "redirect:/cart"; // Nếu giỏ hàng trống, chuyển về trang giỏ hàng
            }

            // Tính toán tổng tiền
            double totalProduct = cartService.calculateTotalProduct(); // Tính tổng sản phẩm
            double shippingFee = cartService.getShippingFee(); // Lấy phí vận chuyển
            double total = totalProduct + shippingFee; // Tổng tiền (sản phẩm + vận chuyển)

            // Gửi các giá trị vào model để hiển thị trên trang xác nhận
            model.addAttribute("totalProduct", totalProduct);
            model.addAttribute("shippingFee", shippingFee);
            model.addAttribute("total", total);

            // Tạo đơn hàng
            orderService.createOrder(customer_name, email, phone_number, address, payment_method, note, cartItems);

            // Chuyển tới trang xác nhận đơn hàng
            return "redirect:/order/confirmation";
        }

        @GetMapping("/confirmation")
        public String confirmation(Model model) {
            model.addAttribute("message", "Your order has been confirmed.");
            return "cart/order-confirmation";
        }

        @GetMapping("/orderList") // Ensure this matches the link in your templates
        public String listOrders(Model model) {
            List<Order> orders = orderService.getAllOrders();
            model.addAttribute("orders", orders);
            return "order/orderList"; // Thymeleaf template for listing orders
        }

        @GetMapping("/{id}") // This will handle order detail retrieval
        public String getOrder(@PathVariable Long id, Model model) {
            Order order = orderService.getOrderById(id).orElseThrow(() -> new IllegalStateException("Order not found"));
            model.addAttribute("order", order);
            return "order/orderDetail"; // Thymeleaf template for order details
        }

        @PostMapping
        public String createOrder(@ModelAttribute Order order) {
            orderService.createOrder(order);
            return "redirect:/products"; // Redirect after creating an order
        }

        @GetMapping("/delete/{id}")
        public String deleteOrder(@PathVariable("id") Long id) {
            orderService.deleteOrder(id);
            return "redirect:/order/orderList"; // Redirect after deleting an order
        }
    }
