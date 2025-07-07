import { useEffect, useState } from "react";
import { RoomCard } from "../../components/RoomCard";
import styles from "/styles/HotelRooms.module.css";
import { Button } from "../../components/Button";
import Modal from "../../components/Modal";
import { AddRoomForm } from "./AddRoom";
import { fetchRooms } from "../../api/hotelApi";

export const HotelRooms = ({ label, hotelId }) => {
  const [rooms, setRooms] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const closeModal = () => setShowAddForm(false);

  useEffect(() => {
    if (!hotelId) return;

    setLoading(true);
    setError(null);

    fetchRooms(hotelId)
      .then((data) => setRooms(data))
      .catch((err) => setError("Błąd podczas pobierania pokoi" + err))
      .finally(() => setLoading(false));
  }, [hotelId]);

  return (
    <div className={styles.container}>
      <div className={styles.title}>{label}</div>
      <div className={styles.content}>
        {loading && <p>Ładowanie pokoi...</p>}
        {error && <p style={{ color: "red" }}>{error}</p>}
        {!loading && !error && rooms.length === 0 && (
          <p>Żaden pokój jeszcze nie został dodany do tego hotelu.</p>
        )}
        {!loading &&
          !error &&
          rooms.map((room) => (
            <RoomCard
              key={room.roomNumber}
              roomNr={room.roomNumber}
              type={room.roomType}
              price={room.pricePerNight}
              roomId={room.id}
            />
          ))}
      </div>

      <Modal isOpen={showAddForm} onClose={closeModal}>
        <AddRoomForm hotelId={hotelId} />
      </Modal>
      <Button label="Dodaj pokój" onClick={() => setShowAddForm(true)} />
    </div>
  );
};
