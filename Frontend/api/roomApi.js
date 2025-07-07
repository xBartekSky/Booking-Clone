import axiosClient from "./axiosClient";
export const addRoom = async (roomData) => {
  try {
    const response = await axiosClient.post("/rooms/", roomData);
    if (response.status !== 200 && response.status !== 201) {
      throw new Error("Błąd podczas dodawania pokoju.");
    }
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || "Wystąpił błąd.");
  }
};

export const fetchHotelRooms = async (hotelId, cancelToken) => {
  try {
    const response = await axiosClient.get(`/hotels/${hotelId}/rooms`, {
      cancelToken,
    });
    if (response.status !== 200) {
      throw new Error("Błąd podczas pobierania listy pokoi.");
    }
    return response.data;
  } catch (error) {
    throw new Error(
      error.message || "Wystąpił błąd podczas pobierania listy pokoi."
    );
  }
};

export const getRoomById = async (roomId) => {
  try {
    const response = await axiosClient.get(`/rooms/${roomId}`);
    return response.data;
  } catch (error) {
    console.error("Błąd pobierania pokoju:", error);
    throw error;
  }
};

export const updateRoom = async (roomId, roomData) => {
  try {
    const response = await axiosClient.put(`/rooms/${roomId}`, roomData);
    if (response.status !== 200 && response.status !== 204) {
      throw new Error("Błąd podczas aktualizacji pokoju.");
    }
    return response.data;
  } catch (error) {
    throw new Error(
      error.message || "Wystąpił błąd podczas aktualizacji pokoju."
    );
  }
};
