package com.webhotel.webhotel.service;

import com.webhotel.webhotel.dto.ReviewDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.Review;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.ReviewMapper;
import com.webhotel.webhotel.repository.HotelRepository;
import com.webhotel.webhotel.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private ReviewMapper reviewMapper;

    public ReviewDto addReview(Long hotelId, User user, ReviewDto reviewDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        Review review = reviewMapper.toEntity(reviewDto);
        review.setHotel(hotel);
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);

        Double rating = hotelService.getHotelAverageRating(hotel.getId());
        hotel.setRating(rating);

        hotelRepository.save(hotel);

        return reviewMapper.ReviewtoDto(saved);
    }

    public List<ReviewDto> getReviewsByHotelId(Long hotelId) {
        List<Review> reviews = reviewRepository.findByHotelId(hotelId);
        return reviewMapper.ReviewtoDtoList(reviews);
    }
}
