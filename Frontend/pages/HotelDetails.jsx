import { useEffect, useState } from "react";
import { useParams, useSearchParams } from "react-router-dom";
import styles from "/styles/HotelDetails.module.css";
import { SearchBar } from "../components/SearchBar";
import { Footer } from "../components/Footer";
import { MainPageNav } from "../features/MainPageNav";
import { HotelOverview } from "../components/hotelComponents/HotelOverview";
import axiosClient from "../api/axiosClient";
import { fetchHotel } from "../api/hotelApi";
import axios from "axios";

export const HotelDetails = () => {
  const { id } = useParams();
  const [hotel, setHotel] = useState({});

  const [searchParams] = useSearchParams();

  const checkIn = searchParams.get("checkIn");
  const checkOut = searchParams.get("checkOut");

  useEffect(() => {
    const source = axios.CancelToken.source();

    const fetchData = async () => {
      try {
        const hotelData = await fetchHotel(id, source.token);
        setHotel(hotelData);
      } catch (error) {
        if (!axiosClient.isCancel(error)) {
          console.error(
            "Błąd podczas pobierania danych hotelu:",
            error.message
          );
        }
      }
    };

    fetchData();

    return () => {
      source.cancel("Anulowano żądanie z powodu odmontowania komponentu.");
    };
  }, [id]);

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <MainPageNav />

        <SearchBar />
      </div>

      <div className={styles.panel}>
        <HotelOverview hotel={hotel} checkIn={checkIn} checkOut={checkOut} />
      </div>

      <Footer adLabel="Zniżka 15% dla nowych użytkowników!" />
    </div>
  );
};
