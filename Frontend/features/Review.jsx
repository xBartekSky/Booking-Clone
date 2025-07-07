import styles from "/styles/Review.module.css";

export const Review = ({ rating, comment, userName, createdAt }) => {
  return (
    <div className={styles.container}>
      <div className={styles.reviewContainer}>
        <div className={styles.stars}>
          {[...Array(5)].map((_, i) => (
            <i
              key={i}
              className={i < rating ? "fa-solid fa-star" : "fa-regular fa-star"}
            ></i>
          ))}
        </div>
        <div className={styles.reviewBody}>
          <p>{comment}</p>
        </div>
        <div className={styles.reviewOwner}>
          <p>
            {userName} — {new Date(createdAt).toLocaleDateString()}
          </p>
        </div>
      </div>
    </div>
  );
};
