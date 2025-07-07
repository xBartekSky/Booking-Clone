import React, { useEffect, useState } from "react";
import styles from "/styles/GetReservation.module.css";
import { InputField } from "../InputField";
import { Button } from "../Button";
import { useUser } from "../../context/UserContext";
import { useLocation, useParams } from "react-router-dom";
import { createBooking } from "../../api/bookingApi";
import { getRoomById } from "../../api/roomApi";
import { FaMoneyBillWave, FaCreditCard } from "react-icons/fa";

export const GetReservation = () => {
  const { state } = useLocation();
  const { roomNumber, price, roomType, checkIn, checkOut } = state || {};
  const { user } = useUser();
  const [roomData, setRoomData] = useState(null);
  const { id: roomId } = useParams();
  const [onlinePayment, setOnlinePayment] = useState(true);
  const formatDate = (dateStr) => new Date(dateStr).toISOString().split("T")[0];

  const [formData, setFormData] = useState({
    checkInDate: checkIn || "",
    checkOutDate: checkOut || "",
    firstName: user?.name || "",
    lastName: user?.surname || "",
    phone: user?.phone || "",
    address: user?.address || "",
    email: user?.email || "",
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: "" }));
    setMessage("");
  };

  useEffect(() => {
    const fetchRoom = async () => {
      try {
        const room = await getRoomById(roomId);
        setRoomData(room);
      } catch (err) {
        console.error("Nie udało się pobrać pokoju:", err);
      }
    };
    if (roomId) fetchRoom();
  }, [roomId]);

  const validateForm = () => {
    const errs = {};
    if (!formData.checkInDate) errs.checkInDate = "Wybierz datę przyjazdu";
    if (!formData.checkOutDate) errs.checkOutDate = "Wybierz datę wyjazdu";
    if (!formData.firstName.trim()) errs.firstName = "Imię jest wymagane";
    if (!formData.lastName.trim()) errs.lastName = "Nazwisko jest wymagane";
    if (!formData.phone.trim()) errs.phone = "Telefon jest wymagany";
    if (!formData.address.trim()) errs.address = "Adres jest wymagany";
    if (!formData.email.trim()) errs.email = "E-mail jest wymagany";
    else if (!/^\S+@\S+\.\S+$/.test(formData.email))
      errs.email = "Niepoprawny format e-mail";

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    try {
      const data = await createBooking({
        roomId,
        checkInDate: formatDate(formData.checkInDate),
        checkOutDate: formatDate(formData.checkOutDate),
        paymentRequired: onlinePayment,
        userId: user?.id,
        firstName: formData.firstName,
        lastName: formData.lastName,
        phone: formData.phone,
        address: formData.address,
        email: formData.email,
        hotelId: roomData?.hotelId,
      });

      if (onlinePayment && data.checkoutUrl) {
        setMessage("Rezerwacja utworzona! Przekierowuję do płatności...");
        window.location.href = data.checkoutUrl;
      } else {
        setMessage("Rezerwacja utworzona! Płatność gotówką przy zameldowaniu.");
      }
    } catch (err) {
      console.error("Błąd rezerwacji:", err);
      setMessage(err.message || "Wystąpił błąd podczas rezerwacji.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.panel}>
        <form className={styles.form} onSubmit={handleSubmit}>
          <h1 className={styles.title}>Ostatni krok rezerwacji</h1>
          {message && <p className={styles.message}>{message}</p>}
          <div className={styles.inputs}>
            <InputField
              label="Data przyjazdu"
              type="date"
              value={formData.checkInDate}
              iconName="calendar"
              error={errors.checkInDate}
            />
            <InputField
              label="Data wyjazdu"
              type="date"
              value={formData.checkOutDate}
              iconName="calendar"
              error={errors.checkOutDate}
            />
            <InputField
              label="Imię"
              type="text"
              value={formData.firstName}
              onChange={(e) => handleChange("firstName", e.target.value)}
              placeholder="Wpisz imię"
              iconName="user"
              error={errors.firstName}
            />
            <InputField
              label="Nazwisko"
              type="text"
              value={formData.lastName}
              onChange={(e) => handleChange("lastName", e.target.value)}
              placeholder="Wpisz nazwisko"
              iconName="user"
              error={errors.lastName}
            />
            <InputField
              label="Telefon"
              type="tel"
              value={formData.phone}
              onChange={(e) => handleChange("phone", e.target.value)}
              placeholder="Wpisz numer telefonu"
              iconName="phone"
              error={errors.phone}
            />
          </div>
          <div className={styles.inputs}>
            <InputField
              label="Adres"
              type="text"
              value={formData.address}
              onChange={(e) => handleChange("address", e.target.value)}
              placeholder="Wpisz adres"
              iconName="map"
              error={errors.address}
            />
            <InputField
              label="E-mail"
              type="email"
              value={formData.email}
              onChange={(e) => handleChange("email", e.target.value)}
              placeholder="Wpisz adres e-mail"
              iconName="mail"
              error={errors.email}
            />

            <div className={styles.paymentOptions}>
              <label>
                <input
                  type="radio"
                  name="payment"
                  value="online"
                  checked={onlinePayment}
                  onChange={() => setOnlinePayment(true)}
                />
                <FaCreditCard className={styles.paymentIcon} />
                Płatność online
              </label>
              <label>
                <input
                  type="radio"
                  name="payment"
                  value="cash"
                  checked={!onlinePayment}
                  onChange={() => setOnlinePayment(false)}
                />
                <FaMoneyBillWave className={styles.paymentIcon} />
                Płatność gotówką na miejscu
              </label>
            </div>

            <Button
              type="submit"
              label={loading ? "Przetwarzanie..." : "Zarezerwuj"}
              disabled={loading}
            />
          </div>
        </form>
      </div>
    </div>
  );
};

export default GetReservation;
