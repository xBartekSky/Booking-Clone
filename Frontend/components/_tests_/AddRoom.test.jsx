import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { AddRoomForm } from "../../features/forms/AddRoom";

// Mock komponentów InputField i Button
jest.mock("../../components/InputField", () => ({
  InputField: ({ label, type = "text", onChange, value, placeholder }) => (
    <label>
      {label}
      <input
        type={type}
        onChange={onChange}
        value={value}
        placeholder={placeholder}
        data-testid={label}
      />
    </label>
  ),
}));

jest.mock("../../components/Button", () => ({
  Button: ({ type, label }) => <button type={type}>{label}</button>,
}));

describe("AddRoomForm", () => {
  const hotelId = 123;

  beforeEach(() => {
    localStorage.setItem("token", "test-token");
    global.fetch = jest.fn().mockResolvedValue({ ok: true });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders all input fields", () => {
    render(<AddRoomForm hotelId={hotelId} />);

    expect(screen.getByTestId("Opis pokoju")).toBeInTheDocument();
    expect(screen.getByTestId("Cena za noc")).toBeInTheDocument();
    expect(screen.getByTestId("Numer pokoju")).toBeInTheDocument();
    expect(screen.getByTestId("Standard pokoju")).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /Dodaj pokój/i })
    ).toBeInTheDocument();
  });

  it("submits room data correctly", async () => {
    render(<AddRoomForm hotelId={hotelId} />);

    fireEvent.change(screen.getByTestId("Opis pokoju"), {
      target: { value: "Pokój testowy z widokiem" },
    });

    fireEvent.change(screen.getByTestId("Cena za noc"), {
      target: { value: "350" },
    });

    fireEvent.change(screen.getByTestId("Numer pokoju"), {
      target: { value: "101" },
    });

    fireEvent.change(screen.getByTestId("Standard pokoju"), {
      target: { value: "Deluxe" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Dodaj pokój/i }));

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledWith("http://localhost:8080/rooms/", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer test-token",
        },
        body: JSON.stringify({
          description: "Pokój testowy z widokiem",
          pricePerNight: "350",
          roomNumber: "101",
          roomType: "Deluxe",
          hotelId: 123,
        }),
      });
    });
  });
});
