// DateRangePicker.jsx
import { useState, useRef, useEffect } from "react";
import { DateRange } from "react-date-range";
import { format } from "date-fns";
import "react-date-range/dist/styles.css";
import "react-date-range/dist/theme/default.css";

export const DateRangePicker = ({ onSelect, className }) => {
  const [range, setRange] = useState([
    {
      startDate: new Date(),
      endDate: new Date(),
      key: "selection",
    },
  ]);
  const [open, setOpen] = useState(false);
  const ref = useRef();

  useEffect(() => {
    const onClickOutside = (e) => {
      if (ref.current && !ref.current.contains(e.target)) {
        setOpen(false);
      }
    };
    window.addEventListener("mousedown", onClickOutside);
    return () => window.removeEventListener("mousedown", onClickOutside);
  }, []);

  const handleSelect = (ranges) => {
    setRange([ranges.selection]);
    onSelect?.(ranges.selection);
  };

  const popupStyle = {
    position: "absolute",
    top: "100%",
    left: "50%",
    transform: "translateX(-50%)",
    zIndex: 1000,
    background: "#fff",
    boxShadow: "0 8px 24px rgba(0,0,0,0.1)",
    borderRadius: "8px",
    marginTop: "4px",
  };

  return (
    <div
      ref={ref}
      style={{
        position: "relative",
        display: "inline-block",
        width: "auto",
      }}
    >
      <input
        type="text"
        readOnly
        className={className}
        onClick={() => setOpen((o) => !o)}
        value={`${format(range[0].startDate, "yyyy-MM-dd")} — ${format(
          range[0].endDate,
          "yyyy-MM-dd"
        )}`}
        style={{ width: "100%", display: "block" }}
      />

      {open && (
        <div style={popupStyle}>
          <DateRange
            editableDateInputs
            onChange={handleSelect}
            moveRangeOnFirstSelection={false}
            ranges={range}
            minDate={new Date()}
          />
        </div>
      )}
    </div>
  );
};
