package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.service.OderDetailService;
import com.viendong.webbanhang.service.ProductService;
import com.viendong.webbanhang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private ProductService productService;
    private final UserService userService;
    private final OderDetailService oderDetailService;

    public DashboardController(ProductService productService,UserService userService, OderDetailService oderDetailService) {
        this.productService = productService;
        this.userService = userService;
        this.oderDetailService = oderDetailService;
    }

    @GetMapping()
    public String showDashboard(Model model) {
        long productCount = productService.countProducts();
        int totalQuantitySold = productService.calculateTotalQuantitySold();
        double totalRevenue = oderDetailService.calculateTotalRevenue();
        long userCount = userService.countUser();
        long ordercount = oderDetailService.countorderDetail();
        model.addAttribute("ordercount", ordercount);
        model.addAttribute("productCount", productCount);
        model.addAttribute("totalQuantity", totalQuantitySold);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("userCount",userCount);
        return "/dashboard/dashboard-list";
    }
}
