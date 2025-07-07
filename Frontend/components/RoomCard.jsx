import { useState } from "react";
import { Button } from "./Button";
import { InputField } from "./InputField";
import styles from "/styles/RoomCard.module.css";
import { updateRoom } from "../api/roomApi";

export const RoomCard = ({ roomId, roomNr, price, type }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    roomNr: roomNr || "",
    price: price || "",
    type: type || "",
  });

  const handleChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSave = async () => {
    try {
      await updateRoom(roomId, formData);
      setIsEditing(false);
    } catch (error) {
      console.error("Błąd podczas aktualizacji pokoju:", error.message);
    }
  };

  return (
    <div className={styles.card}>
      <div className={styles.content}>
        {isEditing ? (
          <>
            <InputField
              label="Numer pokoju"
              type="text"
              value={formData.roomNr}
              onChange={(e) => handleChange("roomNr", e.target.value)}
            />
            <InputField
              label="Typ pokoju"
              type="text"
              value={formData.type}
              onChange={(e) => handleChange("type", e.target.value)}
            />
            <InputField
              label="Cena za noc"
              type="number"
              value={formData.price}
              onChange={(e) => handleChange("price", e.target.value)}
            />
            <Button label="Zapisz" onClick={handleSave} />
          </>
        ) : (
          <>
            <p>
              Pokój {formData.roomNr} - {formData.type}
            </p>
            <p>Cena: {formData.price} PLN/noc</p>
            <Button label="Zarządzaj" onClick={() => setIsEditing(true)} />
          </>
        )}
      </div>
    </div>
  );
};
