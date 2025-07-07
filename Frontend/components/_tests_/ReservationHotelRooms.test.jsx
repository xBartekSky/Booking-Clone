import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { ReservationHotelRooms } from "../hotelComponents/ReservationHotelRooms";

test("renders available rooms correctly", async () => {
  // Mock fetch itp.
  global.fetch = jest.fn((url) => {
    if (url.includes("/unavailable-dates")) {
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve([]),
      });
    }
    return Promise.reject(new Error("Unknown URL"));
  });

  const rooms = [
    { id: 1, roomNumber: 101, pricePerNight: 100, roomType: "Standard" },
  ];

  render(
    <MemoryRouter>
      <ReservationHotelRooms
        room={rooms}
        checkIn="2025-07-01"
        checkOut="2025-07-10"
      />
    </MemoryRouter>
  );

  await waitFor(() => {
    expect(screen.getByText(/Standard/i)).toBeInTheDocument();
  });

  expect(screen.queryByText(/Brak dostępnych pokoi/i)).not.toBeInTheDocument();
});
