import React, { createContext, useState, useContext } from 'react';

interface UserContextData {
  userId: number | null;
  userName: string;
  userEmail: string;
  loginUser: (id: number, name: string, email: string) => void;
  logoutUser: () => void;
}

const UserContext = createContext<UserContextData>({} as UserContextData);

export const UserProvider = ({ children }: { children: React.ReactNode }) => {
  const [userId, setUserId] = useState<number | null>(null);
  const [userName, setUserName] = useState('');
  const [userEmail, setUserEmail] = useState('');

  const loginUser = (id: number, name: string, email: string) => {
    setUserId(id);
    setUserName(name);
    setUserEmail(email);
  };

  const logoutUser = () => {
    setUserId(null);
    setUserName('');
    setUserEmail('');
  };

  return (
    <UserContext.Provider value={{ userId, userName, userEmail, loginUser, logoutUser }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => useContext(UserContext);