import React, { useState } from "react";
import styles from "/styles/HotelReviews.module.css";
import { Review } from "../../features/Review";
import Modal from "../Modal";
import { AddReviewForm } from "../../features/forms/AddReviewForm";
import { Button } from "../Button";
import { useUser } from "../../context/UserContext";
import { useUserBookings } from "../../hooks/useUserBookings";
import { useLocation, useNavigate } from "react-router-dom";

export const HotelReviews = ({ hotelId, label, reviews, allowAdd }) => {
  const [showAddForm, setShowAddForm] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const { user } = useUser();
  const token = localStorage.getItem("token");
  const { hasStayedAtHotel } = useUserBookings(token);
  const navigate = useNavigate();
  const location = useLocation();

  const closeModal = () => {
    setShowAddForm(false);
  };

  const handleAddReviewClick = () => {
    setErrorMessage("");

    if (!user) {
      navigate("/login", { state: { from: location.pathname } });
    } else if (!hasStayedAtHotel(hotelId)) {
      setErrorMessage(
        "Musisz mieć zakończoną rezerwację w tym obiekcie, aby dodać opinię."
      );
    } else {
      setShowAddForm(true);
    }
  };

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>{label}</h1>
      <div className={styles.content}>
        {reviews.map((review) => (
          <Review
            key={review.id}
            rating={review?.rating}
            comment={review?.comment}
            userName={review?.username}
            createdAt={review?.createdAt}
          />
        ))}

        <Modal isOpen={showAddForm} onClose={closeModal}>
          <AddReviewForm idHotel={hotelId} />
        </Modal>
      </div>

      {allowAdd && (
        <div>
          <Button label="Dodaj opinię" onClick={handleAddReviewClick} />
          {errorMessage && (
            <div style={{ color: "red", marginTop: "8px" }}>{errorMessage}</div>
          )}
        </div>
      )}
    </div>
  );
};

export default HotelReviews;
