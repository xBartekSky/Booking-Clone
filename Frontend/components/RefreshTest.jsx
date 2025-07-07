import React from "react";
import { refreshTokenTest } from "../api/axiosClient";

export const TestRefreshButton = () => {
  const handleRefreshClick = async () => {
    await refreshTokenTest();
  };

  return (
    <div>
      <button onClick={handleRefreshClick}>Odśwież token ręcznie</button>
    </div>
  );
};

export default TestRefreshButton;
