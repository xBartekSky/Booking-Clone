import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { AddReviewForm } from "../../features/forms/AddReviewForm";
import { useUser } from "../../context/UserContext";

// Mock InputField
jest.mock("../../components/InputField", () => ({
  InputField: ({ value, onChange }) => (
    <input
      data-testid="comment-input"
      value={value}
      onChange={onChange}
      placeholder="Wpisz swoją opinię"
    />
  ),
}));

// Mock UserContext
jest.mock("../../context/UserContext", () => ({
  useUser: jest.fn(),
}));

describe("AddReviewForm", () => {
  beforeEach(() => {
    useUser.mockReturnValue({ userId: "123" });
    localStorage.setItem("token", "fake-token");

    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ message: "Dodano opinię!" }),
      })
    );
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it("renders input and stars", () => {
    render(<AddReviewForm idHotel="1" />);

    expect(
      screen.getByPlaceholderText("Wpisz swoją opinię")
    ).toBeInTheDocument();
    expect(screen.getAllByText("★")).toHaveLength(5);
  });

  it("allows user to enter a comment and select rating", () => {
    render(<AddReviewForm idHotel="1" />);

    const input = screen.getByPlaceholderText("Wpisz swoją opinię");
    fireEvent.change(input, { target: { value: "Super hotel!" } });
    expect(input.value).toBe("Super hotel!");

    const stars = screen.getAllByText("★");
    fireEvent.click(stars[3]); // klik na 4. gwiazdkę (index 3)
  });

  it("submits the form and shows success message", async () => {
    render(<AddReviewForm idHotel="1" />);

    const input = screen.getByPlaceholderText("Wpisz swoją opinię");
    fireEvent.change(input, { target: { value: "Świetna obsługa!" } });

    const stars = screen.getAllByText("★");
    fireEvent.click(stars[4]); // ocena 5

    const button = screen.getByRole("button", { name: /Dodaj opinię/i });
    fireEvent.click(button);

    await waitFor(() =>
      expect(screen.getByText("Dodano opinię!")).toBeInTheDocument()
    );

    // Sprawdź, czy fetch został wywołany z poprawnymi parametrami
    expect(global.fetch).toHaveBeenCalledWith(
      "http://localhost:8080/reviews/hotel/1",
      expect.objectContaining({
        method: "POST",
        headers: expect.objectContaining({
          Authorization: "Bearer fake-token",
        }),
      })
    );
  });
});
