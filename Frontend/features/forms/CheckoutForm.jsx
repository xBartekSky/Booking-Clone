import { useStripe, useElements, CardElement } from "@stripe/react-stripe-js";
import { useState } from "react";

export const CheckoutForm = ({ bookingData }) => {
  const stripe = useStripe();
  const elements = useElements();
  const [status, setStatus] = useState("");
  const token = localStorage.getItem("token");
  const handleSubmit = async (e) => {
    e.preventDefault();

    const res = await fetch("http://localhost:8080/bookings", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        ...bookingData,
        paymentRequired: true,
      }),
    });

    const data = await res.json();

    if (!stripe || !elements) return;

    const result = await stripe.confirmCardPayment(data.client_secret, {
      payment_method: {
        card: elements.getElement(CardElement),
      },
    });

    if (result.error) {
      setStatus("Błąd płatności: " + result.error.message);
    } else {
      if (result.paymentIntent.status === "succeeded") {
        setStatus("Płatność zakończona sukcesem!");
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <CardElement />
      <button type="submit" disabled={!stripe}>
        Zapłać
      </button>
      <p>{status}</p>
    </form>
  );
};
