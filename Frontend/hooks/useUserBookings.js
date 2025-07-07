import { useEffect, useState } from "react";
import { fetchBookings } from "../api/bookingApi";

export const useUserBookings = (token) => {
  const [allBookings, setAllBookings] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;

    const fetchData = async () => {
      try {
        if (token) {
          const bookingsData = await fetchBookings(token);
          console.log("Rezerwacje: ", bookingsData);
          if (isMounted) {
            setAllBookings(bookingsData);
            setError(null);
          }
        }
      } catch (error) {
        if (isMounted) {
          console.error("Błąd podczas pobierania rezerwacji:", error.message);
          setError(
            error.message || "Wystąpił błąd podczas ładowania rezerwacji."
          );
        }
      }
    };

    fetchData();

    return () => {
      isMounted = false;
    };
  }, [token]);

  const now = new Date();

  const currentBookings = allBookings.filter(
    (b) => new Date(b.checkOutDate) >= now
  );

  const pastBookings = allBookings.filter(
    (b) => new Date(b.checkOutDate) < now
  );

  const hasStayedAtHotel = (hotelId) => {
    return allBookings.some((booking) => booking.hotelId === hotelId);
  };

  return { currentBookings, pastBookings, hasStayedAtHotel, error };
};
