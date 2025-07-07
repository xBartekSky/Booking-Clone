import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import styles from "/styles/SearchBar.module.css";
import { DateRangePicker } from "./DateRangePicker";

export const SearchBar = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const [city, setCity] = useState(searchParams.get("city") || "");
  const [checkIn, setCheckIn] = useState(searchParams.get("checkIn") || "");
  const [checkOut, setCheckOut] = useState(searchParams.get("checkOut") || "");
  const [guests, setGuests] = useState(searchParams.get("guests") || 1);

  useEffect(() => {
    setCity(searchParams.get("city") || "");
    setCheckIn(searchParams.get("checkIn") || "");
    setCheckOut(searchParams.get("checkOut") || "");
    setGuests(searchParams.get("guests") || 1);
  }, [searchParams]);

  const handleSearch = () => {
    setSearchParams({
      city,
      checkIn,
      checkOut,
      guests,
    });
  };

  const handleDateSelect = ({ startDate, endDate }) => {
    setCheckIn(startDate);
    setCheckOut(endDate);
  };

  return (
    <div className={styles.container}>
      <div className={styles.barContainer}>
        <div className={styles.bar}>
          <div className={styles.inputs}>
            <input
              className={styles.input}
              placeholder="Miejsce podróży"
              type="text"
              value={city}
              onChange={(e) => setCity(e.target.value)}
            />
            <DateRangePicker
              onSelect={handleDateSelect}
              className={styles.input}
            ></DateRangePicker>
            <input
              className={styles.input}
              placeholder="Ilość osób"
              type="number"
              min={1}
              value={guests}
              onChange={(e) => setGuests(e.target.value)}
            />
            <button className={styles.searchButton} onClick={handleSearch}>
              Szukaj
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
