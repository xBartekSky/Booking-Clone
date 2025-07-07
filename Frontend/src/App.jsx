import { Routes, Route } from "react-router-dom";
import { LoginForm } from "../features/forms/LoginForm";
import { RegisterForm } from "../features/forms/RegisterForm";
import { LoginPage } from "../pages/LoginPage";
import { DashboardPage } from "../pages/DashboardPage";
import { MainPage } from "../pages/MainPage";
import { RegisterPage } from "../pages/RegisterPage";
import { useEffect, useState } from "react";
import { UserContext } from "../context/UserContext";
import { AddHotelForm } from "../features/forms/AddHotelForm";
import { ConfirmChoose } from "../components/ConfirmChoose";
import { HotelDetails } from "../pages/HotelDetails";
import { Footer } from "../components/Footer";

import { Review } from "../features/Review";
import { ManageHotel } from "../pages/ManageHotel";
import "../api/axiosInterceptor";
import { PrivateRoute } from "../components/PrivateRoute";
import { HotelRooms } from "../features/forms/HotelRooms";
import { RoomCard } from "../components/RoomCard";
import { CheckoutForm } from "../features/forms/CheckoutForm";
import { SuccessPage } from "../pages/SuccessPage";
import GetReservationPage from "../pages/GetReservationPage";

function App() {
  const [user, setUser] = useState(null);
  useEffect(() => {
    const fetchUser = async () => {
      const token = localStorage.getItem("token");
      console.log(token);
      if (!token) return;

      const response = await fetch("http://localhost:8080/userinfo", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const userData = await response.json();
        console.log("Zalogowany użytkownik:", userData);
        setUser(userData);
      } else {
        console.error("Błąd podczas pobierania użytkownika");
      }
    };
    fetchUser();
  }, []);
  return (
    <UserContext.Provider value={{ user, setUser }}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route element={<PrivateRoute />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/manageHotel/:id" element={<ManageHotel />} />
          <Route path="/checkout" element={<CheckoutForm />} />
          <Route path="/successPage" element={<SuccessPage />} />
          <Route
            path="/getReservation/:id"
            element={<GetReservationPage></GetReservationPage>}
          />
        </Route>
        <Route path="/" element={<MainPage />} />
        <Route path="/hotelDetails/:id" element={<HotelDetails />} />

        <Route path="/test" element={<ManageHotel />} />
        <Route path="/test2" element={<HotelRooms />} />
      </Routes>
    </UserContext.Provider>
  );
}

export default App;
