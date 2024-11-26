package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserManagementController {
    private final UserService userService;
    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "15") int size,
                            Model model) {
        page = Math.max(0, (page + 1) - 1);
        Page<User> usersPage = userService.findUsersWithPagination(PageRequest.of(page, size));
        List<User> users = userService.findAllUsers();
        long UserCount=userService.countUser();
        model.addAttribute("UserCount", UserCount);
        model.addAttribute("users", users);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        return "admin/user-list"; // Trả về view danh sách người dùng
    }
}
