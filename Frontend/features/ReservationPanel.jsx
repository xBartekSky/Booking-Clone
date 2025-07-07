import { useEffect, useState } from "react";
import styles from "/styles/ReservationPanel.module.css";
import Modal from "../components/Modal";
import { AddHotelForm } from "./forms/AddHotelForm";
import { ConfirmChoose } from "../components/ConfirmChoose";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/Button";
import { deleteHotel, fetchOwnHotels } from "../api/hotelApi";

export const ReservationPanel = ({ user }) => {
  const [hotels, setHotels] = useState([]);
  const [showAddForm, setShowAddForm] = useState(false);
  const [showConfirmForm, setShowConfirmForm] = useState(false);
  const [hotelToDeleteId, setHotelToDeleteId] = useState(null);
  const nav = useNavigate();

  const closeModal = () => {
    setShowAddForm(false);
    setShowConfirmForm(false);
    setHotelToDeleteId(null);
  };

  const handleHotelAdded = (newHotel) => {
    console.log("Update");
    setHotels((prevHotels) => [...prevHotels, newHotel]);
  };

  const handleManage = (id) => {
    nav(`/manageHotel/${id}`);
  };

  const handleDelete = async (id) => {
    try {
      await deleteHotel(id);
      setHotels((prevHotels) => prevHotels.filter((hotel) => hotel.id !== id));

      closeModal();
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    const loadHotels = async () => {
      try {
        const data = await fetchOwnHotels();
        setHotels(data);
        console.log(data);
      } catch (err) {
        console.error(err);
      }
    };

    loadHotels();
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const fetchOwnHotels = async () => {
      try {
        const response = await fetch("http://localhost:8080/hotels/myHotels", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.ok) {
          const data = await response.json();
          setHotels(data);
          console.log(data);
        } else {
          console.log("Błąd podczas ładowania hoteli");
        }
      } catch (error) {
        console.log(error);
      }
    };

    fetchOwnHotels();
  }, []);

  return (
    <div className={styles.container}>
      <div className={styles.panel}>
        <label className={styles.panelTitle}>Twoje obiekty</label>

        <Modal isOpen={showAddForm} onClose={closeModal}>
          <AddHotelForm user={user} onHotelAdded={handleHotelAdded} />
        </Modal>

        <div className={styles.hotelContainer}>
          {hotels.map((key) => (
            <div key={key.id} className={styles.hotelContent}>
              <div className={styles.imageContainer}>
                <div className={styles.imageSize}>
                  <img
                    src={
                      key.mainImageUrl
                        ? `http://localhost:8080${key.mainImageUrl}`
                        : "src/assets/nowe_zdjecie.webp"
                    }
                    className={styles.image}
                    alt=""
                  />
                </div>
              </div>
              <div className={styles.hotelDetails}>
                <a href="" className={styles.hotelTitle}>
                  {key.city ?? "Brak tytułu"}
                </a>

                <a href="" className={styles.hotelSubTitle}>
                  {key.description
                    ? key.description.split(",")[0] + "."
                    : "Brak opisu"}
                </a>
                <Button
                  label="Zarządzaj"
                  onClick={() => handleManage(key.id)}
                  variant="green"
                />
                <Button
                  label="Usuń"
                  onClick={() => {
                    setHotelToDeleteId(key.id);
                    setShowConfirmForm(true);
                  }}
                  variant="red"
                />
              </div>
            </div>
          ))}
        </div>

        <Modal
          isOpen={showConfirmForm}
          onClose={() => {
            closeModal();
          }}
        >
          <ConfirmChoose
            label="Czy napewno chcesz usunać ten obiekt?"
            onClick={() => {
              handleDelete(hotelToDeleteId);
            }}
            onCancel={() => {
              closeModal();
            }}
          />
        </Modal>

        <Button label="Dodaj obiekt" onClick={() => setShowAddForm(true)} />
      </div>
    </div>
  );
};
