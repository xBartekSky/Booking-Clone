import { useEffect, useState } from "react";
import { HotelInformation } from "../components/hotelComponents/HotelInformation";
import { LoggedUserHeader } from "../components/LoggedUserHeader";
import { useUser } from "../context/UserContext";
import styles from "/styles/ManageHotel.module.css";
import { useParams } from "react-router-dom";
import { HotelRooms } from "../features/forms/HotelRooms";
import HotelReviews from "../components/hotelComponents/HotelReviews";
import { MainPageNav } from "../features/MainPageNav";
import { fetchHotelReviews } from "../api/reviewApi";
import { fetchHotel } from "../api/hotelApi";
import axiosClient from "../api/axiosClient";
import axios from "axios";

export const ManageHotel = () => {
  const { user } = useUser();
  const { id } = useParams();
  const [hotel, setHotel] = useState(null);
  const [reviews, setReviews] = useState([]);

  useEffect(() => {
    const source = axios.CancelToken.source();

    const fetchData = async () => {
      try {
        try {
          const reviewData = await fetchHotelReviews(id, source.token);
          setReviews(reviewData);
          console.log("Opinie:", reviewData);
        } catch (error) {
          console.error("Błąd podczas pobierania opinii:", error.message);
        }

        try {
          const hotelData = await fetchHotel(id, source.token);
          setHotel(hotelData);
        } catch (error) {
          console.error(
            "Błąd podczas pobierania danych hotelu:",
            error.message
          );
        }
      } catch (error) {
        if (!axiosClient.isCancel(error)) {
          console.log("Wystąpił błąd podczas ładowania danych.");
        }
      }
    };

    fetchData();

    return () => {
      source.cancel("Anulowano żądania z powodu odmontowania komponentu.");
    };
  }, [id]);

  return (
    <div className={styles.container}>
      <LoggedUserHeader name={user?.username}></LoggedUserHeader>
      <MainPageNav></MainPageNav>
      <div className={styles.panel}>
        <div className={styles.app}>
          <HotelInformation
            hotelName={hotel?.name}
            hotelAddress={hotel?.address}
            phoneNumber={hotel?.phoneNumber}
            hotelId={hotel?.id}
            label="Informacje o hotelu"
          ></HotelInformation>
          <HotelRooms label="Lista pokoi" hotelId={id}></HotelRooms>
          <HotelReviews label="Opinie hotelu" reviews={reviews}></HotelReviews>
        </div>
      </div>
    </div>
  );
};
