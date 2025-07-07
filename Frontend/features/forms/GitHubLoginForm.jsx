import styles from "/styles/GitHubLoginForm.module.css";

export const GitHubLoginForm = () => {
  const handleGitLogin = () => {
    window.location.href = "http://localhost:8080/auth/github";
  };

  return (
    <div className={styles.gitLoginContainer}>
      <button
        onClick={handleGitLogin}
        className={styles.gitLoginButton}
        aria-label="Zaloguj się przez GitHub"
      ></button>
    </div>
  );
};
