import { useEffect, useState } from "react";
import { ReservationRoomCard } from "../ReservationRoomCard";
import styles from "/styles/ReservationHotelRooms.module.css";
import { fetchUnavailableDates } from "../../api/reservationApi";

export const ReservationHotelRooms = ({ room, checkIn, checkOut }) => {
  const [availableRooms, setAvailableRooms] = useState([]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    const fetchAvailableRooms = async () => {
      const result = [];

      for (const r of room) {
        try {
          const dates = await fetchUnavailableDates(r.id);
          const available = isRoomAvailable(dates, checkIn, checkOut);
          if (available) result.push(r);
        } catch (error) {
          console.error("Błąd przy sprawdzaniu pokoju", r.id, error);
          setMessage("Wystąpił błąd podczas ładowania dostępnych pokoi.");
        }
      }

      setAvailableRooms(result);
      if (result.length === 0 && !message) {
        setMessage("Brak dostępnych pokoi w wybranym terminie.");
      }
    };

    if (checkIn && checkOut && room.length > 0) {
      setMessage("");
      fetchAvailableRooms();
    }
  }, [room, checkIn, checkOut, message]);

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Rezerwuj wymarzony pokój</h1>
      <div className={styles.content}>
        {message && <p className={styles.message}>{message}</p>}
        {availableRooms.length > 0
          ? availableRooms.map((r) => (
              <ReservationRoomCard
                key={r.id}
                roomNumber={r.roomNumber}
                price={r.pricePerNight}
                roomType={r.roomType}
                roomId={r.id}
                checkIn={checkIn}
                checkOut={checkOut}
              />
            ))
          : !message && <p>Brak dostępnych pokoi w wybranym terminie</p>}
      </div>
    </div>
  );
};

function isRoomAvailable(unavailableDates, checkIn, checkOut) {
  const start = new Date(checkIn);
  const end = new Date(checkOut);

  for (const { checkIn: uStart, checkOut: uEnd } of unavailableDates) {
    const uStartDate = new Date(uStart);
    const uEndDate = new Date(uEnd);

    if (start < uEndDate && end > uStartDate) {
      return false;
    }
  }

  return true;
}
