import styles from "/styles/Button.module.css";

export const Button = ({
  label,
  onClick,
  variant = "green",
  type = "button",
}) => {
  const variantClass = variant === "red" ? styles.red : styles.green;

  return (
    <div className={styles.container}>
      <button
        type={type}
        onClick={onClick}
        className={`${styles.button} ${variantClass}`}
      >
        {label}
      </button>
    </div>
  );
};
