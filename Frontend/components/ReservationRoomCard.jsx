import { Button } from "./Button";
import styles from "/styles/ReservationRoomCard.module.css";

import { useNavigate } from "react-router-dom";

export const ReservationRoomCard = ({
  roomNumber,
  price,
  roomType,
  roomId,
  checkIn,
  checkOut,
}) => {
  const nav = useNavigate();

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Pokój {roomNumber}</h1>
      <p className={styles.price}>
        ZŁ<span className={styles.numberPrice}>{price}</span>/dzień
      </p>
      <div className={styles.informationContainer}>
        <p>Standard - {roomType}</p>
      </div>
      <Button
        label="Zarezerwuj"
        onClick={() => {
          nav(`/getReservation/${roomId}`, {
            state: { roomNumber, price, roomType, checkIn, checkOut },
          });
        }}
      />
    </div>
  );
};
