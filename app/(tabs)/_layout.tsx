import { FontAwesome5 } from '@expo/vector-icons'; // Usaremos ícones do FontAwesome
import { Tabs } from 'expo-router';
import React from 'react';

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false, // Vamos esconder o cabeçalho padrão
        tabBarActiveTintColor: '#007BFF', // Cor do ícone ativo
        tabBarInactiveTintColor: 'gray', // Cor dos ícones inativos
        tabBarStyle: {
            height: 60,
            paddingBottom: 10,
        }
      }}
    >
      <Tabs.Screen
        name="agenda" // Corresponde ao arquivo agenda.tsx
        options={{
          title: 'Agenda',
          tabBarIcon: ({ color }) => <FontAwesome5 name="calendar-alt" size={24} color={color} />,
        }}
      />
      <Tabs.Screen
        name="graficos" // Corresponde ao arquivo graficos.tsx
        options={{
          title: 'Gráficos',
          tabBarIcon: ({ color }) => <FontAwesome5 name="chart-bar" size={24} color={color} />,
        }}
      />
      <Tabs.Screen
        name="metas" // Corresponde ao arquivo metas.tsx
        options={{
          title: 'Metas',
          tabBarIcon: ({ color }) => <FontAwesome5 name="trophy" size={24} color={color} />,
        }}
      />
      <Tabs.Screen
        name="perfil" // Corresponde ao arquivo perfil.tsx
        options={{
          title: 'Perfil',
          tabBarIcon: ({ color }) => <FontAwesome5 name="user-alt" size={24} color={color} />,
        }}
      />
    </Tabs>
  );
}