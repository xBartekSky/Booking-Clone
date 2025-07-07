import { useNavigate } from "react-router-dom";
import styles from "/styles/Footer.module.css";

export const Footer = ({ adLabel }) => {
  const nav = useNavigate();
  return (
    <div className={styles.container}>
      <div className={styles.ad}>
        <div className={styles.adContent}>
          <div className={styles.labelContainer}>
            <label className={styles.adLabel}>{adLabel}</label>
          </div>
          <div className={styles.buttons}>
            <button
              onClick={() => {
                nav("/login");
              }}
            >
              Zaloguj
            </button>
            <button
              onClick={() => {
                nav("/register");
              }}
            >
              Utwórz nowe konto
            </button>
          </div>
        </div>
      </div>

      <div className={styles.panel}>
        <div className={styles.info}>
          <div className={styles.social}>
            <div className={styles.pageTitle}>
              <h1 className={styles.title}>Webhotel.Com</h1>
            </div>
            <div className={styles.socialIcons}>
              <i className="fa-brands fa-facebook fa-2x"></i>
              <i className="fa-brands fa-youtube fa-2x"></i>
              <i className="fa-brands fa-instagram fa-2x"></i>
              <i className="fa-brands fa-x-twitter fa-2x"></i>
            </div>
          </div>
          <div className={styles.helpContainer}>
            <div className={styles.help}>
              <a href="">Wsparcie</a>
              <a href="">Page</a>
              <a href="">Page</a>
              <a href="">Page</a>
              <a href="">Page</a>
            </div>
            <div className={styles.help}>
              <a href="">Ustawienia</a>
              <a href="">Page</a>
              <a href="">Page</a>
              <a href="">Page</a>
              <a href="">Page</a>
            </div>
            <div className={styles.help}>
              <a href="">Informacje</a>
              <a href="">Page</a>
              <a href="">Page</a>
              <a href="">Page</a>
              <a href="">Page</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
