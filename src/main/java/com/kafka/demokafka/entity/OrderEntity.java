package com.kafka.demokafka.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders",uniqueConstraints = {@UniqueConstraint(name = "uk_order_id",columnNames = "order_id")})
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id",unique = true)
    private String orderId;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "order_amount")
    private Double amount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public OrderEntity() {
    }

    public OrderEntity(String orderId, String customerName, Double amount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
