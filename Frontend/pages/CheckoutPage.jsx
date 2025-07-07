import { Elements } from "@stripe/react-stripe-js";
import { stripePromise } from "./stripe";
import { CheckoutForm } from "./CheckoutForm";

export const CheckoutPage = ({ bookingData, token }) => {
  return (
    <Elements stripe={stripePromise}>
      <CheckoutForm bookingData={bookingData} token={token} />
    </Elements>
  );
};
