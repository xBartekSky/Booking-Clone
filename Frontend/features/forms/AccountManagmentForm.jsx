import { useState } from "react";
import styles from "/styles/AccountManagementForm.module.css";
import { InputField } from "../../components/InputField";
import { Button } from "../../components/Button";
import { updateUserInfo } from "../../api/authApi";

export const AccountManagementForm = ({ user, onClose }) => {
  const [formData, setFormData] = useState({
    username: user?.username || "",
    email: user?.email || "",
    name: user?.name || "",
    surname: user?.surname || "",
    address: user?.address || "",
    phoneNumber: user?.phoneNumber || "",
  });

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const updatedUser = await updateUserInfo(formData);
      console.log("Zaktualizowano dane użytkownika:", updatedUser);
      onClose();
    } catch (error) {
      alert("Wystąpił błąd przy zapisie danych." + error);
    }
  };

  return (
    <form className={styles.form} onSubmit={handleSubmit}>
      <h2>Zarządzaj kontem</h2>

      <InputField
        label="Nazwa użytkownika"
        type="text"
        iconName="fas fa-user"
        value={formData.username}
        onChange={handleChange}
        inputClassName={styles.input}
        placeholder="Nazwa użytkownika"
        name="username"
      />

      <InputField
        label="E-mail"
        type="email"
        iconName="fas fa-envelope"
        value={formData.email}
        onChange={handleChange}
        inputClassName={styles.input}
        placeholder="E-mail"
        name="email"
      />

      <InputField
        label="Imię"
        type="text"
        iconName="fas fa-id-card"
        value={formData.name}
        onChange={handleChange}
        inputClassName={styles.input}
        placeholder="Imię"
        name="name"
      />

      <InputField
        label="Nazwisko"
        type="text"
        iconName="fas fa-id-card-alt"
        value={formData.surname}
        onChange={handleChange}
        inputClassName={styles.input}
        placeholder="Nazwisko"
        name="surname"
      />

      <InputField
        label="Adres"
        type="text"
        iconName="fas fa-map-marker-alt"
        value={formData.address}
        onChange={handleChange}
        inputClassName={styles.input}
        placeholder="Adres"
        name="address"
      />

      <InputField
        label="Numer telefonu"
        type="tel"
        iconName="fas fa-phone"
        value={formData.phoneNumber}
        onChange={handleChange}
        inputClassName={styles.input}
        placeholder="Numer telefonu"
        name="phoneNumber"
      />

      <div className={styles.buttons}>
        <Button type="submit" variant="green" label="Zapisz"></Button>
        <Button
          type="button"
          onClick={onClose}
          variant="red"
          label="Anuluj"
        ></Button>
      </div>
    </form>
  );
};

export default AccountManagementForm;
