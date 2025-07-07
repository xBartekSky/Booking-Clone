import axiosClient from "./axiosClient";
import { refreshAccessToken } from "./refreshToken";

axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const newToken = await refreshAccessToken();
        originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
        return axiosClient(originalRequest);
      } catch (refreshError) {
        console.error("Refresh token failed:", refreshError);
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);
