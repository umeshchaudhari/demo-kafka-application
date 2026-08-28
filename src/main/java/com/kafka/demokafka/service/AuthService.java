package com.kafka.demokafka.service;

import com.kafka.demokafka.config.JwtService;
import com.kafka.demokafka.dto.LoginRequestDto;
import com.kafka.demokafka.entity.RegisterRequest;
import com.kafka.demokafka.entity.UserEntity;
import com.kafka.demokafka.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            throw new RuntimeException("Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        user.setRole("USER");
        userRepository.save(user);
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
