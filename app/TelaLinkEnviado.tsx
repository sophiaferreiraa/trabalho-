import React from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { useRouter } from 'expo-router';

export default function TelaLinkEnviado() {
  const router = useRouter();

  return (
    <View style={styles.container}>
      <View style={styles.card}>
        <Text style={styles.emoji}>✅</Text>
        <Text style={styles.titulo}>Email enviado!</Text>
        <Text style={styles.mensagem}>
          Verifique seu email. Um link para redefinir sua senha foi enviado.
        </Text>

        <TouchableOpacity style={styles.botao} onPress={() => router.replace('./TelaLogin')}>
          <Text style={styles.textoBotao}>Voltar para login</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#007BFF', justifyContent: 'center', alignItems: 'center', padding: 20 },
  card: { width: '100%', backgroundColor: 'white', borderRadius: 20, padding: 25, alignItems: 'center', shadowColor: '#000', shadowOpacity: 0.1, shadowRadius: 10, elevation: 5 },
  emoji: { fontSize: 50, marginBottom: 15 },
  titulo: { fontSize: 24, fontWeight: 'bold', color: '#007BFF', marginBottom: 10, textAlign: 'center' },
  mensagem: { fontSize: 14, color: '#555', textAlign: 'center', marginBottom: 25 },
  botao: { backgroundColor: '#007BFF', borderRadius: 10, padding: 15, width: '100%', alignItems: 'center' },
  textoBotao: { color: 'white', fontWeight: 'bold', fontSize: 16 },
});
