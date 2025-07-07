package com.webhotel.webhotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.webhotel.webhotel.entity.Room;

public interface RoomRepository extends CrudRepository<Room, Integer> {

    // List<Room> findByIsAvailable(boolean available);

    List<Room> findByHotelId(Long hotelId);

    Optional<Room> findById(Long roomId);

}
