package com.webhotel.webhotel.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.webhotel.webhotel.dto.*;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.UserMapper;
import com.webhotel.webhotel.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("john");
        dto.setEmail("john@example.com");
        dto.setPassword("password");

        User user = new User();
        User savedUser = new User();
        UserDto userDto = new UserDto();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(userMapper.registerToUser(dto)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toUserDto(savedUser)).thenReturn(userDto);

        UserDto result = userService.register(dto);

        assertEquals(userDto, result);
        verify(userRepository).save(user);
        assertEquals("encoded", user.getPassword());
    }

    @Test
    void register_emailConflict() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("john");
        dto.setEmail("john@example.com");
        dto.setPassword("password");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.register(dto));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void register_usernameConflict() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("john");
        dto.setEmail("john@example.com");
        dto.setPassword("password");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.register(dto));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void login_success() {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");
        dto.setPassword("password");

        User user = new User();
        user.setPassword("encoded");
        UserDto userDto = new UserDto();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.login(dto);

        assertEquals(userDto, result);
    }

    @Test
    void login_userNotFound() {
        LoginDto dto = new LoginDto();
        dto.setEmail("notfound@example.com");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.login(dto));
    }

    @Test
    void login_invalidPassword() {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");
        dto.setPassword("wrongpass");

        User user = new User();
        user.setPassword("encoded");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encoded")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.login(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void findByUsername_success() {
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(dto);

        UserDto result = userService.findByUsername("john");

        assertEquals(dto, result);
    }

    @Test
    void findByUsername_notFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername("john"));
    }

    @Test
    void createUserFromOAuth_success() {
        String email = "new@example.com";
        String username = "newuser";
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(dto);

        UserDto result = userService.createUserFromOAuth(username, email);

        assertEquals(dto, result);
    }

    @Test
    void createUserFromOAuth_emailConflict() {
        when(userRepository.findByEmail("e@x.com")).thenReturn(Optional.of(new User()));

        assertThrows(ResponseStatusException.class,
                () -> userService.createUserFromOAuth("any", "e@x.com"));
    }

    @Test
    void createUserFromOAuth_usernameConflict() {
        when(userRepository.findByEmail("e@x.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));

        assertThrows(ResponseStatusException.class,
                () -> userService.createUserFromOAuth("user", "e@x.com"));
    }

    @Test
    void getCurrentUsernameFromToken_success() {
        String expectedUsername = "testuser";
        Authentication auth = new UsernamePasswordAuthenticationToken(expectedUsername, null);
        SecurityContext context = mock(SecurityContext.class);

        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        String result = userService.getCurrentUsernameFromToken();

        assertEquals(expectedUsername, result);
    }
}
