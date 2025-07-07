import { useEffect } from "react";
import { GitHubLoginForm } from "../features/forms/GitHubLoginForm";
import { LoginForm } from "../features/forms/LoginForm";

import "/styles/LoginPage.css";
import { useNavigate } from "react-router-dom";
import { GuestHeader } from "../components/GuestHeader";

export const LoginPage = () => {
  const nav = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    console.log("Token z URL:", token);
    if (token) {
      console.log(token);
      localStorage.setItem("token", token);
      nav("/dashboard");
    }
  }, [nav]);

  return (
    <div className="container">
      <GuestHeader></GuestHeader>
      <div className="app">
        <div className="accessContainer">
          <div className="accessPanel">
            <h1 className="loginPageTitle">Logowanie</h1>

            <label className="loginPageSubtitle">
              Zaloguj się przy użyciu konta Webhotel.com aby uzyskać dostęp do
              usług witryny.
            </label>

            <LoginForm className="loginForm"></LoginForm>
            <div className="accessPanelSocial">
              <div className="socialTitle">
                <label className="socialTitle">lub zaloguj się używając</label>
              </div>
              <GitHubLoginForm></GitHubLoginForm>
              <a href="/register" className="registerAd">
                Nie masz jeszcze konta? Zarejestruj się
              </a>
              <span className="privacyPolicy">
                Zalogowanie się na swoje konto lub utworzenie go jest
                równoznaczne z akceptacją Warunków oraz Oświadczenia o ochronie
                prywatności.
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
