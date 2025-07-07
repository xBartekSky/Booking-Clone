import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { LoginForm } from "../../features/forms/LoginForm";
import { UserContext } from "../../context/UserContext";
import { BrowserRouter } from "react-router-dom";

// Mock fetch i useNavigate
global.fetch = jest.fn();

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

describe("LoginForm", () => {
  const setUser = jest.fn();

  beforeEach(() => {
    fetch.mockClear();
    setUser.mockClear();
    mockNavigate.mockClear();
  });

  function renderWithContext() {
    return render(
      <BrowserRouter>
        <UserContext.Provider value={{ setUser }}>
          <LoginForm />
        </UserContext.Provider>
      </BrowserRouter>
    );
  }

  test("pokazuje błędy walidacji gdy email lub hasło są niepoprawne", async () => {
    renderWithContext();

    // Kliknij submit bez uzupełniania pól
    fireEvent.click(screen.getByRole("button", { name: /zaloguj się/i }));

    expect(
      await screen.findByText(/podaj poprawny adres e-mail/i)
    ).toBeInTheDocument();
    expect(screen.getByText(/hasło nie może być puste/i)).toBeInTheDocument();
  });

  test("logowanie udane - ustawia usera i przekierowuje", async () => {
    renderWithContext();

    // Mock odpowiedzi fetch dla loginu
    fetch
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ token: "test-token" }),
      })
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ id: 1, name: "Jan Kowalski" }),
      });

    fireEvent.change(screen.getByPlaceholderText(/podaj adres e-mail/i), {
      target: { value: "test@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/podaj hasło/i), {
      target: { value: "password123" },
    });

    fireEvent.click(screen.getByRole("button", { name: /zaloguj się/i }));

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledTimes(2);
      expect(localStorage.getItem("token")).toBe("test-token");
      expect(setUser).toHaveBeenCalledWith({ id: 1, name: "Jan Kowalski" });
      expect(mockNavigate).toHaveBeenCalledWith("/dashboard");
    });
  });

  test("logowanie nieudane - pokazuje błąd", async () => {
    renderWithContext();

    fetch.mockResolvedValueOnce({
      ok: false,
    });

    fireEvent.change(screen.getByPlaceholderText(/podaj adres e-mail/i), {
      target: { value: "wrong@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/podaj hasło/i), {
      target: { value: "wrongpass" },
    });

    fireEvent.click(screen.getByRole("button", { name: /zaloguj się/i }));

    expect(
      await screen.findByText(/nieprawidłowy e-mail lub hasło/i)
    ).toBeInTheDocument();
  });
});
