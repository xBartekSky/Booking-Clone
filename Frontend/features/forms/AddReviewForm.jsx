import { useState } from "react";
import { InputField } from "../../components/InputField";
import styles from "/styles/AddReview.module.css";
import { useUser } from "../../context/UserContext";
import { addReview } from "../../api/reviewApi";
import { Button } from "../../components/Button";

export const AddReviewForm = ({ idHotel }) => {
  const { userId } = useUser();
  const [reviewData, setReviewData] = useState({
    comment: "",
    createdAt: new Date().toISOString(),
    rating: 0,
    updatedAt: new Date().toISOString(),
    hotelId: idHotel,
    userId: userId ?? "",
  });

  const [message, setMessage] = useState("");

  const handleStarClick = (index) => {
    setReviewData({ ...reviewData, rating: index + 1 });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await addReview(idHotel, reviewData);
      setMessage("Dodano opinię!");
      setReviewData((prev) => ({
        ...prev,
        comment: "",
        rating: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      }));
    } catch (err) {
      console.error(err);
      setMessage("Wystąpił błąd.");
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.panel}>
        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.inputs}>
            <label className={styles.starLabel}>Ocena:</label>
            <div className={styles.stars}>
              {[...Array(5)].map((_, index) => (
                <span
                  key={index}
                  className={`${styles.star} ${
                    index < reviewData.rating ? styles.filled : ""
                  }`}
                  onClick={() => handleStarClick(index)}
                >
                  ★
                </span>
              ))}
            </div>

            <InputField
              label="Komentarz"
              placeholder="Wpisz swoją opinię"
              type="text"
              value={reviewData.comment}
              inputClassName={styles.input}
              onChange={(e) =>
                setReviewData({ ...reviewData, comment: e.target.value })
              }
            />
          </div>
          <Button type="submit" variant="green" label="Dodaj"></Button>

          {message && <p className={styles.message}>{message}</p>}
        </form>
      </div>
    </div>
  );
};
