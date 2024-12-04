package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.Order;
import com.viendong.webbanhang.model.OrderDetail;
import com.viendong.webbanhang.service.OderDetailService;
import com.viendong.webbanhang.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orderList")

public class OrderAdminController {

    private final OrderService orderService;

    @Autowired
    public OrderAdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping() // Ensure this matches the link in your templates
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        long OrderCount = orderService.countOrder();
        model.addAttribute("OrderCount", OrderCount);
        return "order/order-list"; // Thymeleaf template for listing orders
    }

    @GetMapping("/{id}") // This will handle order detail retrieval
    public String getOrder(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id).orElseThrow(() -> new IllegalStateException("Order not found"));
        model.addAttribute("order", order);
        return "order/orderDetail"; // Thymeleaf template for order details
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
        return "redirect:/order/orderList"; // Redirect after deleting an order
    }

}

