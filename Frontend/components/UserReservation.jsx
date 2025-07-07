import React from "react";
import styles from "/styles/UserReservation.module.css";

const UserReservation = ({ bookings, label }) => {
  console.log(bookings);
  return (
    <div className={styles.section}>
      <h3>{label}</h3>
      {bookings.length === 0 ? (
        <p>Brak rezerwacji.</p>
      ) : (
        <ul className={styles.list}>
          {bookings.map((booking) => (
            <li key={booking.id} className={styles.item}>
              <p>
                <strong>Pokój:</strong> {booking.roomId || "Brak danych"}
              </p>
              <p>
                <strong>Od:</strong>{" "}
                {new Date(booking.checkInDate).toLocaleDateString()}
              </p>
              <p>
                <strong>Do:</strong>{" "}
                {new Date(booking.checkOutDate).toLocaleDateString()}
              </p>
              <p>
                <strong>Cena:</strong> {booking.price} PLN
              </p>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default UserReservation;
