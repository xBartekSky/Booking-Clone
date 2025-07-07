package com.webhotel.webhotel.mapper;

import com.webhotel.webhotel.dto.BookingDto;
import com.webhotel.webhotel.dto.BookingResponseDto;
import com.webhotel.webhotel.entity.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void testBookingToBookingResponseDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");

        Hotel hotel = new Hotel();
        hotel.setName("Grand Plaza");

        Room room = new Room();
        room.setRoomNumber("101");
        room.setHotel(hotel);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.of(2025, 1, 10));
        booking.setCheckOutDate(LocalDate.of(2025, 1, 15));
        booking.setPrice(299.99);
        booking.setCanceled(true);
        booking.setPaymentIntentId("pi_1234");
        booking.setName("John");
        booking.setSurname("Doe");
        booking.setPhone("123456789");
        booking.setAdress("123 Main St");

        BookingResponseDto dto = mapper.toDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals("Grand Plaza", dto.getHotelName());
        assertEquals("101", dto.getRoomName());
        assertEquals("john_doe", dto.getUsername());
        assertEquals(booking.getCheckInDate(), dto.getCheckInDate());
        assertEquals(booking.getCheckOutDate(), dto.getCheckOutDate());
        assertEquals(booking.getPrice(), dto.getPrice());
        assertTrue(dto.isCanceled());
        assertEquals("pi_1234", dto.getPaymentIntentId());
        assertEquals("John", dto.getName());
        assertEquals("Doe", dto.getSurname());
        assertEquals("123456789", dto.getPhone());
        assertEquals("123 Main St", dto.getAdress());
    }

    @Test
    void testDtoToBooking() {
        BookingDto dto = new BookingDto();
        dto.setId(200L);
        dto.setRoomId(10L);
        dto.setUserId(5L);
        dto.setCheckInDate(LocalDate.of(2025, 2, 5));
        dto.setCheckOutDate(LocalDate.of(2025, 2, 10));
        dto.setPaymentIntentId("pi_5678");
        dto.setPaymentRequired(true);
        dto.setName("Jane");
        dto.setSurname("Smith");
        dto.setPhone("987654321");
        dto.setAdress("456 Side St");

        Booking booking = mapper.dtoToBooking(dto);

        assertNull(booking.getRoom());
        assertNull(booking.getUser());
        assertEquals(dto.getCheckInDate(), booking.getCheckInDate());
        assertEquals(dto.getCheckOutDate(), booking.getCheckOutDate());
        assertEquals(dto.getPaymentIntentId(), booking.getPaymentIntentId());
        assertEquals("Jane", booking.getName());
        assertEquals("Smith", booking.getSurname());
        assertEquals("987654321", booking.getPhone());
        assertEquals("456 Side St", booking.getAdress());
        assertFalse(booking.isCanceled());
        assertNotNull(booking.getCreatedAt());
    }

    @Test
    void testBookingToBookingDto() {
        User user = new User();
        user.setId(7L);

        Room room = new Room();
        room.setId(15L);

        Booking booking = new Booking();
        booking.setId(300L);
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(LocalDate.of(2025, 3, 1));
        booking.setCheckOutDate(LocalDate.of(2025, 3, 7));
        booking.setPaymentIntentId("pi_8910");
        booking.setName("Alice");
        booking.setSurname("Wonderland");
        booking.setPhone("111222333");
        booking.setAdress("789 Fantasy Rd");

        BookingDto dto = mapper.bookingToDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(15L, dto.getRoomId());
        assertEquals(7L, dto.getUserId());
        assertEquals(booking.getCheckInDate(), dto.getCheckInDate());
        assertEquals(booking.getCheckOutDate(), dto.getCheckOutDate());
        assertEquals("pi_8910", dto.getPaymentIntentId());
        assertEquals("Alice", dto.getName());
        assertEquals("Wonderland", dto.getSurname());
        assertEquals("111222333", dto.getPhone());
        assertEquals("789 Fantasy Rd", dto.getAdress());
    }
}
