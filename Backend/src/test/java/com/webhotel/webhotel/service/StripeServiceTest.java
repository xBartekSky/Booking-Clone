package com.webhotel.webhotel.service;

import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.exception.StripeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    private StripeService stripeService;

    @BeforeEach
    void setUp() throws Exception {
        stripeService = new StripeService();

        // Use reflection to set the private field
        Field field = StripeService.class.getDeclaredField("stripeSecretKey");
        field.setAccessible(true);
        field.set(stripeService, "sk_test_mockkey");

        stripeService.init(); // Initialize Stripe.apiKey
    }

    @Test
    void createCheckoutSession_success() throws Exception {
        Map<String, String> metadata = Map.of("bookingId", "123");

        Session mockSession = mock(Session.class);
        when(mockSession.getUrl()).thenReturn("http://mock-url");

        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(mockSession);

            String url = stripeService.createCheckoutSession(10000L, "usd", metadata);

            assertEquals("http://mock-url", url);
            sessionMockedStatic.verify(() -> Session.create(any(SessionCreateParams.class)));
        }
    }

    @Test
    void createPaymentIntent_success() throws Exception {
        Map<String, String> metadata = Map.of("bookingId", "123");

        PaymentIntent mockIntent = mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> paymentIntentMockedStatic = mockStatic(PaymentIntent.class)) {
            paymentIntentMockedStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                    .thenReturn(mockIntent);

            PaymentIntent result = stripeService.createPaymentIntent(10000L, "usd", metadata);

            assertEquals(mockIntent, result);
            paymentIntentMockedStatic.verify(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)));
        }
    }

    @Test
    void retrievePaymentIntent_success() throws StripeException {
        String paymentIntentId = "pi_12345";
        PaymentIntent mockIntent = mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> paymentIntentMockedStatic = mockStatic(PaymentIntent.class)) {
            paymentIntentMockedStatic.when(() -> PaymentIntent.retrieve(paymentIntentId)).thenReturn(mockIntent);

            PaymentIntent result = stripeService.retrievePaymentIntent(paymentIntentId);

            assertEquals(mockIntent, result);
            paymentIntentMockedStatic.verify(() -> PaymentIntent.retrieve(paymentIntentId));
        }
    }
}
