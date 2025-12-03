import axios from 'axios';
import { useRouter } from 'expo-router';
import React, { useState } from 'react';
import { Alert, StatusBar, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { useUser } from './contexts/UserContext';

const API_URL = 'http://192.168.3.8:3001';

export default function TelaLogin() {
  const router = useRouter();
  const { loginUser } = useUser();

  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');

  const handleLogin = async () => {
    if (!email || !senha) {
      Alert.alert('Erro', 'Por favor, preencha todos os campos.');
      return;
    }

    try {
      const response = await axios.post(`${API_URL}/login`, { email, senha });

      if (response.status === 200) {
        const user = response.data.user;
        loginUser(user.id, user.nome, user.email);
        Alert.alert('Sucesso', 'Login bem-sucedido!');
        router.replace('/questionario'); // redireciona após login
      }
    } catch (error: any) {
      console.error('Erro no login:', error);
      if (error.response && error.response.status === 401) {
        Alert.alert('Erro', 'Email ou senha incorretos.');
      } else {
        Alert.alert('Erro', 'Não foi possível conectar. Verifique a conexão.');
      }
    }
  };

  const handleEsqueceuSenha = () => {
    router.push('./TelaRecuperarSenha');
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      <View style={styles.topBlueArea}>
        <Text style={styles.titulo}>Task Flow</Text>
        <Text style={styles.subtitulo}>Vença a procrastinação!</Text>
      </View>

      <View style={styles.bottomCard}>
        <Text style={styles.bemVindo}>Bem-Vindo!</Text>

        <Text style={styles.label}>Email</Text>
        <TextInput
          style={styles.input}
          placeholder="Digite seu email"
          keyboardType="email-address"
          autoCapitalize="none"
          value={email}
          onChangeText={setEmail}
        />

        <Text style={styles.label}>Senha</Text>
        <TextInput
          style={styles.input}
          placeholder="Digite sua senha"
          secureTextEntry
          value={senha}
          onChangeText={setSenha}
        />

        <TouchableOpacity onPress={handleEsqueceuSenha}>
          <Text style={styles.esqueceuSenha}>Esqueceu a senha?</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.botaoEntrar} onPress={handleLogin}>
          <Text style={styles.textoBotao}>Entrar</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => router.push('./TelaCadastro')}>
          <View style={styles.registroArea}>
            <Text style={styles.textoRegistro}>Não tem conta?</Text>
            <Text style={styles.linkRegistro}>Cadastre-se</Text>
          </View>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#007BFF' },
  topBlueArea: { alignItems: 'center', marginTop: 60, marginBottom: 20 },
  titulo: { fontSize: 28, fontWeight: 'bold', color: 'white' },
  subtitulo: { fontSize: 16, color: 'white' },
  bottomCard: { flex: 1, backgroundColor: '#f4f4f4ff', borderTopLeftRadius: 30, borderTopRightRadius: 30, padding: 20 },
  bemVindo: { fontSize: 22, fontWeight: 'bold', textAlign: 'center', marginBottom: 20 },
  label: { fontSize: 14, color: '#333', marginBottom: 5 },
  input: { backgroundColor: 'white', borderRadius: 10, paddingHorizontal: 15, paddingVertical: 10, marginBottom: 10, fontSize: 16, borderWidth: 1, borderColor: '#ddd' },
  botaoEntrar: { backgroundColor: '#3399FF', borderRadius: 10, padding: 15, alignItems: 'center', marginVertical: 10 },
  textoBotao: { color: 'white', fontSize: 16, fontWeight: 'bold' },
  registroArea: { flexDirection: 'row', justifyContent: 'center', marginTop: 20 },
  textoRegistro: { marginRight: 5, fontSize: 14 },
  linkRegistro: { color: '#007BFF', fontWeight: 'bold', fontSize: 14 },
  esqueceuSenha: { color: '#007BFF', textAlign: 'right', marginBottom: 15, fontSize: 14 },
});
