import axiosClient from "./axiosClient";

export const addReview = async (hotelId, reviewData) => {
  try {
    const response = await axiosClient.post(
      `/reviews/hotel/${hotelId}`,
      reviewData
    );
    if (response.status !== 200 && response.status !== 201) {
      throw new Error("Błąd podczas dodawania opinii.");
    }
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || "Wystąpił błąd.");
  }
};

export const fetchHotelReviews = async (hotelId, cancelToken) => {
  try {
    const response = await axiosClient.get(`/reviews/hotel/${hotelId}`, {
      cancelToken,
    });
    if (response.status !== 200) {
      throw new Error("Błąd podczas pobierania opinii hotelu.");
    }
    return response.data;
  } catch (error) {
    throw new Error(
      error.message || "Wystąpił błąd podczas pobierania opinii hotelu."
    );
  }
};
