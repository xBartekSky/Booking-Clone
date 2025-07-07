import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { RegisterForm } from "../../features/forms/RegisterForm";
import { BrowserRouter } from "react-router-dom";

// Mock useNavigate z react-router-dom
const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

// Mock global fetch
beforeEach(() => {
  jest.spyOn(global, "fetch");
  mockNavigate.mockClear();
});

afterEach(() => {
  global.fetch.mockRestore();
});

function renderWithRouter(ui) {
  return render(<BrowserRouter>{ui}</BrowserRouter>);
}

describe("RegisterForm", () => {
  test("pokazuje błędy walidacji dla niepoprawnych danych", async () => {
    renderWithRouter(<RegisterForm />);

    fireEvent.change(screen.getByPlaceholderText(/Podaj adres e-mail/i), {
      target: { value: "zlyemail" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Powtórz adres e-mail/i), {
      target: { value: "inny@email.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Podaj hasło/i), {
      target: { value: "123" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Zarejestruj/i }));

    expect(
      await screen.findByText(/Podaj poprawny adres e-mail/i)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Podane adresy e-mail nie pasują do siebie/i)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Hasło musi się składać z conajmniej 6 znaków/i)
    ).toBeInTheDocument();
  });

  test("wysyła poprawne dane i nawiguję po sukcesie", async () => {
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ message: "Zarejestrowano" }),
    });

    renderWithRouter(<RegisterForm />);

    fireEvent.change(screen.getByPlaceholderText(/Podaj adres e-mail/i), {
      target: { value: "test@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Powtórz adres e-mail/i), {
      target: { value: "test@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Podaj hasło/i), {
      target: { value: "password123" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Podaj nazwę użytkownika/i), {
      target: { value: "testuser" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Zarejestruj/i }));

    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith(
        "http://localhost:8080/register",
        expect.objectContaining({
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            email: "test@example.com",
            password: "password123",
            username: "testuser",
          }),
        })
      );
    });

    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  test("pokazuje błąd email zajęty przy statusie 409", async () => {
    global.fetch.mockResolvedValueOnce({
      ok: false,
      status: 409,
    });

    renderWithRouter(<RegisterForm />);

    fireEvent.change(screen.getByPlaceholderText(/Podaj adres e-mail/i), {
      target: { value: "taken@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Powtórz adres e-mail/i), {
      target: { value: "taken@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Podaj hasło/i), {
      target: { value: "password123" },
    });
    fireEvent.change(screen.getByPlaceholderText(/Podaj nazwę użytkownika/i), {
      target: { value: "testuser" },
    });

    fireEvent.click(screen.getByRole("button", { name: /Zarejestruj/i }));

    expect(
      await screen.findByText(/Podany adres e-mail jest zajęty/i)
    ).toBeInTheDocument();
  });
});
