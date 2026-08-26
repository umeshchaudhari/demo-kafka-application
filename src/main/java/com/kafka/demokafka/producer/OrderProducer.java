package com.kafka.demokafka.producer;

import com.kafka.demokafka.model.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderProducer {

    private static final String ORDER_TOPIC = "orders";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderEvent orderEvent){
        String orderId = UUID.randomUUID().toString();
        orderEvent.setOrderId(orderId);
        try{
            kafkaTemplate.send(ORDER_TOPIC,orderId,orderEvent).get();
        } catch (Exception e) {
            throw new RuntimeException("Kafka is not start.Please start kafka and try again."
                    ,e);
        }

    }
}
