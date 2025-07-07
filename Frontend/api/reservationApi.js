import axiosClient from "./axiosClient";

export const fetchUnavailableDates = async (roomId) => {
  try {
    const response = await axiosClient.get(
      `/rooms/${roomId}/unavailable-dates`
    );
    if (response.status !== 200) {
      throw new Error("Błąd podczas ładowania niedostępnych dat.");
    }
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || "Wystąpił błąd.");
  }
};
