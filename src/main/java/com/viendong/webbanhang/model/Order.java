package com.viendong.webbanhang.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customer_name;
    private String email;
    private String phone_number;
    private String address;
    private String payment_method;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    private String note;
    private double totalPrice;  // Thêm trường tổng giá trị đơn hàng

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public String getNote() {
        return note;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail() {
        return email;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public Long getId() {
        return id;
    }

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
