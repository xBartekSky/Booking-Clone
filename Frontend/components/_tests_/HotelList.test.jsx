import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { HotelList } from "../../features/HotelList";
import { useNavigate } from "react-router-dom";

// Mock zależności
jest.mock("react-router-dom", () => ({
  useNavigate: jest.fn(),
}));

// Mock fetch API
global.fetch = jest.fn();

// Mock CSS Modules
jest.mock("/styles/HotelList.module.css", () => ({
  container: "container",
  app: "app",
  hotelsContainer: "hotelsContainer",
  hotelContent: "hotelContent",
  hotelImage: "hotelImage",
  imageContainer: "imageContainer",
  image: "image",
  hotelDesc: "hotelDesc",
  separator: "separator",
  button: "button",
  pagination: "pagination",
}));

describe("Komponent HotelList", () => {
  const mockHotels = [
    {
      id: "1",
      name: "Hotel Warszawa",
      city: "Warszawa",
      description: "Luksusowy hotel w centrum, blisko atrakcji",
      mainImageUrl: "/images/h3.jpg",
    },
    {
      id: "2",
      name: "Hotel Kraków",
      city: "Kraków",
      description: "Przytulny hotel z widokiem na Wawel",
      mainImageUrl: null,
    },
  ];

  const navigate = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    useNavigate.mockReturnValue(navigate);
    fetch.mockReset();
  });

  it("poprawnie renderuje listę hoteli", async () => {
    // Mock odpowiedzi fetch
    fetch.mockResolvedValueOnce({
      ok: true,
      json: jest.fn().mockResolvedValue(mockHotels),
    });

    render(<HotelList searchParams={{ city: "" }} />);

    // Oczekiwanie na załadowanie hoteli
    await waitFor(
      () => {
        expect(screen.getByText("Hotel Warszawa")).toBeInTheDocument();
        expect(screen.getByText("Hotel Kraków")).toBeInTheDocument();
        expect(screen.getByText("Warszawa")).toBeInTheDocument();
        expect(screen.getByText("Kraków")).toBeInTheDocument();
        expect(
          screen.getByText("Luksusowy hotel w centrum.")
        ).toBeInTheDocument();
        expect(
          screen.getByText("Przytulny hotel z widokiem na Wawel.")
        ).toBeInTheDocument();
      },
      { timeout: 3000 }
    );

    // Weryfikacja wywołania fetch
    expect(fetch).toHaveBeenCalledWith("http://localhost:8080/hotels");

    // Sprawdzenie obrazów za pomocą getAllByAltText
    await waitFor(
      async () => {
        const images = screen.getAllByAltText(""); // Puste alt, jak w DOM
        expect(images).toHaveLength(2);
        expect(images[0]).toHaveAttribute(
          "src",
          "http://localhost:8080/images/h3.jpg"
        );
        expect(images[1]).toHaveAttribute(
          "src",
          "src/assets/nowe_zdjecie.webp"
        );
      },
      { timeout: 5000 }
    );
  });

  it("renderuje komunikat o braku hoteli, gdy lista jest pusta", async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      json: jest.fn().mockResolvedValue([]),
    });

    render(<HotelList searchParams={{ city: "" }} />);

    await waitFor(() => {
      expect(
        screen.getByText("Brak hoteli do wyświetlenia")
      ).toBeInTheDocument();
    });
  });

  it("pobiera hotele dla określonego miasta z searchParams", async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      json: jest.fn().mockResolvedValue([mockHotels[0]]),
    });

    render(<HotelList searchParams={{ city: "Warszawa" }} />);

    await waitFor(() => {
      expect(screen.getByText("Hotel Warszawa")).toBeInTheDocument();
    });

    expect(fetch).toHaveBeenCalledWith(
      "http://localhost:8080/hotels/searchByCity?city=Warszawa"
    );
  });

  it('nawiguje do szczegółów hotelu po kliknięciu "Sprawdź dostępność"', async () => {
    fetch.mockResolvedValueOnce({
      ok: true,
      json: jest.fn().mockResolvedValue(mockHotels),
    });

    render(<HotelList searchParams={{ city: "Warszawa" }} />);

    await waitFor(() => {
      expect(screen.getByText("Hotel Warszawa")).toBeInTheDocument();
    });

    fireEvent.click(
      screen.getAllByRole("button", { name: "Sprawdź dostępność" })[0]
    );

    expect(navigate).toHaveBeenCalledWith("/hotelDetails/1?city=Warszawa");
  });

  it("obsługuje błąd fetch i loguje go do konsoli", async () => {
    const consoleErrorSpy = jest.spyOn(console, "error").mockImplementation();

    fetch.mockRejectedValueOnce(new Error("Błąd sieci"));

    render(<HotelList searchParams={{ city: "" }} />);

    await waitFor(() => {
      expect(consoleErrorSpy).toHaveBeenCalledWith("Błąd: ", expect.any(Error));
    });

    consoleErrorSpy.mockRestore();
  });

  it("przyciski paginacji są wyłączone na pierwszej i ostatniej stronie", async () => {
    // Mock większej liczby hoteli, aby uzyskać więcej niż jedną stronę
    const largeHotelList = Array.from({ length: 7 }, (_, i) => ({
      id: `${i + 1}`,
      name: `Hotel ${i + 1}`,
      city: "Miasto",
      description: `Opis hotelu ${i + 1}`,
      mainImageUrl: null,
    }));

    fetch.mockResolvedValueOnce({
      ok: true,
      json: jest.fn().mockResolvedValue(largeHotelList),
    });

    render(<HotelList searchParams={{ city: "" }} />);

    await waitFor(() => {
      expect(screen.getByText("Hotel 1")).toBeInTheDocument();
    });

    const prevButton = screen.getByRole("button", { name: "Poprzednia" });
    const nextButton = screen.getByRole("button", { name: "Następna" });

    // Na pierwszej stronie (page=0)
    expect(prevButton).toBeDisabled();
    expect(nextButton).toBeEnabled();

    // Przejdź na ostatnią stronę
    fireEvent.click(nextButton);

    await waitFor(() => {
      expect(screen.getByText("Strona 2 z 2")).toBeInTheDocument();
    });

    expect(prevButton).toBeEnabled();
    expect(nextButton).toBeDisabled();
  });
});
