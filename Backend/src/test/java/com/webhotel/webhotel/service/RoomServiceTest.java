package com.webhotel.webhotel.service;

import com.webhotel.webhotel.dto.RoomDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.Room;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.RoomMapper;
import com.webhotel.webhotel.repository.HotelRepository;
import com.webhotel.webhotel.repository.RoomRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    private Room room;
    private RoomDto roomDto;
    private Hotel hotel;
    private User owner;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setId(1L);

        hotel = new Hotel();
        hotel.setId(100L);
        hotel.setOwner(owner);

        room = new Room();
        room.setId((long) 10);
        room.setHotel(hotel);

        roomDto = new RoomDto();
        roomDto.setId((long) 10);
        roomDto.setHotelId(100L);
    }

    @Test
    void testGetAllRooms() {
        List<Room> rooms = Arrays.asList(room);
        when(roomRepository.findAll()).thenReturn(rooms);
        when(roomMapper.roomToRoomDto(room)).thenReturn(roomDto);

        Iterable<RoomDto> result = roomService.getAllRooms();
        List<RoomDto> resultList = (List<RoomDto>) result;

        assertEquals(1, resultList.size());
        assertEquals(roomDto, resultList.get(0));
    }

    @Test
    void testGetRoomByIdFound() {
        when(roomRepository.findById(10)).thenReturn(Optional.of(room));
        when(roomMapper.roomToRoomDto(room)).thenReturn(roomDto);

        Optional<RoomDto> result = roomService.getRoomById(10);

        assertTrue(result.isPresent());
        assertEquals(roomDto, result.get());
    }

    @Test
    void testGetRoomByIdNotFound() {
        when(roomRepository.findById(10)).thenReturn(Optional.empty());

        Optional<RoomDto> result = roomService.getRoomById(10);

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveRoomSuccess() {
        when(hotelRepository.findById(100L)).thenReturn(Optional.of(hotel));
        when(roomMapper.roomDtoToRoom(roomDto)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.roomToRoomDto(room)).thenReturn(roomDto);

        RoomDto saved = roomService.saveRoom(roomDto, owner);

        assertEquals(roomDto, saved);
    }

    @Test
    void testSaveRoomThrowsAccessDenied() {
        User notOwner = new User();
        notOwner.setId(2L);

        when(hotelRepository.findById(100L)).thenReturn(Optional.of(hotel));

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            roomService.saveRoom(roomDto, notOwner);
        });
    }

    @Test
    void testSaveRoomThrowsHotelNotFound() {
        when(hotelRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            roomService.saveRoom(roomDto, owner);
        });
    }

    @Test
    void testDeleteRoom() {
        doNothing().when(roomRepository).deleteById(10);

        roomService.deleteRoom(10);

        verify(roomRepository, times(1)).deleteById(10);
    }

    @Test
    void testGetRoomsByHotelId() {
        List<Room> rooms = Arrays.asList(room);
        when(roomRepository.findByHotelId(100L)).thenReturn(rooms);
        when(roomMapper.roomToRoomDto(room)).thenReturn(roomDto);

        List<RoomDto> result = roomService.getRoomsByHotelId(100L);

        assertEquals(1, result.size());
        assertEquals(roomDto, result.get(0));
    }
}
