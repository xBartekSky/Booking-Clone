package com.webhotel.webhotel.service;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.webhotel.webhotel.dto.BookingDto;
import com.webhotel.webhotel.dto.BookingResponseDto;
import com.webhotel.webhotel.entity.Booking;
import com.webhotel.webhotel.entity.Room;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.BookingMapper;
import com.webhotel.webhotel.repository.BookingRepository;
import com.webhotel.webhotel.repository.RoomRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Room room;
    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("john");

        room = new Room();
        room.setId(1L);
        room.setPricePerNight(100.0);

        bookingDto = new BookingDto();
        bookingDto.setRoomId(1L);
        bookingDto.setCheckInDate(LocalDate.of(2025, 7, 1));
        bookingDto.setCheckOutDate(LocalDate.of(2025, 7, 4));

        booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
    }

    @Test
    void bookRoom_successfulBooking() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findConflictingBookings(eq(1L), any(), any())).thenReturn(List.of());
        when(bookingMapper.dtoToBooking(bookingDto)).thenReturn(booking);
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toDto(any())).thenReturn(new BookingResponseDto());

        BookingResponseDto response = bookingService.bookRoom(user, bookingDto);

        assertNotNull(response);
        verify(bookingRepository).save(any());
    }

    @Test
    void bookRoom_conflict_throwsException() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findConflictingBookings(anyLong(), any(), any())).thenReturn(List.of(new Booking()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bookingService.bookRoom(user, bookingDto));

        assertEquals("Room is already booked for the selected dates.", ex.getMessage());
    }

    @Test
    void getBookingByIdAndUsername_success() {
        booking.setUser(user);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(new BookingResponseDto());

        BookingResponseDto result = bookingService.getBookingByIdAndUsername(1L, "john");

        assertNotNull(result);
    }

    @Test
    void getBookingByIdAndUsername_wrongUser_throws() {
        User otherUser = new User();
        otherUser.setUsername("alice");
        booking.setUser(otherUser);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.getBookingByIdAndUsername(1L, "john"));

        assertEquals("Access denied. You can only view your own bookings.", ex.getMessage());
    }

    @Test
    void getAllBookingsForUsername_returnsList() {
        when(bookingRepository.findByUsername("john")).thenReturn(List.of(booking));
        when(bookingMapper.toDtoList(any())).thenReturn(List.of(new BookingResponseDto()));

        List<BookingResponseDto> results = bookingService.getAllBookingsForUsername("john");

        assertEquals(1, results.size());
    }

    @Test
    void deleteBookingByIdAndUsername_success() {
        booking.setUser(user);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBookingByIdAndUsername(1L, "john");

        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void deleteBookingByIdAndUsername_wrongUser_throws() {
        User another = new User();
        another.setUsername("not-john");
        booking.setUser(another);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.deleteBookingByIdAndUsername(1L, "john"));

        assertEquals("Access denied. You can only delete your own bookings.", ex.getMessage());
    }

    @Test
    void createPaymentIntent_success() throws Exception {
        Map<String, String> metadata = Map.of("bookingId", "123");

        PaymentIntent mockIntent = mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> mocked = Mockito.mockStatic(PaymentIntent.class)) {
            mocked.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(mockIntent);

            PaymentIntent result = bookingService.createPaymentIntent(10000L, "usd", metadata);

            assertEquals(mockIntent, result);
        }
    }
}
