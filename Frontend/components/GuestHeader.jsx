import { useNavigate } from "react-router-dom";
import styles from "/styles/Header.module.css";

export const GuestHeader = () => {
  const nav = useNavigate();
  const handleLogiNav = () => {
    nav("/login");
  };

  return (
    <div className={styles.header}>
      <div className={styles.headerSection}>
        <div className={styles.logoContainer}>
          <a className={styles.headerTitle}>
            <i className="fa-solid fa-hotel "></i>Webhotel.com
          </a>
        </div>
        <div className={styles.faqContainer}>
          <button className={styles.faqButton}>
            <i
              className="fa-solid fa-magnifying-glass fa-2x "
              style={{ color: "white" }}
            ></i>
          </button>
          <button className={styles.languageButton}>
            <i
              className="fa-solid fa-language fa-2x"
              style={{ color: "white" }}
            ></i>
          </button>
          <button className={styles.loginButton} onClick={handleLogiNav}>
            Logowanie
          </button>
        </div>
      </div>
    </div>
  );
};
