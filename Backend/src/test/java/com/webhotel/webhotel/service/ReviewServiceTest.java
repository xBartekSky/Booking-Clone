package com.webhotel.webhotel.service;

import com.webhotel.webhotel.dto.ReviewDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.Review;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.ReviewMapper;
import com.webhotel.webhotel.repository.HotelRepository;
import com.webhotel.webhotel.repository.ReviewRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelService hotelService;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private Hotel hotel;
    private User user;
    private Review review;
    private ReviewDto reviewDto;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        hotel = new Hotel();
        hotel.setId(10L);
        hotel.setRating(0.0);

        review = new Review();
        review.setId(100L);
        review.setHotel(hotel);
        review.setUser(user);

        reviewDto = new ReviewDto();
        reviewDto.setId(100L);
        reviewDto.setComment("Great stay!");
        reviewDto.setRating(5.0);
    }

    @Test
    void testAddReviewSuccess() {
        when(hotelRepository.findById(10L)).thenReturn(Optional.of(hotel));
        when(reviewMapper.toEntity(reviewDto)).thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(hotelService.getHotelAverageRating(10L)).thenReturn(4.5);
        when(reviewMapper.ReviewtoDto(review)).thenReturn(reviewDto);

        ReviewDto result = reviewService.addReview(10L, user, reviewDto);

        assertNotNull(result);
        assertEquals(reviewDto, result);
        assertEquals(user, review.getUser());
        assertEquals(hotel, review.getHotel());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());

        verify(hotelRepository).save(hotel);
        assertEquals(4.5, hotel.getRating());
    }

    @Test
    void testAddReviewHotelNotFound() {
        when(hotelRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addReview(10L, user, reviewDto));

        assertEquals("Hotel not found", ex.getMessage());
    }

    @Test
    void testGetReviewsByHotelId() {
        List<Review> reviews = List.of(review);
        List<ReviewDto> reviewDtos = List.of(reviewDto);

        when(reviewRepository.findByHotelId(10L)).thenReturn(reviews);
        when(reviewMapper.ReviewtoDtoList(reviews)).thenReturn(reviewDtos);

        List<ReviewDto> result = reviewService.getReviewsByHotelId(10L);

        assertEquals(1, result.size());
        assertEquals(reviewDto, result.get(0));
    }
}
