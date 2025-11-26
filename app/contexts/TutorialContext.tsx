import React, { createContext, useContext, useState, useEffect } from 'react';
import { useRouter } from 'expo-router';

type TutorialContextType = {
  passoAtual: number;
  iniciarTutorial: () => void;
  proximoPasso: () => void;
  encerrarTutorial: () => void;
  tutorialAtivo: boolean;
};

const TutorialContext = createContext<TutorialContextType>({} as TutorialContextType);

export const TutorialProvider = ({ children }: { children: React.ReactNode }) => {
  const [passoAtual, setPassoAtual] = useState(-1); // -1 significa inativo
  const router = useRouter();

  const iniciarTutorial = () => {
    setPassoAtual(0); // Passo 0: Metas
    router.replace('/(tabs)/metas');
  };

  const proximoPasso = () => {
    const proximo = passoAtual + 1;
    setPassoAtual(proximo);

    // Roteiro de Navegação
    if (proximo === 1) router.push('/(tabs)/agenda');
    if (proximo === 2) router.push('/(tabs)/graficos');
    if (proximo === 3) router.push('/(tabs)/perfil');
    if (proximo > 3) encerrarTutorial();
  };

  const encerrarTutorial = () => {
    setPassoAtual(-1);
    router.replace('/(tabs)/agenda'); // Volta para a tela principal ao fim
  };

  return (
    <TutorialContext.Provider value={{ 
      passoAtual, 
      iniciarTutorial, 
      proximoPasso, 
      encerrarTutorial,
      tutorialAtivo: passoAtual !== -1 
    }}>
      {children}
    </TutorialContext.Provider>
  );
};

export const useTutorial = () => useContext(TutorialContext);