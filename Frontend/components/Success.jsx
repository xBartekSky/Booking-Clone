import { useEffect, useState } from "react";
import styles from "/styles/Success.module.css";
import { Button } from "./Button";
import { useNavigate } from "react-router-dom";

export const Success = () => {
  const nav = useNavigate();
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setProgress((prev) => (prev < 100 ? prev + 2 : 100));
    }, 10);

    return () => clearInterval(interval);
  }, []);

  const radius = 70;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (progress / 100) * circumference;

  return (
    <div className={styles.container}>
      <div className={styles.circleWrapper}>
        <svg className={styles.svg} viewBox="0 0 160 160">
          <circle className={styles.circleBg} cx="80" cy="80" r={radius} />
          <circle
            className={styles.circleProgress}
            cx="80"
            cy="80"
            r={radius}
            strokeDasharray={circumference}
            strokeDashoffset={offset}
          />
        </svg>
        <div className={styles.checkmark}>
          <svg
            width="48"
            height="48"
            viewBox="0 0 24 24"
            fill="none"
            stroke="#22c55e"
            strokeWidth="3"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <polyline points="20 6 9 17 4 12" />
          </svg>
        </div>
      </div>
      <h1 className={styles.text}>Rezerwacja zakończona powodzeniem</h1>
      <Button
        label="Przeglądaj dalej"
        onClick={() => {
          nav("/mainpage");
        }}
      ></Button>
    </div>
  );
};
