package com.kafka.demokafka.service;

import com.kafka.demokafka.entity.OrderEntity;
import com.kafka.demokafka.model.OrderEvent;
import com.kafka.demokafka.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    @Transactional
    public void saveOrder(OrderEvent event){

        //Idempotency
        if(orderRepository.existsByOrderId(event.getOrderId())){
            //log.info("Order already exists : {}", event.getOrderId());
            return;
        }

        OrderEntity order = new OrderEntity(
                event.getOrderId(),
                event.getCustomerName(),
                event.getAmount()
        );

        orderRepository.save(order);
        //log.info("Order saved successfully: {}", event.getOrderId());
    }

    //Get Order with Paginations
    public Page<OrderEntity> getOrders(int page , int size,
                                       String customerName,Double minAmount,
                                       Double maxAmount){
        String searchPattern = (customerName != null && !customerName.trim().isEmpty())
                ? "%" + customerName.trim() + "%"
                : null;
        Pageable pageable = PageRequest.of(page,size);

        return orderRepository.findOrders(
                searchPattern,
                minAmount,
                maxAmount,
                pageable
        );
    }

    //Total Order
    public long getTotalCount(){
        return orderRepository.count();
    }

    //daily Count
    public List<Map<String,Object>> getDailyOrderCount(){
        List<Object[]> result = orderRepository.findDailyCount();
        //log.info("Inside the getDailyOrderCount method :: ");
        //Stream Api is used only to transform
        // small query result into response format
        return result.stream().map(row->{
            Map<String,Object> data = new HashMap<>();
            data.put("date",row[0]);
            data.put("count", row[1]);
            return data;
        }).toList();
    }
}