import axiosClient from "./axiosClient";

export const refreshAccessToken = async () => {
  const response = await axiosClient.post("/api/refresh");
  const newToken = response.data.token;
  localStorage.setItem("token", newToken);
  return newToken;
};
