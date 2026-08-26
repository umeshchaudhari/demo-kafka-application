package com.kafka.demokafka.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OrderResponse {

    private List<OrderEntity> orders;
    private int page;
    private int size;
    private long totalOrders;
    private int totalPages;
    private long todayOrderCount;
    private Map<LocalDate, Long> dailyOrderCount;

    public OrderResponse(List<OrderEntity> orders, int page, int size, long totalOrders, int totalPages, long todayOrderCount, Map<LocalDate, Long> dailyOrderCount) {
        this.orders = orders;
        this.page = page;
        this.size = size;
        this.totalOrders = totalOrders;
        this.totalPages = totalPages;
        this.todayOrderCount = todayOrderCount;
        this.dailyOrderCount = dailyOrderCount;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(long todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public Map<LocalDate, Long> getDailyOrderCount() {
        return dailyOrderCount;
    }

    public void setDailyOrderCount(Map<LocalDate, Long> dailyOrderCount) {
        this.dailyOrderCount = dailyOrderCount;
    }
}
