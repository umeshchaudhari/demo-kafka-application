package com.kafka.demokafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;

public class KafkaTopicConfig {

    @Bean
    public NewTopic ordersTopic(){
        return new NewTopic(
                "orders",
                1,
                (short) 1
        );
    }
}
