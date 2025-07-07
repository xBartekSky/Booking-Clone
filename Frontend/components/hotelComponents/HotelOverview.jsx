import { useEffect, useState } from "react";
import { HotelGallery } from "../../features/HotelGallery";
import { Review } from "../../features/Review";
import styles from "/styles/HotelOverview.module.css";
import { useParams } from "react-router-dom";
import { HotelDescription } from "./HotelDescription";
import { HotelFacilities } from "./HotelFacilities";
import HotelReviews from "./HotelReviews";
import { ReservationHotelRooms } from "./ReservationHotelRooms";
import { fetchHotelReviews } from "../../api/reviewApi";
import axiosClient from "../../api/axiosClient";
import { fetchHotelRooms } from "../../api/roomApi";
import axios from "axios";

export const HotelOverview = ({ hotel, checkOut, checkIn }) => {
  const [reviews, setReviews] = useState([]);
  const [randomReview, setRandomReview] = useState(null);
  const [rooms, setRooms] = useState([]);
  const { id } = useParams();
  useEffect(() => {
    const source = axios.CancelToken.source();
    const fetchData = async () => {
      try {
        // Pobieranie opinii
        try {
          const reviewData = await fetchHotelReviews(id, source.token);
          setReviews(reviewData);
          if (reviewData.length > 0) {
            const randomIndex = Math.floor(Math.random() * reviewData.length);
            setRandomReview(reviewData[randomIndex]);
          }
        } catch (error) {
          console.error("Błąd podczas pobierania opinii:", error.message);
        }

        // Pobieranie pokoi
        try {
          const roomData = await fetchHotelRooms(id, source.token);
          setRooms(roomData);
          console.log("Pokoje:", roomData);
        } catch (error) {
          console.error("Błąd podczas pobierania pokoi:", error.message);
        }
      } catch (error) {
        if (!axiosClient.isCancel(error)) {
          console.log(error);
        }
      }
    };

    fetchData();

    // Czyszczenie przy odmontowaniu
    return () => {
      source.cancel("Anulowano żądania z powodu odmontowania komponentu.");
    };
  }, [id]);

  return (
    <div className={styles.container}>
      <div className={styles.app}>
        <div className={styles.titleContainer}>
          <h1 className={styles.title}>{hotel.name}</h1>
          <a className={styles.info}>
            ul. {hotel.address}, {hotel.city}, {hotel.country}
          </a>
        </div>
        <div className={styles.panel}>
          <HotelGallery
            images={hotel.images}
            className={styles.hotelGallery}
          ></HotelGallery>
          <div className={styles.rateContainer}>
            <Review
              rating={randomReview?.rating}
              comment={`„${randomReview?.comment}”`}
              createdAt={randomReview?.createdAt}
              userName={randomReview?.username}
            ></Review>
          </div>
        </div>
        <div className={styles.sectionsContainer}>
          <div className={styles.leftSection}>
            <HotelDescription
              description={hotel.description}
            ></HotelDescription>
            <HotelFacilities hotel={hotel}></HotelFacilities>
          </div>
          <div className={styles.rightSection}></div>
        </div>

        <ReservationHotelRooms
          room={rooms}
          checkOut={checkOut}
          checkIn={checkIn}
        ></ReservationHotelRooms>
        <HotelReviews
          hotelId={hotel?.id}
          label="Opinie hotelu"
          reviews={reviews}
          allowAdd="true"
        ></HotelReviews>
      </div>
    </div>
  );
};
