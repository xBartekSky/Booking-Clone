package com.webhotel.webhotel.controllers;

import com.webhotel.webhotel.config.AuthProvider;
import com.webhotel.webhotel.dto.ReviewDto;
import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.repository.UserRepository;
import com.webhotel.webhotel.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    private ReviewService reviewService;
    private AuthProvider authProvider;
    private UserRepository userRepository;
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        reviewService = mock(ReviewService.class);
        authProvider = mock(AuthProvider.class);
        userRepository = mock(UserRepository.class);
        reviewController = new ReviewController();

        // Use reflection to inject mocks if using field injection
        setField(reviewController, "reviewService", reviewService);
        setField(reviewController, "authProvider", authProvider);
        setField(reviewController, "userRepository", userRepository);
    }

    // Utility method for field injection
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddReview() {
        Long hotelId = 1L;
        String token = "Bearer some-token";
        String username = "testuser";

        ReviewDto inputDto = new ReviewDto();
        ReviewDto expectedDto = new ReviewDto(); // Optionally fill fields

        UserDto userDto = new UserDto();
        userDto.setUsername(username);

        Authentication authentication = mock(Authentication.class);
        when(authProvider.validateToken("some-token")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDto);

        User mockUser = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        when(reviewService.addReview(hotelId, mockUser, inputDto)).thenReturn(expectedDto);

        ResponseEntity<ReviewDto> response = reviewController.addReview(hotelId, inputDto, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    void testGetReviewsForHotel() {
        Long hotelId = 1L;
        ReviewDto review1 = new ReviewDto();
        ReviewDto review2 = new ReviewDto();

        when(reviewService.getReviewsByHotelId(hotelId)).thenReturn(List.of(review1, review2));

        ResponseEntity<List<ReviewDto>> response = reviewController.getReviewsForHotel(hotelId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(review1));
    }
}
