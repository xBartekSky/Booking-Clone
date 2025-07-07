import { useSearchParams } from "react-router-dom";
import { LoggedUserHeader } from "../components/LoggedUserHeader";
import { SearchBar } from "../components/SearchBar";
import { Wrapper } from "../components/Wrapper";
import { HotelList } from "../features/HotelList";
import { MainPageNav } from "../features/MainPageNav";
import { useUser } from "../context/UserContext";
import { GuestHeader } from "../components/GuestHeader";
import styles from "/styles/MainPage.module.css";
import { Footer } from "../components/Footer";

export const MainPage = () => {
  const [searchParams] = useSearchParams();
  const { user } = useUser();

  const params = {
    city: searchParams.get("city") || "",
    checkIn: searchParams.get("checkIn") || "",
    checkOut: searchParams.get("checkOut") || "",
    guests: searchParams.get("guests") || 1,
  };

  return (
    <div className={styles.container}>
      {user ? <LoggedUserHeader name={user?.username} /> : <GuestHeader />}
      <div>
        <MainPageNav />
        <Wrapper
          title="Zaplanuj swój urlop już dziś..."
          subtitle="Szukaj ofert hoteli i wielu innych obiektów"
        />
        <SearchBar />
      </div>
      <div className={styles.article}>
        <HotelList searchParams={params} />
      </div>
      <div className={styles.footer}>
        <Footer adLabel="Zniżka 15% dla nowych użytkowników!" />
      </div>
    </div>
  );
};
