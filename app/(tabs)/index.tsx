import { Redirect } from 'expo-router';

export default function TabIndex() {
  // Redireciona automaticamente para a Agenda ao tentar acessar a raiz das abas
  // Isso evita que a TelaLogin apareça com a barra de navegação embaixo
  return <Redirect href="/TelaLogin" />;
}