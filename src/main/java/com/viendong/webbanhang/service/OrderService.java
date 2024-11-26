package com.viendong.webbanhang.service;

import com.viendong.webbanhang.model.CartItem;
import com.viendong.webbanhang.model.Order;
import com.viendong.webbanhang.model.OrderDetail;
import com.viendong.webbanhang.repository.OrderDetailRepository;
import com.viendong.webbanhang.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;

    @Transactional
    public Order createOrder(String customer_name, String email, String phone_number,
                             String address, String payment_method, String note, List<CartItem> cartItems) {
        // Create the Order
        Order order = new Order();
        order.setCustomer_name(customer_name);
        order.setEmail(email);
        order.setPhone_number(phone_number);
        order.setAddress(address);
        order.setPayment_method(payment_method);
        order.setNote(note);

        // Save the Order first
        order = orderRepository.save(order);

        // Tính tổng tiền sản phẩm và tổng giá trị
        double totalProduct = 0;
        double total = 0;

        for (CartItem cartItem : cartItems) {
            OrderDetail detail = new OrderDetail();
            double productPrice = cartItem.getProduct().getPrice();
            totalProduct += productPrice * cartItem.getQuantity();

            double shippingFee = cartService.getShippingFee(); // Lấy phí vận chuyển
            total = totalProduct + shippingFee; // Tổng giá trị đơn hàng (sản phẩm + phí vận chuyển)

            detail.setOrder(order); // Set the saved Order
            detail.setProduct(cartItem.getProduct());
            detail.setQuantity(cartItem.getQuantity());
            detail.setPrice(total);  // Set price for the product in the order detail

            // Tính tổng tiền sản phẩm

            // Save the order detail
            orderDetailRepository.save(detail);
        }

        // Lấy phí vận chuyển và tính tổng


        // Gửi tổng giá trị vào đơn hàng
        order.setTotalPrice(total); // Đảm bảo rằng bạn có setter cho thuộc tính này trong Order entity.

        // Clear the cart after saving the order
        cartService.clearCart();
        return order;
    }


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id){
        if(!orderRepository.existsById(id)){
            throw new IllegalStateException("Product with Id " + id + " not found");
        }
        orderRepository.deleteById(id);
    }
}
