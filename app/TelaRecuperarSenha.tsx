import React, { useState } from 'react';
import { Alert, StatusBar, StyleSheet, Text, TextInput, TouchableOpacity, View, ActivityIndicator } from 'react-native';
import { useRouter } from 'expo-router';

export default function TelaRecuperarSenha() {
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);

  const validarEmail = (email: string) => /\S+@\S+\.\S+/.test(email);

  const handleRecuperarSenha = async () => {
    if (!email) return Alert.alert('Erro', 'Digite seu email.');
    if (!validarEmail(email)) return Alert.alert('Erro', 'Digite um email válido.');

    setLoading(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1500)); // simula envio
      setLoading(false);
      router.push('./TelaLinkEnviado');
    } catch {
      setLoading(false);
      Alert.alert('Erro', 'Não foi possível enviar o email. Tente novamente.');
    }
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      <View style={styles.card}>
        <Text style={styles.titulo}>Recuperar Senha</Text>
        <Text style={styles.subtitulo}>Informe seu email para receber o link de redefinição.</Text>

        <TextInput
          style={styles.input}
          placeholder="Digite seu email"
          keyboardType="email-address"
          autoCapitalize="none"
          value={email}
          onChangeText={setEmail}
        />

        <TouchableOpacity style={styles.botao} onPress={handleRecuperarSenha} disabled={loading}>
          {loading ? <ActivityIndicator color="white" /> : <Text style={styles.textoBotao}>Enviar Link</Text>}
        </TouchableOpacity>

        <TouchableOpacity onPress={() => router.back()}>
          <Text style={styles.voltar}>Voltar para login</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#007BFF', justifyContent: 'center', alignItems: 'center', padding: 20 },
  card: { width: '100%', backgroundColor: 'white', borderRadius: 20, padding: 25, shadowColor: '#000', shadowOpacity: 0.1, shadowRadius: 10, elevation: 5 },
  titulo: { fontSize: 24, fontWeight: 'bold', color: '#007BFF', textAlign: 'center', marginBottom: 10 },
  subtitulo: { fontSize: 14, color: '#555', textAlign: 'center', marginBottom: 25 },
  input: { backgroundColor: '#f2f2f2', borderRadius: 10, paddingHorizontal: 15, paddingVertical: 12, marginBottom: 20, fontSize: 16 },
  botao: { backgroundColor: '#007BFF', borderRadius: 10, padding: 15, alignItems: 'center', marginBottom: 15 },
  textoBotao: { color: 'white', fontSize: 16, fontWeight: 'bold' },
  voltar: { color: '#007BFF', textAlign: 'center', fontWeight: 'bold', fontSize: 14 },
});
