import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { AddHotelForm } from "../../features/forms/AddHotelForm";

// Mock InputField
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

// Mock Button
jest.mock("../../components/Button", () => ({
  Button: ({ type, label }) => <button type={type}>{label}</button>,
}));

describe("AddHotelForm", () => {
  const mockUser = {
    email: "test@example.com",
    phoneNumber: "123456789",
  };

  const mockOnHotelAdded = jest.fn();

  beforeEach(() => {
    localStorage.setItem("token", "fake-token");

    global.fetch = jest
      .fn()
      // First fetch: create hotel
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ id: 1 }),
      })
      // Second fetch: upload main image
      .mockResolvedValueOnce({ ok: true })
      // Third fetch: upload gallery image
      .mockResolvedValue({ ok: true }); // for all subsequent gallery uploads
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders input fields and checkboxes", () => {
    render(<AddHotelForm user={mockUser} onHotelAdded={mockOnHotelAdded} />);

    expect(screen.getByTestId("Nazwa hotelu")).toBeInTheDocument();
    expect(screen.getByTestId("Miasto")).toBeInTheDocument();
    expect(screen.getByTestId("Kraj")).toBeInTheDocument();
    expect(screen.getByTestId("Opis obiektu")).toBeInTheDocument();
    expect(screen.getByTestId("Wifi")).toBeInTheDocument();
  });

  it("fills and submits the form", async () => {
    render(<AddHotelForm user={mockUser} onHotelAdded={mockOnHotelAdded} />);

    fireEvent.change(screen.getByTestId("Nazwa hotelu"), {
      target: { value: "Hotel Testowy" },
    });
    fireEvent.change(screen.getByTestId("Miasto"), {
      target: { value: "Warszawa" },
    });
    fireEvent.change(screen.getByTestId("Kraj"), {
      target: { value: "Polska" },
    });
    fireEvent.change(screen.getByTestId("Opis obiektu"), {
      target: { value: "Opis testowy" },
    });

    fireEvent.click(screen.getByTestId("Wifi")); // zaznacz checkbox

    fireEvent.click(screen.getByRole("button", { name: /Dodaj obiekt/i }));

    await waitFor(() =>
      expect(mockOnHotelAdded).toHaveBeenCalledWith(
        expect.objectContaining({ id: 1 })
      )
    );

    // verify that main hotel creation call happened
    expect(fetch).toHaveBeenCalledWith(
      "http://localhost:8080/hotels",
      expect.objectContaining({
        method: "POST",
        headers: expect.objectContaining({
          Authorization: "Bearer fake-token",
        }),
      })
    );
  });
});
