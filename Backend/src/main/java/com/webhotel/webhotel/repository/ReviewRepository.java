package com.webhotel.webhotel.repository;

import com.webhotel.webhotel.entity.Review;
import com.webhotel.webhotel.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHotelId(Long hotelId);

    List<Review> findByUser(User user);
}
