package com.kafka.demokafka.controller;

import com.kafka.demokafka.dto.EncryptedRequest;
import com.kafka.demokafka.dto.EncryptedResponse;
import com.kafka.demokafka.dto.LoginRequestDto;
import com.kafka.demokafka.entity.RegisterRequest;
import com.kafka.demokafka.repository.UserRepository;
import com.kafka.demokafka.service.AuthService;
import com.kafka.demokafka.utility.EncryptionUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final EncryptionUtil encryptionUtil;
    private final ObjectMapper objectMapper;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService, EncryptionUtil encryptionUtil, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.encryptionUtil = encryptionUtil;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<EncryptedResponse> register(@RequestBody EncryptedRequest request) throws Exception{

        try {
            String decryptedJson = encryptionUtil.decrypt(request.getPayload());
            RegisterRequest registerRequest = objectMapper.readValue(decryptedJson,RegisterRequest.class);

            if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
                return encryptedResponse(
                        Map.of("message", "Username already exists")
                );
            }

            authService.register(registerRequest);

//            String responseJson = objectMapper.writeValueAsString(Map.of(
//                    "message",
//                    "User registered succesfully"
//            ));

            //String encryptedResponse = encryptionUtil.encrypt(responseJson);

            return encryptedResponse(
                    Map.of("message", "User registered successfully")
            );
        } catch (Exception e){
            return encryptedResponse(
                    Map.of("message", "Invalid encrypted request")
            );
        }



    }

    @PostMapping("/login")
    public ResponseEntity<EncryptedResponse> login(@RequestBody EncryptedRequest  request) throws Exception {

        try {
            String decryptedJson =
                    encryptionUtil.decrypt(request.getPayload());

            LoginRequestDto loginRequest =
                    objectMapper.readValue(
                            decryptedJson,
                            LoginRequestDto.class
                    );
            String token = authService.login(loginRequest);

            return encryptedResponse(
                    Map.of("token", token)
            );
        }catch (Exception e) {
            return encryptedResponse(
                    Map.of("message", "Invalid credentials")
            );
        }


//        Map<String, String> response = new HashMap<>();
//        response.put("token",token);
//        return ResponseEntity.ok(response);
    }

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
}
