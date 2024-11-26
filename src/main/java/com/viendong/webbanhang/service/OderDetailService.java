package com.viendong.webbanhang.service;
import com.viendong.webbanhang.model.Brand;
import com.viendong.webbanhang.model.Category;
import com.viendong.webbanhang.model.OrderDetail;
import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.repository.OrderDetailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OderDetailService {
   private final OrderDetailRepository orderDetailRepository;

    public long countorderDetail() {
        return orderDetailRepository.count();
    }
    public List<OrderDetail> getAllOrder_Details() {
        return orderDetailRepository.findAll();
    }

    public Optional<OrderDetail> orderDetailById(Long id) {
        return orderDetailRepository.findById(id);
    }
    public double calculateTotalRevenue() {
        List<OrderDetail> orders = orderDetailRepository.findAll();
        return orders.stream()
                .mapToDouble(OrderDetail::getTotalPrice)
                .sum();
    }
    public Page<OrderDetail> findUsersWithPagination(Pageable pageable) {
        return orderDetailRepository.findAll(pageable);
    }
}
