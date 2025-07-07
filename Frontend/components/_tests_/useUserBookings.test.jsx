import { render, screen, waitFor } from "@testing-library/react";
import { useUserBookings } from "../../hooks/useUserBookings";
import React from "react";

// Mock fetch globally
global.fetch = jest.fn();

const mockBookings = [
  {
    checkOutDate: new Date(Date.now() + 1000 * 60 * 60 * 24).toISOString(), // przyszłość
  },
  {
    checkOutDate: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(), // przeszłość
  },
];

// Testowy komponent do użycia hooka
const TestComponent = ({ token }) => {
  const { currentBookings, pastBookings } = useUserBookings(token);

  return (
    <div>
      <div data-testid="current">{currentBookings.length}</div>
      <div data-testid="past">{pastBookings.length}</div>
    </div>
  );
};

describe("useUserBookings", () => {
  beforeEach(() => {
    fetch.mockResolvedValue({
      ok: true,
      json: async () => mockBookings,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("separuje rezerwacje aktualne i przeszłe", async () => {
    render(<TestComponent token="mock-token" />);

    await waitFor(() => {
      expect(screen.getByTestId("current").textContent).toBe("1");
      expect(screen.getByTestId("past").textContent).toBe("1");
    });

    expect(fetch).toHaveBeenCalledWith("http://localhost:8080/bookings", {
      method: "GET",
      headers: { Authorization: "Bearer mock-token" },
    });
  });
});
