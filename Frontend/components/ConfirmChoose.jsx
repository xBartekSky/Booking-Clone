import { Button } from "./Button";
import styles from "/styles/ConfirmChoose.module.css";

export const ConfirmChoose = ({ label, onClick, onCancel }) => {
  return (
    <div className={styles.container}>
      <div className={styles.panel}>
        <div className={styles.labels}>
          <h1 className={styles.title}>Potwierdź wybór</h1>
          <label className={styles.label}>{label}</label>
        </div>

        <div className={styles.buttons}>
          <Button label="Potwierdź" onClick={onClick}></Button>
          <Button label="Anuluj" color="#FF5555" onClick={onCancel}></Button>
        </div>
      </div>
    </div>
  );
};
