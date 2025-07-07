import styles from "/styles/helloUser.module.css";

export const HelloUser = ({ name }) => {
  return <h1 className={styles.helloTitle}>Witaj, {name}</h1>;
};
