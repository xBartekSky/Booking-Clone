package com.webhotel.webhotel.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Map;

@Service
public class StripeService {

        @Value("${stripe.secret.key}")
        private String stripeSecretKey;

        @PostConstruct
        public void init() {
                Stripe.apiKey = stripeSecretKey;
        }

        public String createCheckoutSession(long amount, String currency, Map<String, String> metadata)
                        throws Exception {
                SessionCreateParams params = SessionCreateParams.builder()
                                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl("http://localhost:5173/successPage")
                                .setCancelUrl("http://localhost:5173/payment-cancel")

                                .addLineItem(SessionCreateParams.LineItem.builder()
                                                .setQuantity(1L)
                                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                                                .setCurrency(currency)
                                                                .setUnitAmount(amount)
                                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                                                                .builder()
                                                                                .setName("Hotel Room Booking")
                                                                                .build())
                                                                .build())
                                                .build())
                                .putAllMetadata(metadata)
                                .build();

                Session session = Session.create(params);
                return session.getUrl();
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

        public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
                return PaymentIntent.retrieve(paymentIntentId);
        }
}
