package com.kafka.demokafka.consumer;

import com.kafka.demokafka.model.OrderEvent;
import com.kafka.demokafka.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private final OrderService orderService;

    public OrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    //@KafkaListener(topics = "orders",groupId = "order-group")
    public void consumerOrder(OrderEvent orderEvent){
        orderService.saveOrder(orderEvent);
    }
}

