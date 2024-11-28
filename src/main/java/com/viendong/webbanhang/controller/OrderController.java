    package com.viendong.webbanhang.controller;

    import com.viendong.webbanhang.model.CartItem;
    import com.viendong.webbanhang.model.Order;
    import com.viendong.webbanhang.service.CartService;
    import com.viendong.webbanhang.service.OrderService;
    import com.viendong.webbanhang.service.QRCodeService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import com.google.zxing.WriterException;
    import com.google.zxing.common.BitMatrix;

    import java.util.List;

    @Controller
    @RequestMapping("/order")
    public class OrderController {

        private final OrderService orderService;
        private final CartService cartService;
        private final QRCodeService qrCodeService; // Add this line to inject the QRCodeService

        @Autowired
        public OrderController(OrderService orderService, CartService cartService, QRCodeService qrCodeService) {
            this.orderService = orderService;
            this.cartService = cartService;
            this.qrCodeService = qrCodeService;
        }

        @GetMapping("/checkout")
        public String checkout(Model model) {

            double totalProduct = cartService.calculateTotalProduct();
            double total = cartService.calculateTotal();
            double shippingFee = cartService.getShippingFee();

            System.out.println("Total Product: " + totalProduct);
            System.out.println("Shipping Fee: " + shippingFee);
            System.out.println("Total: " + total);

            // Retrieve cart items
            List<CartItem> cartItems = cartService.getCartItems();
            if (cartItems.isEmpty()) {
                return "redirect:/cart"; // If the cart is empty, redirect to cart
            }

            // Prepare payment URI for QR Code (similar to your POST method)
            String paymentUri = "00020101021138570010A00000072701270006970422011300008003860380208QRIBFTTA53037045802VN630404fa";
            String qrCodeBase64 = null;
            try {
                // Generate QR code
                qrCodeBase64 = qrCodeService.generateQRCodeBase64(paymentUri);
            } catch (WriterException e) {
                // Handle error
                e.printStackTrace();
                model.addAttribute("errorMessage", "There was an error generating the QR code.");
            }

            // Add model attributes for cart and payment details
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalProduct", totalProduct); // Total price of products
            model.addAttribute("total", total); // Total amount to be paid
            model.addAttribute("shippingFee", shippingFee);

            // Add QR code to model if it's generated
            if (qrCodeBase64 != null) {
                model.addAttribute("qrCodeBase64", qrCodeBase64);
            }

            return "cart/checkout"; // The checkout page where you want to show the QR code
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

            // Tạo URI để mở ứng dụng MB Bank với các thông tin cần thiết
            String paymentUri = "00020101021138570010A00000072701270006970422011300008003860380208QRIBFTTA53037045802VN630404fa";

            String qrCodeBase64 = null;
            try {
                // Tạo mã QR từ URI
                qrCodeBase64 = qrCodeService.generateQRCodeBase64(paymentUri);
            } catch (WriterException e) {
                // Xử lý ngoại lệ ở đây, ví dụ như log lỗi hoặc trả về một trang thông báo lỗi
                e.printStackTrace(); // In lỗi ra console
                model.addAttribute("errorMessage", "There was an error generating the QR code.");
                return "cart/error"; // Chuyển sang trang thông báo lỗi nếu cần
            }

            // Gửi mã QR vào model
            model.addAttribute("qrCodeBase64", qrCodeBase64);

            // Gửi các thông tin vào view để hiển thị
            model.addAttribute("totalProduct", totalProduct);
            model.addAttribute("shippingFee", shippingFee);
            model.addAttribute("total", total);

            // Tạo đơn hàng
            orderService.createOrder(customer_name, email, phone_number, address, payment_method, note, cartItems);

            // Chuyển tới trang xác nhận đơn hàng
            return "cart/order-confirmation";
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
