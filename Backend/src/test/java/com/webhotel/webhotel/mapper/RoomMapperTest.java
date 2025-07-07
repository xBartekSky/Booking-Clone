package com.webhotel.webhotel.mapper;

import com.webhotel.webhotel.dto.RoomDto;
import com.webhotel.webhotel.entity.Hotel;
import com.webhotel.webhotel.entity.Room;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RoomMapperTest {

    private final RoomMapper roomMapper = Mappers.getMapper(RoomMapper.class);

    @Test
    void testRoomToRoomDto() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        Room room = new Room();
        room.setId(10L);
        room.setRoomNumber("301A");
        room.setRoomType("Deluxe");
        room.setPricePerNight(199.99);
        room.setDescription("Spacious room with sea view");
        room.setHotel(hotel);

        RoomDto dto = roomMapper.roomToRoomDto(room);

        assertEquals(10L, dto.getId());
        assertEquals("301A", dto.getRoomNumber());
        assertEquals("Deluxe", dto.getRoomType());
        assertEquals(new BigDecimal("199.99"), dto.getPricePerNight());
        assertEquals(1L, dto.getHotelId());
    }

    @Test
    void testRoomDtoToRoom() {
        RoomDto dto = new RoomDto();
        dto.setId(20L);
        dto.setRoomNumber("402B");
        dto.setRoomType("Suite");
        dto.setPricePerNight(new BigDecimal("299.50"));
        dto.setHotelId(2L);

        Room room = roomMapper.roomDtoToRoom(dto);

        assertEquals(20L, room.getId());
        assertEquals("402B", room.getRoomNumber());
        assertEquals("Suite", room.getRoomType());
        assertEquals(299.50, room.getPricePerNight());
        assertNotNull(room.getHotel());
        assertEquals(2L, room.getHotel().getId());
    }

    @Test
    void testMapHotelIdToHotel() {
        Hotel hotel = roomMapper.mapHotelIdToHotel(3L);
        assertNotNull(hotel);
        assertEquals(3L, hotel.getId());

        assertNull(roomMapper.mapHotelIdToHotel(null));
    }
}
