package com.webhotel.webhotel.mapper;

import com.webhotel.webhotel.dto.ReviewDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.Review;
import com.webhotel.webhotel.entity.User;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewMapperTest {

    private final ReviewMapper reviewMapper = Mappers.getMapper(ReviewMapper.class);

    @Test
    void testReviewToDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");

        Hotel hotel = new Hotel();
        hotel.setId(2L);

        Review review = new Review();
        review.setId(100L);
        review.setRating(4.5);
        review.setComment("Great stay!");
        review.setUser(user);
        review.setHotel(hotel);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        ReviewDto dto = reviewMapper.ReviewtoDto(review);

        assertEquals(review.getId(), dto.getId());
        assertEquals(review.getRating(), dto.getRating());
        assertEquals(review.getComment(), dto.getComment());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(user.getUsername(), dto.getUsername());
    }

    @Test
    void testReviewToDtoList() {
        User user = new User();
        user.setId(1L);
        user.setUsername("jane_doe");

        Hotel hotel = new Hotel();
        hotel.setId(2L);

        Review review1 = new Review();
        review1.setId(101L);
        review1.setRating(5.0);
        review1.setComment("Amazing!");
        review1.setUser(user);
        review1.setHotel(hotel);

        Review review2 = new Review();
        review2.setId(102L);
        review2.setRating(3.0);
        review2.setComment("Okayish.");
        review2.setUser(user);
        review2.setHotel(hotel);

        List<ReviewDto> dtoList = reviewMapper.ReviewtoDtoList(List.of(review1, review2));

        assertEquals(2, dtoList.size());
        assertEquals(101L, dtoList.get(0).getId());
        assertEquals(102L, dtoList.get(1).getId());
    }

    @Test
    void testDtoToEntity() {
        ReviewDto dto = new ReviewDto();
        dto.setId(999L); // should be ignored
        dto.setRating(4.8);
        dto.setComment("Fantastic stay!");
        dto.setUserId(42L); // ignored in mapper
        dto.setUsername("ignored"); // ignored in mapper

        Review review = reviewMapper.toEntity(dto);

        assertNull(review.getId());
        assertEquals(dto.getRating(), review.getRating());
        assertEquals(dto.getComment(), review.getComment());
        assertNull(review.getUser());
        assertNull(review.getHotel());
        assertNull(review.getCreatedAt());
        assertNull(review.getUpdatedAt());
    }
}
