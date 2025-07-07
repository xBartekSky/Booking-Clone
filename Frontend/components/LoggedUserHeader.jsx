import { useNavigate } from "react-router-dom";
import { useUser } from "../context/UserContext";
import styles from "/styles/Header.module.css";

export const LoggedUserHeader = ({ name }) => {
  const { setUser } = useUser();
  const nav = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    setUser(null);
    nav("/");
  };
  return (
    <div className={`${styles.header} ${styles.loggedHeader}`}>
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
          <a href="" className={styles.shareHotel}>
            Udostępnij obiekt
          </a>
          <div className={styles.accountContainer}>
            <button className={styles.accountIcon}>
              <img
                src="/src/assets/github-mark.png"
                className={styles.accountImage}
              />
            </button>

            <div className={styles.profileDetails}>
              <a href="" className={styles.userName}>
                {name}
              </a>
              <a onClick={handleLogout} href="" className={styles.userOptions}>
                Wyloguj
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
