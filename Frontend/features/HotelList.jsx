import { useEffect, useState } from "react";
import styles from "/styles/HotelList.module.css";
import { useNavigate } from "react-router-dom";
import { fetchHotels } from "../api/hotelApi";

export const HotelList = ({ searchParams }) => {
  const [allHotels, setAllHotels] = useState([]);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const nav = useNavigate();
  const pageSize = 5;
  const totalPages = Math.ceil(allHotels.length / pageSize);

  useEffect(() => {
    setLoading(true);
    setError(null);

    fetchHotels(searchParams.city)
      .then((data) => {
        setAllHotels(data);
        setPage(0);
      })
      .catch(() => setError("Błąd podczas pobierania hoteli"))
      .finally(() => setLoading(false));
  }, [searchParams]);

  const currentHotels = allHotels.slice(page * pageSize, (page + 1) * pageSize);

  const nextPage = () => {
    if (page < totalPages - 1) setPage(page + 1);
  };

  const prevPage = () => {
    if (page > 0) setPage(page - 1);
  };

  const handleCheckHotel = (hotel) => {
    const query = new URLSearchParams(searchParams).toString();
    nav(`/hotelDetails/${hotel?.id}?${query}`);
  };

  return (
    <div className={styles.container}>
      <div className={styles.app}>
        {loading && <p>Ładowanie hoteli...</p>}
        {error && <p style={{ color: "red" }}>{error}</p>}
        {!loading && !error && currentHotels.length === 0 && (
          <p>Brak hoteli do wyświetlenia</p>
        )}
        {!loading && !error && currentHotels.length > 0 && (
          <>
            <div className={styles.hotelsContainer}>
              {currentHotels.map((hotel) => (
                <div className={styles.hotelContent} key={hotel.id}>
                  <div className={styles.hotelImage}>
                    <div className={styles.imageContainer}>
                      <img
                        className={styles.image}
                        src={
                          hotel.mainImageUrl
                            ? `http://localhost:8080${hotel.mainImageUrl}`
                            : "src/assets/nowe_zdjecie.webp"
                        }
                        alt={hotel.name}
                      />
                    </div>
                  </div>
                  <div className={styles.hotelDesc}>
                    <h3>{hotel.name}</h3>
                    <p>{hotel.city}</p>
                    <p>
                      {hotel.description
                        ? hotel.description.split(",")[0] + "."
                        : "Brak opisu."}
                    </p>

                    <div className={styles.separator}></div>
                    <button
                      className={styles.button}
                      onClick={() => handleCheckHotel(hotel)}
                    >
                      Sprawdź dostępność
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <div className={styles.pagination}>
              <button onClick={prevPage} disabled={page === 0}>
                Poprzednia
              </button>
              <span>
                Strona {page + 1} z {totalPages}
              </span>
              <button onClick={nextPage} disabled={page === totalPages - 1}>
                Następna
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};
