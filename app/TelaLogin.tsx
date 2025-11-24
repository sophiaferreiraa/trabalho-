import axios from 'axios';
import { useRouter } from 'expo-router';
import React, { useState } from 'react';
import { Alert, StatusBar, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { useUser } from './contexts/UserContext'; 

const API_URL = 'http://localhost:3001'; 

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
      const response = await axios.post(`${API_URL}/login`, {
        email: email,
        senha: senha,
      });

      if (response.status === 200) {
        const user = response.data.user;
        
        // Salva o usuário no contexto global
        loginUser(user.id, user.nome, user.email);

        Alert.alert('Sucesso', 'Login bem-sucedido!');
        router.replace('/questionario');
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

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      <View style={styles.statusBar}>
        <Text style={styles.hora}></Text>
      </View>
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
  statusBar: { flexDirection: 'row', justifyContent: 'space-between', padding: 10, paddingTop: 40 },
  hora: { color: 'white', fontSize: 16 },
  topBlueArea: { alignItems: 'center', marginTop: 20, marginBottom: 20 },
  titulo: { fontSize: 28, fontWeight: 'bold', color: 'white' },
  subtitulo: { fontSize: 16, color: 'white' },
  bottomCard: { flex: 1, backgroundColor: '#f4f4f4ff', borderTopLeftRadius: 30, borderTopRightRadius: 30, padding: 20 },
  bemVindo: { fontSize: 22, fontWeight: 'bold', textAlign: 'center', marginBottom: 20 },
  label: { fontSize: 14, color: '#333', marginBottom: 5 },
  input: { backgroundColor: 'white', borderRadius: 10, paddingHorizontal: 15, paddingVertical: 10, marginBottom: 15, fontSize: 16, borderWidth: 1, borderColor: '#ddd' },
  botaoEntrar: { backgroundColor: '#3399FF', borderRadius: 10, padding: 15, alignItems: 'center', marginVertical: 10 },
  textoBotao: { color: 'white', fontSize: 16, fontWeight: 'bold' },
  link: { color: '#3399FF', textAlign: 'center', marginTop: 10 },
  registroArea: { flexDirection: 'row', justifyContent: 'center', marginTop: 20 },
  textoRegistro: { marginRight: 5, fontSize: 14 },
  linkRegistro: { color: '#007BFF', fontWeight: 'bold', fontSize: 14 },
});