package com.webhotel.webhotel.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    public BookingResponseDto bookRoom(User user, BookingDto bookingDto) {
        Room room = roomRepository.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                room.getId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected dates.");
        }

        Booking booking = bookingMapper.dtoToBooking(bookingDto);
        booking.setRoom(room);
        booking.setUser(user);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setCanceled(false);

        long nights = ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        double totalPrice = room.getPricePerNight() * nights;
        booking.setPrice(totalPrice);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toDto(savedBooking);
    }

    public BookingResponseDto getBookingByIdAndUsername(Long id, String username) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isEmpty()) {
            throw new RuntimeException("Booking not found.");
        }

        Booking booking = bookingOptional.get();
        if (!booking.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied. You can only view your own bookings.");
        }

        return bookingMapper.toDto(booking);
    }

    public List<BookingResponseDto> getAllBookingsForUsername(String username) {
        List<Booking> bookings = bookingRepository.findByUsername(username);
        return bookingMapper.toDtoList(bookings);
    }

    @Transactional
    public void deleteBookingByIdAndUsername(Long id, String username) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isEmpty()) {
            throw new RuntimeException("Booking not found.");
        }

        Booking booking = bookingOptional.get();
        if (!booking.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied. You can only delete your own bookings.");
        }

        bookingRepository.deleteById(id);
    }

    public PaymentIntent createPaymentIntent(long amount, String currency, Map<String, String> metadata)
            throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .putAllMetadata(metadata)
                .build();

        return PaymentIntent.create(params);
    }

}
