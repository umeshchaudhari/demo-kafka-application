package com.kafka.demokafka.producer;

import com.kafka.demokafka.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OrderProducer {

    private static final String ORDER_TOPIC = "orders";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderEvent orderEvent){
        //log.info("Inside the sendOrderEvent Method :: ");
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
