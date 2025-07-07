import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import { HotelRooms } from "../../features/forms/HotelRooms";
import React from "react";

beforeEach(() => {
  jest.spyOn(window, "fetch").mockImplementation(() =>
    Promise.resolve({
      ok: true,
      json: () =>
        Promise.resolve([
          { roomNumber: "101", roomType: "Single", pricePerNight: 100 },
          { roomNumber: "102", roomType: "Double", pricePerNight: 150 },
        ]),
    })
  );

  Storage.prototype.getItem = jest.fn(() => "mocked-token");
});

afterEach(() => {
  jest.restoreAllMocks();
});

describe("HotelRooms", () => {
  test("fetchuje i wyświetla listę pokoi", async () => {
    render(<HotelRooms label="Pokoje hotelu" hotelId="123" />);

    expect(screen.getByText(/Pokoje hotelu/i)).toBeInTheDocument();

    expect(
      screen.getByText(/Żaden pokój jeszcze nie został dodany do tego hotelu./i)
    ).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText(/Single/i)).toBeInTheDocument();
      expect(screen.getByText(/Double/i)).toBeInTheDocument();
      expect(screen.getByText(/101/i)).toBeInTheDocument();
      expect(screen.getByText(/102/i)).toBeInTheDocument();
    });
  });

  test("po kliknięciu w przycisk 'Dodaj pokój' otwiera modal z formularzem", async () => {
    render(<HotelRooms label="Pokoje hotelu" hotelId="123" />);

    const button = screen.getByRole("button", { name: "Dodaj pokój" });
    fireEvent.click(button);

    expect(await screen.findByTestId("add-room-form")).toBeInTheDocument();
  });
});
