package com.kafka.demokafka.service;

import com.kafka.demokafka.config.JwtService;
import com.kafka.demokafka.dto.LoginRequestDto;
import com.kafka.demokafka.entity.RegisterRequest;
import com.kafka.demokafka.entity.UserEntity;
import com.kafka.demokafka.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest registerRequest){
//        log.info("Inside the register method :: {}",registerRequest);
        if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            throw new RuntimeException("Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        user.setRole("USER");
        //userRepository.save(user);
        UserEntity savedUser = userRepository.save(user);
        userRepository.flush();

//        log.info(
//                "User saved successfully with ID: {}",
//                savedUser.getId()
//        );
    }

    public String login(LoginRequestDto requestDto){
        authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(requestDto.getUsername());

        return jwtService.generateToken(userDetails);
    }

}
