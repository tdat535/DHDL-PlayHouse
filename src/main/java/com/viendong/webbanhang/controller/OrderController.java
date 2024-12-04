    package com.viendong.webbanhang.controller;

    import com.viendong.webbanhang.model.CartItem;
    import com.viendong.webbanhang.model.Order;
    import com.viendong.webbanhang.model.Product;
    import com.viendong.webbanhang.model.User;
    import com.viendong.webbanhang.repository.ProductRepository;
    import com.viendong.webbanhang.repository.UserRepository;
    import com.viendong.webbanhang.service.*;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import com.google.zxing.WriterException;
    import com.google.zxing.common.BitMatrix;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    import java.util.List;

    @Controller
    @RequestMapping("/order")
    public class OrderController {

        private final OrderService orderService;
        private final CartService cartService;
        private final QRCodeService qrCodeService; // Add this line to inject the QRCodeService

        private final EmailService emailService;
        private final UserRepository userRepository;
        private final ProductRepository productRepository;

        @Autowired
        private ProductService productService;
        @Autowired
        private CategoryService categoryService;
        @Autowired
        private BrandService brandService;

        @Autowired
        public OrderController(OrderService orderService, CartService cartService, QRCodeService qrCodeService, EmailService emailService, UserRepository userRepository, ProductRepository productRepository) {
            this.orderService = orderService;
            this.cartService = cartService;
            this.qrCodeService = qrCodeService;
            this.emailService = emailService;
            this.userRepository = userRepository;
            this.productRepository = productRepository;
        }

        @GetMapping("/cart")
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

        @GetMapping("/checkout")
        public String checkout(Model model, RedirectAttributes redirectAttributes) {
            String currentUsername = getCurrentUsername();
            User currentUser = userRepository.findByUsername(currentUsername);
            model.addAttribute("realName_user", currentUser.getRealName());
            model.addAttribute("phone_user", currentUser.getPhone());
            model.addAttribute("email_user", currentUser.getEmail());
            model.addAttribute("address_user", currentUser.getAddress());

            double totalProduct = cartService.calculateTotalProduct();
            double total = cartService.calculateTotal();
            double shippingFee = cartService.getShippingFee();

            // Lấy các mục trong giỏ hàng
            List<CartItem> cartItems = cartService.getCartItems();
            if (cartItems.isEmpty()) {
                return "redirect:/order/cart"; // Nếu giỏ hàng trống
            }

            // Kiểm tra và giảm số lượng sản phẩm trong giỏ
            for (CartItem item : cartItems) {
                Long productId = item.getProduct().getId();
                int quantity = item.getQuantity();

                // Nếu không đủ hàng
                if (!productService.hasEnoughQuantity(productId, quantity)) {
                    redirectAttributes.addFlashAttribute("error",
                            "Sản phẩm '" + item.getProduct().getName() + "' không đủ số lượng.");
                    return "redirect:/order/cart";
                }

                // Nếu chỉ còn 1 sản phẩm trong kho
                if (productService.isQuantityTooLow(productId, 1)) {
                    redirectAttributes.addFlashAttribute("error",
                            "Sản phẩm '" + item.getProduct().getName() + "' không thể bán do số lượng trong kho quá thấp.");
                    return "redirect:/order/cart";
                }

                // Thực hiện giảm số lượng ngay tại bước checkout
                productService.reduceQuantity(productId, quantity);
                System.out.println("Giảm số lượng sản phẩm: " + item.getProduct().getName() +
                        " - Số lượng còn lại: " + (item.getProduct().getQuantity() - quantity));
            }

            // Thêm các dữ liệu vào model
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalProduct", totalProduct);
            model.addAttribute("total", total);
            model.addAttribute("shippingFee", shippingFee);
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrand());

            // Tạo QR Code
            String paymentUri = "00020101021138570010A00000072701270006970422011300008003860380208QRIBFTTA53037045802VN630404fa";
            try {
                String qrCodeBase64 = qrCodeService.generateQRCodeBase64(paymentUri);
                model.addAttribute("qrCodeBase64", qrCodeBase64);
            } catch (WriterException e) {
                e.printStackTrace();
                model.addAttribute("errorMessage", "Có lỗi khi tạo mã QR.");
            }

            return "cart/checkout";
        }


        @PostMapping("/submit")
        public String submitOrder(@RequestParam String customer_name,
                                  @RequestParam String email,
                                  @RequestParam String phone_number,
                                  @RequestParam String address,
                                  @RequestParam String payment_method,
                                  @RequestParam(required = false, defaultValue = "") String note,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

            // Kiểm tra nếu giỏ hàng trống
            List<CartItem> cartItems = cartService.getCartItems();
            if (cartItems.isEmpty()) {
                return "redirect:/order/cart"; // Nếu giỏ hàng trống, chuyển về trang giỏ hàng
            }

            // Tính toán tổng tiền
            double totalProduct = cartService.calculateTotalProduct(); // Tính tổng sản phẩm
            double shippingFee = cartService.getShippingFee(); // Lấy phí vận chuyển
            double total = totalProduct + shippingFee; // Tổng tiền (sản phẩm + vận chuyển)

            // Gửi các thông tin vào view để hiển thị
            model.addAttribute("totalProduct", totalProduct);
            model.addAttribute("shippingFee", shippingFee);
            model.addAttribute("total", total);

            // Tạo đơn hàng
            orderService.createOrder(customer_name, email, phone_number, address, payment_method, note, cartItems);

            // Trừ số lượng sản phẩm sau khi đặt hàng thành công
            for (CartItem item : cartItems) {
                Long productId = item.getProduct().getId();
                int quantity = item.getQuantity();

                // Kiểm tra nếu số lượng trong kho không đủ
                if (!productService.hasEnoughQuantity(productId, quantity)) {
                    redirectAttributes.addFlashAttribute("error", "Sản phẩm '" + item.getProduct().getName() + "' không đủ số lượng trong kho.");
                    return "redirect:/order/cart"; // Nếu không đủ, quay lại trang giỏ hàng
                }

                // Debug log: Kiểm tra trước khi giảm số lượng
                System.out.println("Trước khi giảm số lượng: " + item.getProduct().getName() + " - Số lượng: " + item.getProduct().getQuantity());

                // Giảm số lượng sản phẩm
                productService.reduceQuantity(productId, quantity);

                // Debug log: Kiểm tra sau khi giảm số lượng
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    System.out.println("Sau khi giảm số lượng: " + product.getName() + " - Số lượng: " + product.getQuantity());
                }
            }

            // Gửi email xác nhận đơn hàng tới email của user hiện tại
            String currentUsername = getCurrentUsername();
            if (currentUsername != null) {
                // Lấy thông tin user từ cơ sở dữ liệu
                User currentUser = userRepository.findByUsername(currentUsername);
                if (currentUser != null && currentUser.getEmail() != null) {
                    String userEmail = currentUser.getEmail();

                    // Nội dung email xác nhận đơn hàng
                    String subject = "Order Confirmation";
                    String body = "Dear " + customer_name + ",\n\n" +
                            "Thank you for your purchase!\n" +
                            "Your order has been successfully placed.\n\n" +
                            "Order Details:\n" +
                            "Total Products: " + totalProduct + "\n" +
                            "Shipping Fee: " + shippingFee + "\n" +
                            "Total: " + total + "\n\n" +
                            "We hope to serve you again soon!\n";

                    // Gửi email
                    emailService.sendOrderConfirmation(userEmail, subject, body);

                    redirectAttributes.addFlashAttribute("message", "Order placed successfully. Confirmation email sent to " + userEmail);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Failed to retrieve user email.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to identify the current user.");
            }

            // Chuyển tới trang xác nhận đơn hàng
            return "redirect:/order/cart";
        }



        private String getCurrentUsername() {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            return null;
        }

        @GetMapping("/confirmation")
        public String confirmation(Model model) {
            model.addAttribute("message", "Your order has been confirmed.");
            return "cart/order-confirmation";
        }


    }
