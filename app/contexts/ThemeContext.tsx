import React, { createContext, useContext, useState } from "react";

type ThemeContextType = {
  modoEscuro: boolean;
  setModoEscuro: (value: boolean) => void;
};

const ThemeContext = createContext<ThemeContextType>({
  modoEscuro: false,
  setModoEscuro: () => {},
});

export const ThemeProvider = ({ children }: { children: React.ReactNode }) => {
  const [modoEscuro, setModoEscuro] = useState(false);
  return (
    <ThemeContext.Provider value={{ modoEscuro, setModoEscuro }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = () => useContext(ThemeContext);
