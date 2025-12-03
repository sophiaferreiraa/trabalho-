import { FontAwesome5 } from '@expo/vector-icons';
import { Tabs } from 'expo-router';
import React from 'react';
import { Platform } from 'react-native'; // Importação necessária para ajustes específicos por sistema

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: '#007BFF',
        tabBarInactiveTintColor: 'gray',
        tabBarStyle: {
            // AUMENTAMOS A ALTURA E O PADDING PARA AFASTAR DA BORDA
            height: Platform.OS === 'ios' ? 95 : 75, // Altura maior para acomodar o espaçamento
            paddingBottom: Platform.OS === 'ios' ? 30 : 15, // Espaço extra embaixo para não colar na barra do celular
            paddingTop: 10, // Espaço em cima dos ícones para centralizar
            borderTopWidth: 1,
            borderTopColor: '#e0e0e0',
            elevation: 5, // Sombra no Android para destacar a barra
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
          href: null,
        }}
      />
    </Tabs>
  );
}