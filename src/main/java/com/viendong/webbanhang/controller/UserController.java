package com.viendong.webbanhang.controller;
import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.service.OderDetailService;
import com.viendong.webbanhang.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/authentication")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
}
