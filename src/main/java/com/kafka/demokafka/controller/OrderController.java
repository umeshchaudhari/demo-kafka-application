package com.kafka.demokafka.controller;

import com.kafka.demokafka.dto.EncryptedRequest;
import com.kafka.demokafka.dto.EncryptedResponse;
import com.kafka.demokafka.entity.OrderEntity;
import com.kafka.demokafka.model.OrderEvent;
import com.kafka.demokafka.producer.OrderProducer;
import com.kafka.demokafka.service.OrderService;
import com.kafka.demokafka.utility.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderProducer orderProducer;
    private final OrderService orderService;
    private final EncryptionUtil encryptionUtil;
    private final ObjectMapper objectMapper;

    public OrderController(OrderProducer orderProducer, OrderService orderService, EncryptionUtil encryptionUtil, ObjectMapper objectMapper) {
        this.orderProducer = orderProducer;
        this.orderService = orderService;
        this.encryptionUtil = encryptionUtil;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<EncryptedResponse> createOrder(@RequestBody EncryptedRequest request){
        try {
            //log.info("Inside the createOrder Method :: ");
            String decryptedJson =
                    encryptionUtil.decrypt(request.getPayload());

            OrderEvent event =
                    objectMapper.readValue(
                            decryptedJson,
                            OrderEvent.class
                    );

            orderProducer.sendOrderEvent(event);
            Map<String,Object> response = new HashMap<>();
            response.put("status","SUCCESS");
            response.put("message","Order sent successfully to Kafka");
            response.put("orderId", event.getOrderId());
            //log.info("Print the Response :: " +response);
            return encryptedResponse(response);

        } catch (RuntimeException e) {
            //log.error("Error while creating order", e);
            Map<String,Object> response = new HashMap<>();
            response.put("status","FAILED");
            response.put("message","Kafka is not start.Pls start and try again.");

            return encryptedResponse(
                    response,
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        } catch (Exception e) {

            log.error("Error decrypting order request", e);

            Map<String, Object> response = new HashMap<>();

            response.put("status", "FAILED");
            response.put(
                    "message",
                    "Invalid encrypted request"
            );

            return encryptedResponse(
                    response,
                    HttpStatus.BAD_REQUEST
            );
        }

    }

    //Get Orders
    @GetMapping
    public ResponseEntity<EncryptedResponse > getOrder(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            String customerName,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount)
    {
        try {
            Page<OrderEntity> orders = orderService.getOrders(
                    page,size,customerName,minAmount,maxAmount
            );
            return encryptedResponse(orders);
        } catch (Exception e) {
            log.error("Error while fetching orders", e);

            return encryptedResponse(
                    Map.of(
                            "status", "FAILED",
                            "message", "Unable to fetch orders"
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        //log.info("Print the Orders :: " +orders);
    }
    //Total Count
    @GetMapping("/count")
    public ResponseEntity<EncryptedResponse> getTotalOrderCount(){
        //log.info("Inside the getTotalOrderCount Method :: ");
        try {
            Long count = orderService.getTotalCount();
            return encryptedResponse(Map.of("count", count));
        } catch (Exception e) {
            log.error("Error while getting order count", e);

            return encryptedResponse(
                    Map.of("status", "FAILED", "message", "Unable to get order count"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }
    //Daily Count
    @GetMapping("/count/daily")
    public ResponseEntity<EncryptedResponse> getDailyOrderCount(){
        //log.info("Inside the getDailyOrderCount Method :: ");
        try {
            List<Map<String, Object>> dailyCount =
                    orderService.getDailyOrderCount();
            return encryptedResponse(dailyCount);
        } catch (Exception e) {
            log.error("Error while getting daily order count", e);

            return encryptedResponse(
                    Map.of(
                            "status", "FAILED",
                            "message",
                            "Unable to get daily order count"
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }

    // =========================================================
    // ENCRYPT RESPONSE - 200 OK
    // =========================================================

    private ResponseEntity<EncryptedResponse> encryptedResponse(
            Object responseObject) throws Exception {

        String responseJson =
                objectMapper.writeValueAsString(responseObject);

        String encryptedData =
                encryptionUtil.encrypt(responseJson);

        return ResponseEntity.ok(
                new EncryptedResponse(encryptedData)
        );
    }

    // =========================================================
    // ENCRYPT RESPONSE - CUSTOM STATUS
    // =========================================================

    private ResponseEntity<EncryptedResponse> encryptedResponse(
            Object responseObject,
            HttpStatus status) {

        try {

            String responseJson =
                    objectMapper.writeValueAsString(responseObject);

            String encryptedData =
                    encryptionUtil.encrypt(responseJson);

            return ResponseEntity
                    .status(status)
                    .body(new EncryptedResponse(encryptedData));

        } catch (Exception e) {

            log.error("Error encrypting response", e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
    //Daily Count
//    @GetMapping("/count/daily")
//    public Map<LocalDate,Long> getDailyCount(){
//        return orderService.getDailyOrderCount();
//    }
}
