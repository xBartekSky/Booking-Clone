import { GuestHeader } from "../components/GuestHeader";
import { RegisterForm } from "../features/forms/RegisterForm";
import styles from "/styles/RegisterPage.module.css";

export const RegisterPage = () => {
  return (
    <div className="container">
      <GuestHeader></GuestHeader>
      <div className={styles.app}>
        <div className={styles.registerContainer}>
          <div className={styles.registerPanel}>
            <h1 className={styles.registerPageTitle}>Rejestracja</h1>
            <RegisterForm></RegisterForm>
          </div>
        </div>
      </div>
    </div>
  );
};
