package com.viendong.webbanhang.controller;

import com.viendong.webbanhang.model.OrderDetail;
import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.service.OderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderDetailController {
    @Autowired
    private final OderDetailService oderDetailService;

    @GetMapping("/api/order-details/{id}")
    @ResponseBody
    public OrderDetail getOrderDetailById(@PathVariable Long id) {
        return oderDetailService.orderDetailById(id).orElseThrow(() -> new RuntimeException("OrderDetail not found"));
    }
    @GetMapping("/oder_details")
    public String listOderDetails(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "15") int size,
                            Model model) {
        page = Math.max(0, (page + 1) - 1);
        Page<OrderDetail> orderDetailPage = oderDetailService.findUsersWithPagination(PageRequest.of(page, size));
        List<OrderDetail> order_details  = oderDetailService.getAllOrder_Details();
        long OrderCount=oderDetailService.countorderDetail();
        model.addAttribute("OrderCount", OrderCount);
        model.addAttribute("order_details", order_details);
        model.addAttribute("orderDetailPage", orderDetailPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderDetailPage.getTotalPages());
        return "/oder_details/oderDetails-list"; // Trả về view danh sách người dùng
    }

}
