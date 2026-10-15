package com.kafka.demokafka.controller;

import com.kafka.demokafka.dto.EncryptedRequest;
import com.kafka.demokafka.dto.EncryptedResponse;
import com.kafka.demokafka.dto.LoginRequestDto;
import com.kafka.demokafka.entity.RegisterRequest;
import com.kafka.demokafka.repository.UserRepository;
import com.kafka.demokafka.service.AuthService;
import com.kafka.demokafka.utility.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
@Slf4j
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
            if (request == null ||
                    request.getData() == null ||
                    request.getData().isBlank()) {

                return encryptedResponse(
                        Map.of("message", "Encrypted request data is required"),
                        HttpStatus.BAD_REQUEST
                );
            }

            String decryptedJson = encryptionUtil.decrypt(request.getData());
            RegisterRequest registerRequest = objectMapper.readValue(decryptedJson,RegisterRequest.class);

            // Basic validation
            if (registerRequest.getUsername() == null ||
                    registerRequest.getUsername().isBlank() ||
                    registerRequest.getPassword() == null ||
                    registerRequest.getPassword().isBlank()) {

                return encryptedResponse(
                        Map.of("message", "Username and password are required"),
                        HttpStatus.BAD_REQUEST
                );
            }
            if (userRepository.findByUsername(
                    registerRequest.getUsername()).isPresent()) {

                return encryptedResponse(
                        Map.of("message", "Username already exists"),
                        HttpStatus.CONFLICT
                );
            }

            authService.register(registerRequest);

//            String responseJson = objectMapper.writeValueAsString(Map.of(
//                    "message",
//                    "User registered succesfully"
//            ));

            //String encryptedResponse = encryptionUtil.encrypt(responseJson);

//            return encryptedResponse(
//                    Map.of("message", "User registered successfully")
//            );
//            return ResponseEntity.status(201).body(
//                    createEncryptedResponse(
//                            Map.of("message", "User registered successfully")
//                    )
//            );
            return encryptedResponse(
                    Map.of("message", "User registered successfully"),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            /*e.printStackTrace();

            return encryptedResponse(
                    Map.of("message", "Registration failed")
            );*/
//            log.error("Registration failed", e);
//
//            return ResponseEntity.status(500).body(
//                    createEncryptedResponse(
//                            Map.of("message", "Registration failed")
//                    )
//            );
            //log.error("Registration failed", e);

            try {
                return encryptedResponse(
                        Map.of("message", "Registration failed"),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            } catch (Exception encryptionException) {
                //log.error(
                        //"Failed to encrypt registration error response",
                        //encryptionException
                //);

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
            }

        }
    }
    @PostMapping("/login")
    public ResponseEntity<EncryptedResponse> login(
            @RequestBody EncryptedRequest request) {

        try {
            String decryptedJson =
                    encryptionUtil.decrypt(request.getData());

            LoginRequestDto loginRequest =
                    objectMapper.readValue(
                            decryptedJson,
                            LoginRequestDto.class
                    );

            String token = authService.login(loginRequest);

            return encryptedResponse(
                    Map.of("token", token),
                    HttpStatus.OK
            );

        } catch (Exception e) {
            //log.warn("Login failed: {}", e.getMessage());

            try {
                return encryptedResponse(
                        Map.of("message", "Invalid credentials"),
                        HttpStatus.UNAUTHORIZED
                );
            } catch (Exception encryptionException) {
//                log.error(
//                        "Failed to encrypt login response",
//                        encryptionException
//                );

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
            }
        }
    }

    /*@PostMapping("/login")
    public ResponseEntity<EncryptedResponse> login(@RequestBody EncryptedRequest  request) throws Exception {

        try {
            String decryptedJson =
                    encryptionUtil.decrypt(request.getData());

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
    }*/

//    private ResponseEntity<EncryptedResponse> encryptedResponse(
//            Object responseObject) throws Exception {
//
//        String responseJson =
//                objectMapper.writeValueAsString(responseObject);
//
//        String encryptedData =
//                encryptionUtil.encrypt(responseJson);
//
//        return ResponseEntity.ok(
//                new EncryptedResponse(encryptedData)
//        );
//    }

    private ResponseEntity<EncryptedResponse> encryptedResponse(
            Object responseObject,
            HttpStatus status) throws Exception {

        String responseJson =
                objectMapper.writeValueAsString(responseObject);

        String encryptedData =
                encryptionUtil.encrypt(responseJson);

        return ResponseEntity
                .status(status)
                .body(new EncryptedResponse(encryptedData));
    }

    private EncryptedResponse createEncryptedResponse(
            Object responseObject) throws Exception {

        String responseJson =
                objectMapper.writeValueAsString(responseObject);

        String encryptedData =
                encryptionUtil.encrypt(responseJson);

        return new EncryptedResponse(encryptedData);
    }
}
