import axiosClient from "./axiosClient";
export const loginUser = async (email, password) => {
  try {
    const response = await axiosClient.post("/api/login", { email, password });
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};

export const fetchUserInfo = async () => {
  try {
    const response = await axiosClient.get("/userinfo");
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};

export const registerUser = async ({ email, password, username }) => {
  try {
    const response = await axiosClient.post("/register", {
      email,
      password,
      username,
    });
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};

export const updateUserInfo = async (userInfo) => {
  try {
    const response = await axiosClient.put("/userinfo", userInfo);
    return response.data;
  } catch (error) {
    console.error("Błąd podczas aktualizacji danych użytkownika:", error);
    throw error;
  }
};
