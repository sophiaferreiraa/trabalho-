import { FontAwesome5 } from '@expo/vector-icons'; 
import { Tabs } from 'expo-router';
import React from 'react';

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false, 
        tabBarActiveTintColor: '#007BFF', 
        tabBarInactiveTintColor: 'gray',
        tabBarStyle: {
            height: 60,
            paddingBottom: 10,
        }
      }}
    >
      {/* Telas Visíveis na Barra */}
      <Tabs.Screen
        name="agenda"
        options={{
          title: 'Agenda',
          tabBarIcon: ({ color }) => <FontAwesome5 name="calendar-alt" size={24} color={color} />,
        }}
      />
      <Tabs.Screen
        name="graficos"
        options={{
          title: 'Gráficos',
          tabBarIcon: ({ color }) => <FontAwesome5 name="chart-bar" size={24} color={color} />,
        }}
      />
      <Tabs.Screen
        name="metas"
        options={{
          title: 'Metas',
          tabBarIcon: ({ color }) => <FontAwesome5 name="trophy" size={24} color={color} />,
        }}
      />
      <Tabs.Screen
        name="perfil"
        options={{
          title: 'Perfil',
          tabBarIcon: ({ color }) => <FontAwesome5 name="user-alt" size={24} color={color} />,
        }}
      />

      {/* Telas Ocultas da Barra (href: null) */}
      <Tabs.Screen
        name="index"
        options={{
          href: null, 
        }}
      />
      <Tabs.Screen
        name="explore"
        options={{
          href: null,
        }}
      />
      <Tabs.Screen
        name="calendario"
        options={{
          href: null, // Isso esconde o botão, mas permite navegar até a tela via código
        }}
      />
    </Tabs>
  );
}