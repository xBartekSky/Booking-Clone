import axiosClient from "./axiosClient";
export const createBooking = async (bookingData) => {
  try {
    const response = await axiosClient.post("/bookings", bookingData);
    if (response.status !== 201) {
      throw new Error("Błąd podczas tworzenia rezerwacji.");
    }
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || "Wystąpił błąd.");
  }
};

export const fetchBookings = async (token, cancelToken) => {
  try {
    const response = await axiosClient.get("/bookings", {
      cancelToken,
    });
    if (response.status !== 200) {
      throw new Error("Błąd podczas pobierania rezerwacji.");
    }
    return response.data;
  } catch (error) {
    throw new Error(
      error.message || "Wystąpił błąd podczas pobierania rezerwacji."
    );
  }
};
