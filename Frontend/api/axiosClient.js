import axios from "axios";

export const refreshTokenTest = async () => {
  console.log("[TokenRefresh] Próbuję odświeżyć token...");
  try {
    const response = await refreshClient.post("/api/refresh");
    const newToken = response.data.token;
    if (newToken) {
      localStorage.setItem("token", newToken);
      console.log("[TokenRefresh] Token odświeżony pomyślnie");
      scheduleTokenRefresh();
    } else {
      console.warn("[TokenRefresh] Otrzymano pusty token podczas odświeżania");
      localStorage.removeItem("token");
    }
  } catch (err) {
    console.error("[TokenRefresh] Błąd podczas odświeżania tokena:", err);
    localStorage.removeItem("token");
  }
};

const parseJwt = (token) => {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => `%${("00" + c.charCodeAt(0).toString(16)).slice(-2)}`)
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error("Błąd podczas parsowania tokena JWT:", e);
    return null;
  }
};

const axiosClient = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
});

const refreshClient = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
});

let refreshTimeoutId = null;

const scheduleTokenRefresh = () => {
  const token = localStorage.getItem("token");
  if (!token) {
    console.log("[TokenRefresh] Brak tokena do odświeżenia");
    return;
  }

  const payload = parseJwt(token);
  if (!payload || !payload.exp) {
    console.log("[TokenRefresh] Nie można odczytać exp z tokena");
    return;
  }

  const expTime = payload.exp * 1000; // czas wygaśnięcia w ms
  const now = Date.now();

  // Odświeżaj 5 sekund przed wygaśnięciem
  let delay = expTime - now - 5 * 1000;

  if (delay <= 0) {
    console.log(
      "[TokenRefresh] Token już wygasł lub za chwilę wygaśnie — odświeżam teraz"
    );
    delay = 3000; // odczekaj 3 sekundy przed kolejną próbą, żeby nie robić zapętlenia
  } else {
    console.log(
      `[TokenRefresh] Zaplanowano odświeżanie tokena za ${Math.round(
        delay / 1000
      )} sekund`
    );
  }

  if (refreshTimeoutId) clearTimeout(refreshTimeoutId);
  refreshTimeoutId = setTimeout(refreshToken, delay);
};

const refreshToken = async () => {
  console.log("[TokenRefresh] Próbuję odświeżyć token...");
  try {
    const response = await refreshClient.post("/api/refresh");
    const newToken = response.data.token;
    if (newToken) {
      localStorage.setItem("token", newToken);
      console.log("[TokenRefresh] Token odświeżony pomyślnie");
      scheduleTokenRefresh();
    } else {
      console.warn("[TokenRefresh] Otrzymano pusty token podczas odświeżania");
      localStorage.removeItem("token");
    }
  } catch (err) {
    console.error("[TokenRefresh] Błąd podczas odświeżania tokena:", err);
    localStorage.removeItem("token");
  }
};

scheduleTokenRefresh();

axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log("[Request] Dodano token do nagłówków");
    }
    return config;
  },
  (error) => Promise.reject(error)
);

axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      console.log("[Response] 401 Unauthorized - próbuję odświeżyć token");
      originalRequest._retry = true;

      try {
        const response = await refreshClient.post("/api/refresh");
        const newToken = response.data.token;

        if (newToken) {
          localStorage.setItem("token", newToken);
          console.log("[Response] Token odświeżony w interceptorze");

          originalRequest.headers.Authorization = `Bearer ${newToken}`;

          scheduleTokenRefresh();

          return axiosClient(originalRequest);
        } else {
          console.warn(
            "[Response] Brak tokena podczas odświeżania w interceptorze"
          );
          localStorage.removeItem("token");
        }
      } catch (refreshError) {
        console.error(
          "[Response] Błąd odświeżania tokena w interceptorze:",
          refreshError
        );
        localStorage.removeItem("token");
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosClient;
