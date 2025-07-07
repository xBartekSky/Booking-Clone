import { FacilitiesCard } from "../FacilitiesCard";
import styles from "/styles/HotelFacilities.module.css";
import {
  FaSnowflake,
  FaCocktail,
  FaDumbbell,
  FaTshirt,
  FaParking,
  FaPaw,
  FaSwimmingPool,
  FaUtensils,
  FaSpa,
  FaWifi,
} from "react-icons/fa";

export const HotelFacilities = ({ hotel }) => {
  const facilitiesConfig = [
    { key: "hasAirConditioning", label: "Klimatyzacja", icon: <FaSnowflake /> },
    { key: "hasBar", label: "Bar", icon: <FaCocktail /> },
    { key: "hasGym", label: "Siłownia", icon: <FaDumbbell /> },
    { key: "hasLaundryService", label: "Pralnia", icon: <FaTshirt /> },
    { key: "hasParking", label: "Parking", icon: <FaParking /> },
    { key: "hasPetFriendly", label: "Przyjazny zwierzętom", icon: <FaPaw /> },
    { key: "hasPool", label: "Basen", icon: <FaSwimmingPool /> },
    { key: "hasRestaurant", label: "Restauracja", icon: <FaUtensils /> },
    { key: "hasSpa", label: "SPA", icon: <FaSpa /> },
    { key: "hasWifi", label: "Wi-Fi", icon: <FaWifi /> },
  ];

  const availableFacilities = facilitiesConfig.filter(({ key }) => hotel[key]);

  if (availableFacilities.length === 0) {
    return <p>Brak udogodnień do wyświetlenia</p>;
  }

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Udogodnienia</h1>
      <div className={styles.facilities}>
        {availableFacilities.map(({ key, label, icon }) => (
          <FacilitiesCard key={key} label={label} icon={icon} />
        ))}
      </div>
    </div>
  );
};
