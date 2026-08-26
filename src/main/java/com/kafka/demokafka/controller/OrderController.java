package com.kafka.demokafka.controller;

import com.kafka.demokafka.entity.OrderEntity;
import com.kafka.demokafka.model.OrderEvent;
import com.kafka.demokafka.producer.OrderProducer;
import com.kafka.demokafka.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderProducer orderProducer;

    private final OrderService orderService;

    public OrderController(OrderProducer orderProducer, OrderService orderService) {
        this.orderProducer = orderProducer;
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> createOrder(@RequestBody OrderEvent event){
        try {
            orderProducer.sendOrderEvent(event);
            Map<String,Object> response = new HashMap<>();
            response.put("status","SUCCESS");
            response.put("message","Order sent successfully to Kafka");
            response.put("orderId", event.getOrderId());
            //log.info("Print the Response :: " +response);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String,Object> response = new HashMap<>();
            response.put("status","FAILED");
            response.put("message","Kafka is not start.Pls start and try again.");

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

    }

    //Get Orders
    @GetMapping
    public ResponseEntity<Page<OrderEntity>> getOrder(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            String customerName,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount)
    {
        Page<OrderEntity> orders = orderService.getOrders(
                page,size,customerName,minAmount,maxAmount
        );
        //log.info("Print the Orders :: " +orders);
        return ResponseEntity.ok(orders);
    }
    //Total Count
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalOrderCount(){
        //log.info("Inside the getTotalOrderCount Method :: ");
        return ResponseEntity.ok(orderService.getTotalCount());
    }
    //Daily Count
    @GetMapping("/count/daily")
    public ResponseEntity<List<Map<String, Object>>> getDailyOrderCount(){
        //log.info("Inside the getDailyOrderCount Method :: ");
        return ResponseEntity.ok(orderService.getDailyOrderCount());
    }
    //Daily Count
//    @GetMapping("/count/daily")
//    public Map<LocalDate,Long> getDailyCount(){
//        return orderService.getDailyOrderCount();
//    }
}
