package com.viendong.webbanhang.controller;
import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.service.EmailService;
import com.viendong.webbanhang.service.OderDetailService;
import com.viendong.webbanhang.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/authentication")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public String login() {
        return "authentication/login"; // Return the login view
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User()); // Add a new User object to the model
        return "authentication/register"; // Return the register view
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           @NotNull BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors); // Add errors to the model
            return "authentication/register"; // Return the register view with errors
        }
        userService.save(user); // Save the user to the database
        userService.setDefaultRole(user.getUsername()); // Set default role
        return "redirect:/authentication/login"; // Redirect to login page
    }

    @GetMapping("/recover")
    public String recover() {
        return "authentication/recover";
    }

    @PostMapping("/sendVerificationCode")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendVerificationCode(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        String email = request.get("email");

        // Kiểm tra xem email có tồn tại trong cơ sở dữ liệu không
        boolean emailExists = userService.checkEmailExists(email);  // Ví dụ phương thức kiểm tra email

        if (!emailExists) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Email does not exist. Please check the email and try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Nếu email tồn tại, tạo mã xác minh (có thể thay UUID bằng mã ngắn)
        String verificationCode = UUID.randomUUID().toString();  // Mã xác minh ngẫu nhiên

        try {
            // Gửi email chứa mã xác minh
            String subject = "Password Recovery Verification Code";
            String text = "Your verification code is: " + verificationCode;
            emailService.sendVerifyCode(email, subject, text);

            // Lưu thông tin vào session
            session.setAttribute("verificationCode", verificationCode);
            session.setAttribute("email", email);

            // Tạo phản hồi JSON
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Verification code sent successfully to " + email);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send verification code. Please try again.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @PostMapping("/recover")
    public String recoverPassword(@RequestParam String email,
                                  @RequestParam String verificationCode,
                                  @RequestParam String password,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        // Lấy mã xác thực đã lưu trữ (ví dụ: từ session hoặc cơ sở dữ liệu)
        String storedCode = (String) session.getAttribute("verificationCode");
        String storedEmail = (String) session.getAttribute("email");

        if (storedCode == null || !storedCode.equals(verificationCode) || !storedEmail.equals(email)) {
            // Mã xác thực không hợp lệ
            redirectAttributes.addFlashAttribute("error", "Invalid verification code or email.");
            return "redirect:/authentication/recover";
        }

        try {
            // Cập nhật mật khẩu mới
            userService.updatePassword(email, password);

            // Xóa mã xác thực sau khi sử dụng
            session.removeAttribute("verificationCode");
            session.removeAttribute("email");

            redirectAttributes.addFlashAttribute("message", "Password has been successfully updated.");
            return "redirect:/authentication/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update password. Please try again.");
            return "redirect:/authentication/recover";
        }
    }

    @PostMapping("/checkEmailExistence")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkEmailExistence(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean emailExists = userService.checkEmailExists(email);  // Kiểm tra sự tồn tại của email trong cơ sở dữ liệu

        Map<String, Object> response = new HashMap<>();
        if (emailExists) {
            response.put("success", true);
        } else {
            response.put("success", false);
            response.put("message", "Email does not exist. Please check and try again.");
        }
        return ResponseEntity.ok(response);
    }



}
