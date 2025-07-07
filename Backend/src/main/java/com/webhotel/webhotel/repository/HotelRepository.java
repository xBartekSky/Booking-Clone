package com.webhotel.webhotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.webhotel.webhotel.entity.Hotel;

public interface HotelRepository extends CrudRepository<Hotel, Integer> {

    Optional<Hotel> findById(Long id);

    List<Hotel> findByNameIgnoreCaseContaining(String name);

    List<Hotel> findByCityIgnoreCaseContaining(String city);

    List<Hotel> findByOwnerId(Long ownerId);

    boolean existsById(Long id);

    void deleteById(Long id);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotel.id = :hotelId")
    Double findAverageRating(@Param("hotelId") Long hotelId);

}
