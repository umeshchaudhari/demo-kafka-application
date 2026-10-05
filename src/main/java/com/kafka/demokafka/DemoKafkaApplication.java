package com.kafka.demokafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
public class DemoKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoKafkaApplication.class, args);

//        byte[] key = new byte[32];
//        new SecureRandom().nextBytes(key);
//
//        System.out.println(Base64.getEncoder().encodeToString(key));

    }

}
