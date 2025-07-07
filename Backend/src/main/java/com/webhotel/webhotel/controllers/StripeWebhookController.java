package com.webhotel.webhotel.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.webhotel.webhotel.entity.Booking;
import com.webhotel.webhotel.repository.BookingRepository;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        System.out.println("Received Stripe webhook");
        System.out.println("Signature Header: " + sigHeader);

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            System.out.println("Successfully verified Stripe event signature");
        } catch (SignatureVerificationException e) {
            System.out.println("Signature verification failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        System.out.println("Event type: " + event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            var dataObjectDeserializer = event.getDataObjectDeserializer();

            if (!dataObjectDeserializer.getObject().isPresent()) {
                System.out.println("Data object deserialization failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid event data object");
            }

            Object stripeObject = dataObjectDeserializer.getObject().get();
            System.out.println("Deserialized Stripe object: " + stripeObject.getClass().getSimpleName());

            if (!(stripeObject instanceof Session)) {
                System.out.println("Unexpected object type: " + stripeObject.getClass().getName());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unexpected event data object type");
            }

            Session session = (Session) stripeObject;
            Map<String, String> metadata = session.getMetadata();
            System.out.println("Session metadata: " + metadata);

            try {
                String bookingIdStr = metadata.get("bookingId");
                if (bookingIdStr == null) {
                    System.out.println("No bookingId found in metadata");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing bookingId in metadata");
                }

                Long bookingId = Long.parseLong(bookingIdStr);
                System.out.println("Parsed bookingId: " + bookingId);

                Booking booking = bookingRepository.findById(bookingId).orElse(null);

                if (booking == null) {
                    System.out.println("Booking not found for ID: " + bookingId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
                }

                System.out.println("Found booking: " + booking.getId() + " - current status: " + booking.getStatus());

                booking.setStatus("CONFIRMED");
                booking.setPaymentIntentId(session.getPaymentIntent());
                System.out.println("Updating booking status to CONFIRMED and setting paymentIntentId: "
                        + session.getPaymentIntent());

                bookingRepository.save(booking);
                System.out.println("Booking updated successfully");

                return ResponseEntity.ok("Booking confirmed");
            } catch (Exception e) {
                System.out.println("Exception while processing webhook:");
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
            }
        } else {
            System.out.println("Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok("Ignored");
    }
}
