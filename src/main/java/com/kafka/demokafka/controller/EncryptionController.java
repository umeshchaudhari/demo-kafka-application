package com.kafka.demokafka.controller;

import com.kafka.demokafka.dto.EncryptedRequest;
import com.kafka.demokafka.dto.EncryptedResponse;
import com.kafka.demokafka.utility.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/encryption")
public class EncryptionController {

    private final EncryptionUtil encryptionUtil;
    private final ObjectMapper objectMapper;

    public EncryptionController(
            EncryptionUtil encryptionUtil,
            ObjectMapper objectMapper) {
        this.encryptionUtil = encryptionUtil;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/encrypt")
    public ResponseEntity<EncryptedResponse> encrypt(
            @RequestBody JsonNode request) {

//        log.info("Inside the encrypt API Method {} ");

        try {
            // Convert incoming JSON to String
            String json = objectMapper.writeValueAsString(request);

            // Encrypt
            String encryptedData =
                    encryptionUtil.encrypt(json);

            return ResponseEntity.ok(
                    new EncryptedResponse(encryptedData)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<?> decrypt(
            @RequestBody EncryptedRequest request) {

        try {
            // Decrypt
            String decryptedJson =
                    encryptionUtil.decrypt(request.getData());

            // Convert decrypted JSON back to JSON object
            JsonNode jsonNode =
                    objectMapper.readTree(decryptedJson);

            return ResponseEntity.ok(jsonNode);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Unable to decrypt payload"
                    ));
        }
    }
}
