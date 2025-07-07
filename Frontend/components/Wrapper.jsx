import styles from "/styles/Wrapper.module.css";

export const Wrapper = ({ title, subtitle }) => {
  return (
    <div className={styles.wrapper}>
      <div className={styles.app}>
        <h1 className={styles.title}>{title}</h1>
        <h1 className={styles.subtitle}>{subtitle}</h1>
      </div>
    </div>
  );
};
