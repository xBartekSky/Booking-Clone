import "/styles/InputField.css";

export const InputField = ({
  label,
  type,
  onChange,
  placeholder = label,
  iconName,
  inputClassName,
  error,
  multiline = false,
  maxLength = 256,
  value,
  name,
}) => {
  return (
    <div className="inputField">
      <label className="inputLabel">
        <i className={iconName} style={{ color: "gray" }}></i> {label}
      </label>

      {multiline ? (
        <textarea
          className={`input ${inputClassName}`}
          placeholder={placeholder}
          onChange={onChange}
          maxLength={maxLength}
          value={value}
          rows={4}
        />
      ) : (
        <input
          type={type}
          className={`input ${inputClassName}`}
          placeholder={placeholder}
          onChange={onChange}
          maxLength={maxLength}
          value={value}
          name={name}
        />
      )}

      {error && <div className="errorText">{error}</div>}
    </div>
  );
};
