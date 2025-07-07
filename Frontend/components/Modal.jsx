import styles from "/styles/Modal.module.css";
import ReactDOM from "react-dom";
const Modal = ({ isOpen, onClose, children }) => {
  if (!isOpen) return null;

  return ReactDOM.createPortal(
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <button className={styles.modalClose} onClick={onClose}>
          ×
        </button>
        {children}
      </div>
    </div>,
    document.body
  );
};

export default Modal;
