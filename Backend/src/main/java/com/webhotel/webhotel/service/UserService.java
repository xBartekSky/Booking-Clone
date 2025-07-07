package com.webhotel.webhotel.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import com.webhotel.webhotel.dto.LoginDto;
import com.webhotel.webhotel.dto.RegisterDto;
import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.UserMapper;
import com.webhotel.webhotel.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserDto register(RegisterDto registerDto) {
        Optional<User> optionalUser = userRepository.findByEmail(registerDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        optionalUser = userRepository.findByUsername(registerDto.getUsername());
        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }

        User user = userMapper.registerToUser(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    public UserDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
    }

    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toUserDto(user);
    }

    public UserDto createUserFromOAuth(String username, String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");

        }

        optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    public String getCurrentUsernameFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
