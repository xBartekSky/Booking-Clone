import { LoggedUserHeader } from "../components/LoggedUserHeader";
import { HelloUser } from "../components/HelloUser";
import styles from "/styles/DashboardPage.module.css";
import { useUser } from "../context/UserContext";
import { MainPageNav } from "../features/MainPageNav";
import { ReservationPanel } from "../features/ReservationPanel";
import { Footer } from "../components/Footer";
import { GuestHeader } from "../components/GuestHeader";
import { useUserBookings } from "../hooks/useUserBookings";
import { useState } from "react";
import Modal from "../components/Modal";
import UserReservation from "../components/UserReservation";
import AccountManagementForm from "../features/forms/AccountManagmentForm";
import TestRefreshButton from "../components/RefreshTest";

export const DashboardPage = () => {
  const { user } = useUser();
  const token = localStorage.getItem("token");
  const { currentBookings, pastBookings } = useUserBookings(token);

  const [showCurrentReservation, setShowCurrentReservation] = useState(false);
  const [showPastReservation, setShowPastReservation] = useState(false);
  const [showAccountManagement, setShowAccountManagement] = useState(false);

  const closeModal = () => {
    setShowCurrentReservation(false);
    setShowPastReservation(false);
    setShowAccountManagement(false);
  };

  return (
    <div className={styles.container}>
      {user ? <LoggedUserHeader name={user?.username} /> : <GuestHeader />}
      <MainPageNav />
      <div className={styles.app}>
        <div className={styles.panel}>
          <div className={styles.helloContainer}>
            <HelloUser name={user?.username} />
          </div>
          <div className={styles.dashboardContent}>
            <ReservationPanel user={user} />

            <div className={styles.contents}>
              {/* Modale */}
              <Modal isOpen={showCurrentReservation} onClose={closeModal}>
                <UserReservation
                  label="Aktualne rezerwacje"
                  bookings={currentBookings}
                />
              </Modal>

              <Modal isOpen={showPastReservation} onClose={closeModal}>
                <UserReservation
                  label="Historia rezerwacji"
                  bookings={pastBookings}
                />
              </Modal>

              <Modal isOpen={showAccountManagement} onClose={closeModal}>
                <AccountManagementForm user={user} onClose={closeModal} />
              </Modal>

              {/* Sekcje */}
              <div className={styles.content}>
                <h1 className={styles.contentTitle}>Moje rezerwacje</h1>
                <button
                  className={styles.contentButton}
                  onClick={() => setShowCurrentReservation(true)}
                >
                  Wyświetl aktualne rezerwacje
                </button>
                <button
                  onClick={() => setShowPastReservation(true)}
                  className={styles.contentButton}
                >
                  Wyświetl historię rezerwacji
                </button>
                <button className={styles.contentButton}>
                  Zgłoś problem dot. rezerwacji
                </button>
              </div>

              <div className={styles.content}>
                <h1 className={styles.contentTitle}>Pomoc</h1>
              </div>

              <div className={styles.content}>
                <h1 className={styles.contentTitle}>Zarządzaj kontem</h1>
                <button
                  onClick={() => setShowAccountManagement(true)}
                  className={styles.contentButton}
                >
                  Edytuj dane konta
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <Footer adLabel="Zniżka 15% dla nowych użytkowników!" />
    </div>
  );
};
