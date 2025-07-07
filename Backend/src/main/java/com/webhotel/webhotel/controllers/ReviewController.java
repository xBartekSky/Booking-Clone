package com.webhotel.webhotel.controllers;

import com.webhotel.webhotel.config.AuthProvider;
import com.webhotel.webhotel.dto.ReviewDto;
import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.repository.UserRepository;
import com.webhotel.webhotel.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/hotel/{hotelId}")
    public ResponseEntity<ReviewDto> addReview(
            @PathVariable Long hotelId,
            @RequestBody ReviewDto reviewDto,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ReviewDto createdReview = reviewService.addReview(hotelId, user, reviewDto);
        return ResponseEntity.ok(createdReview);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReviewDto>> getReviewsForHotel(@PathVariable Long hotelId) {
        List<ReviewDto> reviews = reviewService.getReviewsByHotelId(hotelId);
        return ResponseEntity.ok(reviews);
    }
}
