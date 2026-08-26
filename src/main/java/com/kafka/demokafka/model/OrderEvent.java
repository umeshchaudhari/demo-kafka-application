package com.kafka.demokafka.model;

public class OrderEvent {

    private String orderId;
    private String customerName;
    private Double amount;

    public OrderEvent() {
    }

    public OrderEvent(String orderId, String customerName, Double amount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
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
}
