import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { ReservationPanel } from "../../features/ReservationPanel";
import { MemoryRouter } from "react-router-dom";

// Mock modal, button, confirm, addHotelForm
jest.mock(
  "../../components/Modal",
  () =>
    ({ children, isOpen }) =>
      isOpen ? <div data-testid="modal">{children}</div> : null
);
jest.mock("../../components/Button", () => ({
  Button: ({ label, onClick }) => <button onClick={onClick}>{label}</button>,
}));
jest.mock("../../components/ConfirmChoose", () => ({
  ConfirmChoose: ({ label, onClick, onCancel }) => (
    <div>
      <p>{label}</p>
      <button onClick={onClick}>Potwierdź</button>
      <button onClick={onCancel}>Anuluj</button>
    </div>
  ),
}));
jest.mock("../../features/forms/AddHotelForm", () => ({
  AddHotelForm: ({ onHotelAdded }) => (
    <div>
      <p>Formularz dodawania</p>
      <button onClick={() => onHotelAdded({ id: 999, city: "Test City" })}>
        Dodaj testowy hotel
      </button>
    </div>
  ),
}));

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

describe("ReservationPanel", () => {
  const user = { email: "test@example.com" };

  beforeEach(() => {
    localStorage.setItem("token", "test-token");

    global.fetch = jest.fn((url, options) => {
      if (url.includes("/myHotels")) {
        return Promise.resolve({
          ok: true,
          json: () =>
            Promise.resolve([
              {
                id: 1,
                city: "Warszawa",
                description: "Świetny hotel",
                mainImageUrl: "/img.jpg",
              },
            ]),
        });
      }

      if (url.includes("/deleteHotel")) {
        return Promise.resolve({ ok: true });
      }

      return Promise.resolve({ ok: false });
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders fetched hotels", async () => {
    render(
      <MemoryRouter>
        <ReservationPanel user={user} />
      </MemoryRouter>
    );

    expect(await screen.findByText("Warszawa")).toBeInTheDocument();
    expect(screen.getByText("Świetny hotel.")).toBeInTheDocument();
    expect(screen.getByText("Zarządzaj")).toBeInTheDocument();
    expect(screen.getByText("Usuń")).toBeInTheDocument();
  });

  it("opens and adds new hotel via modal", async () => {
    render(
      <MemoryRouter>
        <ReservationPanel user={user} />
      </MemoryRouter>
    );

    fireEvent.click(screen.getByText("Dodaj obiekt"));

    expect(await screen.findByText("Formularz dodawania")).toBeInTheDocument();

    fireEvent.click(screen.getByText("Dodaj testowy hotel"));

    expect(await screen.findByText("Test City")).toBeInTheDocument();
  });

  it("navigates to manage page when clicking Zarządzaj", async () => {
    render(
      <MemoryRouter>
        <ReservationPanel user={user} />
      </MemoryRouter>
    );

    fireEvent.click(await screen.findByText("Zarządzaj"));
    expect(mockNavigate).toHaveBeenCalledWith("/manageHotel/1");
  });

  it("deletes hotel after confirmation", async () => {
    render(
      <MemoryRouter>
        <ReservationPanel user={user} />
      </MemoryRouter>
    );

    fireEvent.click(await screen.findByText("Usuń"));
    expect(
      await screen.findByText("Czy napewno chcesz usunać ten obiekt?")
    ).toBeInTheDocument();

    fireEvent.click(screen.getByText("Potwierdź"));

    await waitFor(() =>
      expect(fetch).toHaveBeenCalledWith(
        "http://localhost:8080/hotels/deleteHotel/1",
        expect.objectContaining({
          method: "DELETE",
          headers: expect.objectContaining({
            Authorization: "Bearer test-token",
          }),
        })
      )
    );

    await waitFor(() => {
      expect(screen.queryByText("Warszawa")).not.toBeInTheDocument();
    });
  });

  it("closes confirm modal when anuluj clicked", async () => {
    render(
      <MemoryRouter>
        <ReservationPanel user={user} />
      </MemoryRouter>
    );

    fireEvent.click(await screen.findByText("Usuń"));

    fireEvent.click(screen.getByText("Anuluj"));

    await waitFor(() => {
      expect(
        screen.queryByText("Czy napewno chcesz usunać ten obiekt?")
      ).not.toBeInTheDocument();
    });
  });
});
