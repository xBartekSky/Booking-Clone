package com.webhotel.webhotel.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.webhotel.webhotel.dto.RoomDto;
import com.webhotel.webhotel.entity.Booking;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.Room;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.RoomMapper;
import com.webhotel.webhotel.repository.BookingRepository;
import com.webhotel.webhotel.repository.HotelRepository;
import com.webhotel.webhotel.repository.RoomRepository;

import jakarta.transaction.Transactional;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private BookingRepository bookingRepository;

    public Iterable<RoomDto> getAllRooms() {
        List<RoomDto> roomdtos = new ArrayList<>();
        for (Room room : roomRepository.findAll()) {
            roomdtos.add(roomMapper.roomToRoomDto(room));
        }
        return roomdtos;
    }

    public Optional<RoomDto> getRoomById(Integer id) {
        Optional<Room> room = roomRepository.findById(id);
        return room.map(roomMapper::roomToRoomDto);
    }

    public RoomDto saveRoom(RoomDto roomDto, User user) {

        Hotel hotel = hotelRepository.findById(roomDto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        if (hotel.getOwner() != user) {
            throw new AccessDeniedException("User does not own this hotel.");
        }

        Room hotelRoom = roomMapper.roomDtoToRoom(roomDto);
        hotelRoom = roomRepository.save(hotelRoom);
        return roomMapper.roomToRoomDto(hotelRoom);
    }

    public void deleteRoom(Integer id) {
        roomRepository.deleteById(id);
    }

    // public List<Room> findAvailableRooms() {
    // return roomRepository.findByIsAvailable(true);
    // }

    public List<RoomDto> getRoomsByHotelId(Long hotelId) {
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        List<RoomDto> roomDtos = new ArrayList<>();

        for (Room room : rooms) {
            RoomDto dto = roomMapper.roomToRoomDto(room);
            roomDtos.add(dto);
        }

        return roomDtos;
    }

    public Long getHotelOwnerIdByRoomId(int roomId) {
        return roomRepository.findById(roomId)
                .map(room -> room.getHotel().getOwner().getId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Transactional
    public RoomDto updateRoom(int id, RoomDto roomDto) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (roomDto.getRoomNumber() != null) {
            existingRoom.setRoomNumber(roomDto.getRoomNumber());
        }
        if (roomDto.getRoomType() != null) {
            existingRoom.setRoomType(roomDto.getRoomType());
        }
        if (roomDto.getPricePerNight() != null) {
            existingRoom.setPricePerNight(roomDto.getPricePerNight().doubleValue());
        }

        Room savedRoom = roomRepository.save(existingRoom);
        return roomMapper.roomToRoomDto(savedRoom);
    }

    public List<RoomDto> getAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        List<Room> allRooms = roomRepository.findByHotelId(hotelId);
        List<RoomDto> availableRooms = new ArrayList<>();

        for (Room room : allRooms) {
            List<Booking> conflicts = bookingRepository.findConflictingBookings(room.getId(), checkIn, checkOut);
            if (conflicts.isEmpty()) {
                RoomDto roomDto = roomMapper.roomToRoomDto(room);
                availableRooms.add(roomDto);
            }
        }

        return availableRooms;
    }

}
