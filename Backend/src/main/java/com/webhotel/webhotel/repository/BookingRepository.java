package com.webhotel.webhotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webhotel.webhotel.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
                SELECT b FROM Booking b
                WHERE b.room.id = :roomId
                AND b.canceled = false
                AND (:checkOut > b.checkInDate AND :checkIn < b.checkOutDate)
            """)
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    List<Booking> findByRoomIdAndCanceledFalse(Long roomId);

    @Query("SELECT b FROM Booking b WHERE b.user.username = :username")
    List<Booking> findByUsername(@Param("username") String username);
}
