import axiosClient from "./axiosClient";

export const addHotel = async (hotelData, mainImageFile, galleryFiles) => {
  try {
    const response = await axiosClient.post("/hotels", hotelData);

    if (mainImageFile) {
      const formData = new FormData();
      formData.append("file", mainImageFile);
      await axiosClient.post(
        `/hotels/${response.data.id}/uploadImage`,
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );
    }

    for (const file of galleryFiles) {
      const galleryData = new FormData();
      galleryData.append("file", file);
      await axiosClient.post(
        `/hotels/${response.data.id}/uploadGalleryImage`,
        galleryData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );
    }

    return response.data;
  } catch (error) {
    console.error(
      "Błąd podczas dodawania hotelu:",
      error.response?.data || error.message
    );
    throw error;
  }
};

export const fetchRooms = async (hotelId) => {
  try {
    const response = await axiosClient.get(
      `/hotels/protected/${hotelId}/rooms`
    );
    return response.data;
  } catch (error) {
    console.error(
      "Błąd podczas pobierania pokoi:",
      error.response?.data || error.message
    );
    throw error;
  }
};

export const fetchHotels = async (city) => {
  try {
    if (city) {
      const response = await axiosClient.get(`/hotels/searchByCity`, {
        params: { city },
      });
      return response.data;
    } else {
      const response = await axiosClient.get("/hotels");
      return response.data;
    }
  } catch (error) {
    console.error(
      "Błąd podczas pobierania hoteli:",
      error.response?.data || error.message
    );
    throw error;
  }
};

export const fetchOwnHotels = async () => {
  try {
    const response = await axiosClient.get("/hotels/myHotels");
    if (response.status !== 200) {
      throw new Error("Błąd podczas ładowania hoteli.");
    }
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || "Wystąpił błąd.");
  }
};

export const deleteHotel = async (id) => {
  try {
    const response = await axiosClient.delete(`/hotels/deleteHotel/${id}`);
    if (response.status !== 200 && response.status !== 204) {
      throw new Error("Błąd podczas usuwania hotelu.");
    }
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || "Wystąpił błąd.");
  }
};

export const updateHotel = async (hotelId, hotelData) => {
  try {
    const response = await axiosClient.put(`/hotels/id/${hotelId}`, hotelData);
    if (response.status !== 200 && response.status !== 204) {
      throw new Error("Błąd podczas aktualizacji hotelu.");
    }
    return response.data;
  } catch (error) {
    throw new Error(
      error.message || "Wystąpił błąd podczas aktualizacji hotelu."
    );
  }
};

export const fetchHotel = async (hotelId, cancelToken) => {
  try {
    const response = await axiosClient.get(`/hotels/id/${hotelId}`, {
      cancelToken,
    });
    if (response.status !== 200) {
      throw new Error("Błąd podczas pobierania danych hotelu.");
    }
    return response.data;
  } catch (error) {
    throw new Error(
      error.message || "Wystąpił błąd podczas pobierania danych hotelu."
    );
  }
};
