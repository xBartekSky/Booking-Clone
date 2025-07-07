import { useState } from "react";
import { InputField } from "../../components/InputField";
import styles from "/styles/AddHotelForm.module.css";
import { Button } from "../../components/Button";
import { addHotel } from "../../api/hotelApi";

export const AddHotelForm = ({ user, onHotelAdded }) => {
  const [name, setHotelName] = useState("");
  const [description, setDesc] = useState("");
  const [address, setHotelAddress] = useState("");
  const [city, setHotelCity] = useState("");
  const [country, setHotelCountry] = useState("");
  const [mainImageFile, setMainImageFile] = useState(null);
  const [galleryFiles, setGalleryFiles] = useState([]);

  const initialFacilities = {
    hasAirConditioning: false,
    hasBar: false,
    hasGym: false,
    hasLaundryService: false,
    hasParking: false,
    hasPetFriendly: false,
    hasPool: false,
    hasRestaurant: false,
    hasSpa: false,
    hasWifi: false,
  };
  const [facilities, setFacilities] = useState(initialFacilities);

  const handleFacilities = (key, value) => {
    setFacilities((prev) => ({ ...prev, [key]: value }));
  };

  const handleAddHotel = async (e) => {
    e.preventDefault();
    try {
      const hotelData = {
        name,
        email: user?.email,
        phoneNumber: user?.phoneNumber,
        city,
        description,
        address,
        country,
        ...facilities,
      };
      const addedHotel = await addHotel(hotelData, mainImageFile, galleryFiles);
      onHotelAdded(addedHotel);
    } catch (error) {
      console.error("Nie udało się dodać hotelu." + error);
    }
  };

  return (
    <div className={styles.panel}>
      <h1 className={styles.title}>Dodawanie obiektu</h1>
      <form className={styles.form} onSubmit={handleAddHotel}>
        <div className={styles.inputs}>
          <InputField
            label="Nazwa hotelu"
            placeholder="Nazwa hotelu"
            inputClassName={styles.input}
            onChange={(e) => setHotelName(e.target.value)}
          ></InputField>
          <InputField
            label="Miasto"
            placeholder="Miasto"
            inputClassName={styles.input}
            onChange={(e) => setHotelCity(e.target.value)}
          ></InputField>
          <InputField
            label="Kraj"
            placeholder="Kraj"
            inputClassName={styles.input}
            onChange={(e) => setHotelCountry(e.target.value)}
          ></InputField>
          <InputField
            label="Ulica i nr lokalu"
            inputClassName={styles.input}
            onChange={(e) => setHotelAddress(e.target.value)}
          ></InputField>
          <InputField
            label="Opis obiektu"
            inputClassName={styles.descInput}
            onChange={(e) => setDesc(e.target.value)}
            multiline="true"
          ></InputField>
          <InputField
            type="file"
            label="Zdjęcie główne"
            inputClassName={styles.input}
            onChange={(e) => setMainImageFile(e.target.files[0])}
          ></InputField>
          <input
            type="file"
            label="Galeria zdjęć"
            className={styles.input}
            multiple
            onChange={(e) => setGalleryFiles(Array.from(e.target.files))}
          ></input>
          <Button type="submit" label="Dodaj obiekt"></Button>
        </div>
        <div className={styles.facilitiesContainer}>
          <h1 className={styles.facilitiesTitle}>Udogodnienia</h1>
          <div className={styles.facilities}>
            <InputField
              type="checkbox"
              label="Klimatyzacja"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasAirConditioning", e.target.checked);
              }}
            ></InputField>
            <InputField
              type="checkbox"
              label="Drinkbar"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasBar", e.target.checked);
                console.log(facilities);
              }}
            ></InputField>
            <InputField
              type="checkbox"
              label="Siłownia"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasGym", e.target.checked);
              }}
            ></InputField>
            <InputField
              type="checkbox"
              label="Pralnia"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasLaundryService", e.target.checked);
              }}
            ></InputField>
            <InputField
              type="checkbox"
              label="Parking"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasParking", e.target.checked);
              }}
            ></InputField>
            <InputField
              type="checkbox"
              label="Akceptowanie zwierząt"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasPetFriendly", e.target.checked);
              }}
            ></InputField>
            <InputField
              type="checkbox"
              label="Basen"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasPool", e.target.checked);
              }}
            ></InputField>
            <InputField
              type="checkbox"
              label="Restauracja"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasRestaurant", e.target.checked);
              }}
            ></InputField>
            <InputField
              type="checkbox"
              label="Spa"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasSpa", e.target.checked);
              }}
            ></InputField>
            <InputField
              type="checkbox"
              label="Wifi"
              inputClassName={styles.facilitiesInput}
              onChange={(e) => {
                handleFacilities("hasWifi", e.target.checked);
              }}
            ></InputField>
          </div>
        </div>
      </form>
    </div>
  );
};
