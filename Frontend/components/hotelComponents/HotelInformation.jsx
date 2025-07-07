import { useEffect, useState } from "react";
import { Button } from "../Button";
import { InputField } from "../InputField";
import styles from "/styles/Information.module.css";
import { updateHotel } from "../../api/hotelApi";

export const HotelInformation = ({
  label,
  hotelName,
  hotelAddress,
  phoneNumber,
  hotelId,
}) => {
  console.log("Props:", hotelName, hotelAddress, phoneNumber);

  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    address: "",
    phoneNumber: "",
  });

  useEffect(() => {
    setFormData({
      name: hotelName || "",
      address: hotelAddress || "",
      phoneNumber: phoneNumber || "",
    });
  }, [hotelName, hotelAddress, phoneNumber]);

  useEffect(() => {
    console.log("Montowanie");

    return console.log("Wyczyszczono");
  }, []);

  const handleChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSave = async () => {
    try {
      await updateHotel(hotelId, formData);

      setIsEditing(false);
    } catch (error) {
      console.error("Błąd podczas aktualizacji hotelu:", error.message);
    }
  };

  return (
    <div className={styles.infoBox}>
      <label className={styles.title}>{label}</label>
      <div className={styles.infoContent}>
        {isEditing ? (
          <>
            <InputField
              label="Nazwa hotelu"
              type="text"
              value={formData.name}
              iconName="fa-solid fa-hotel"
              onChange={(e) => handleChange("name", e.target.value)}
            />
            <InputField
              label="Adres"
              type="text"
              value={formData.address}
              iconName="fa-solid fa-map-location-dot"
              onChange={(e) => handleChange("address", e.target.value)}
            />
            <InputField
              label="Telefon"
              type="tel"
              value={formData.phoneNumber}
              iconName="fa-solid fa-phone"
              onChange={(e) => handleChange("phoneNumber", e.target.value)}
            />
            <Button label="Zapisz" onClick={handleSave} />
          </>
        ) : (
          <>
            <p>
              <i className="fa-solid fa-hotel" style={{ color: "gray" }}></i>{" "}
              Nazwa hotelu: {formData.name}
            </p>
            <p>
              <i
                className="fa-solid fa-map-location-dot"
                style={{ color: "gray" }}
              ></i>{" "}
              Adres: {formData.address}
            </p>
            <p>
              <i className="fa-solid fa-phone" style={{ color: "gray" }}></i>{" "}
              Telefon: {formData.phoneNumber}
            </p>
            <Button label="Edytuj" onClick={() => setIsEditing(true)} />
          </>
        )}
      </div>
    </div>
  );
};

export default HotelInformation;
