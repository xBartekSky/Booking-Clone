package com.webhotel.webhotel.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.PaymentIntent;
import com.webhotel.webhotel.config.AuthProvider;
import com.webhotel.webhotel.dto.BookingDto;
import com.webhotel.webhotel.dto.BookingResponseDto;
import com.webhotel.webhotel.dto.ErrorDto;
import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.entity.Booking;
import com.webhotel.webhotel.entity.Room;
import com.webhotel.webhotel.entity.User;
import com.webhotel.webhotel.mapper.BookingMapper;
import com.webhotel.webhotel.repository.BookingRepository;
import com.webhotel.webhotel.repository.RoomRepository;
import com.webhotel.webhotel.repository.UserRepository;
import com.webhotel.webhotel.service.BookingService;
import com.webhotel.webhotel.service.StripeService;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AuthProvider authProvider;

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody BookingDto bookingDto,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        long nights = ChronoUnit.DAYS.between(bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        if (nights <= 0) {
            return ResponseEntity.badRequest().body(new ErrorDto("Invalid date range"));
        }

        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                room.getId(), bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());

        if (!conflicts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorDto("Room is already booked for the selected dates."));
        }

        double totalPrice = room.getPricePerNight() * nights;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setPrice(totalPrice);
        booking.setName(bookingDto.getName());
        booking.setSurname(bookingDto.getSurname());
        booking.setAdress(bookingDto.getAdress());
        booking.setPhone(bookingDto.getPhone());
        booking.setHotelId(bookingDto.getHotelId());

        booking = bookingRepository.save(booking);

        String status;
        String checkoutUrl = null;

        if (bookingDto.isPaymentRequired()) {
            status = "PENDING_PAYMENT";
            try {
                long amount = new BigDecimal(totalPrice)
                        .multiply(BigDecimal.valueOf(100))
                        .longValue();

                Map<String, String> metadata = Map.of(
                        "bookingId", String.valueOf(booking.getId())

                );

                checkoutUrl = stripeService.createCheckoutSession(amount, "pln", metadata);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorDto("Error creating checkout session: " + e.getMessage()));
            }

        } else {
            status = "CONFIRMED";
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking); // save regardless of payment

        if (bookingDto.isPaymentRequired()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Booking created and awaiting payment.",
                            "checkoutUrl", checkoutUrl,
                            "bookingId", booking.getId()));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Booking created successfully.",
                            "bookingId", booking.getId()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();

        try {
            BookingResponseDto booking = bookingService.getBookingByIdAndUsername(id, username);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorDto("Booking not found or access denied."));
        }
    }

    @GetMapping
public ResponseEntity<?> getUserBookings(@RequestHeader("Authorization") String authorizationHeader) {
    try {
        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();
        List<BookingResponseDto> bookings = bookingService.getAllBookingsForUsername(username);
        return ResponseEntity.ok(bookings);
    } catch (Exception e) {
        System.err.println("Błąd w getUserBookings: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Błąd podczas pobierania rezerwacji: " + e.getMessage()));
    }
}

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookingById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();

        try {
            bookingService.deleteBookingByIdAndUsername(id, username);
            return ResponseEntity.ok(Map.of("message", "Booking deleted successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorDto("Booking not found or access denied."));
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");
        Authentication authentication = authProvider.validateToken(token);
        String username = ((UserDto) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User hotelOwner = booking.getRoom().getHotel().getOwner();

        if (!hotelOwner.getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDto("You are not authorized to complete this booking."));
        }

        if ("COMPLETED".equalsIgnoreCase(booking.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDto("Booking has already been completed."));
        }

        booking.setStatus("COMPLETED");
        bookingRepository.save(booking);

        return ResponseEntity.ok(Map.of("message", "Booking status changed to COMPLETED."));
    }

}
