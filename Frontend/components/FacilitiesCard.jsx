export const FacilitiesCard = ({ icon, label }) => {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        gap: "10px",
        fontSize: "20px",
      }}
    >
      <i
        style={{
          color: "orange",
          fontSize: "40px",
        }}
      >
        {icon}
      </i>
      <span>{label}</span>
    </div>
  );
};
