import styles from "/styles/HotelDescription.module.css";

export const HotelDescription = ({ description }) => {
  return (
    <div className={styles.container}>
      <div className={styles.titleContainer}>
        <h1 className={styles.title}>Opis</h1>
      </div>
      <div className={styles.descContainer}>
        <p>{description}</p>
      </div>
    </div>
  );
};
