import axios from 'axios';
import { useRouter } from 'expo-router';
import React, { useState } from 'react';
import { Alert, StatusBar, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';

const API_URL = 'http://localhost:3001';

export default function TelaCadastro() {
  const router = useRouter();

  // Estados para armazenar nome, email e senha digitados
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');

  const handleCadastro = async () => {
    // Validação básica para todos os campos
    if (!nome || !email || !senha) {
      Alert.alert('Erro', 'Por favor, preencha todos os campos.');
      return;
    }

    try {
      // Faz uma requisição POST para a rota de cadastro do seu backend
      const response = await axios.post(`${API_URL}/cadastrar`, {
        nome: nome,
        email: email,
        senha: senha,
      });

      // Se a resposta for bem-sucedida (status 201 Created)
      if (response.status === 201) {
        Alert.alert('Sucesso', 'Cadastro realizado com sucesso! Você já pode entrar.');
        console.log('Usuário cadastrado:', response.data.user);
        // Navega para a tela do questionário após o cadastro
        router.push('/questionario');
      }
    } catch (error) {
      console.error('Erro no cadastro:', error);
      Alert.alert('Erro', 'Não foi possível cadastrar. O e-mail já pode estar em uso.');
    }
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      <View style={styles.statusBar}>
        <Text style={styles.hora}></Text>
        <View style={styles.statusIcons}>
          <Text style={styles.iconeStatus}></Text>
        </View>
      </View>
      <View style={styles.topBlueArea}>
        <Text style={styles.titulo}>Task Flow</Text>
        <Text style={styles.subtitulo}>Vença a procrastinação!</Text>
      </View>
      <View style={styles.bottomCard}>
        <Text style={styles.bemVindo}>Crie sua conta</Text>
        
        {/* Campo de entrada para o nome */}
        <Text style={styles.label}>Nome</Text>
        <TextInput
          style={styles.input}
          placeholder="Digite seu nome"
          value={nome}
          onChangeText={setNome}
        />
        
        {/* Campo de entrada para o email */}
        <Text style={styles.label}>Email</Text>
        <TextInput
          style={styles.input}
          placeholder="Digite seu email"
          keyboardType="email-address"
          value={email}
          onChangeText={setEmail}
        />
        
        {/* Campo de entrada para a senha */}
        <Text style={styles.label}>Senha</Text>
        <TextInput
          style={styles.input}
          placeholder="Digite sua senha"
          secureTextEntry
          value={senha}
          onChangeText={setSenha}
        />

        {/* Botão para cadastro */}
        <TouchableOpacity style={styles.botaoEntrar} onPress={handleCadastro}>
          <Text style={styles.textoBotao}>Cadastrar e Entrar</Text>
        </TouchableOpacity>
        
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#007BFF',
  },
  statusBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 10,
    paddingTop: 40,
  },
  hora: {
    color: 'white',
    fontSize: 16,
  },
  statusIcons: {
    flexDirection: 'row',
  },
  iconeStatus: {
    color: 'white',
    fontSize: 16,
  },
  topBlueArea: {
    alignItems: 'center',
    marginTop: 20,
    marginBottom: 20,
  },
  titulo: {
    fontSize: 28,
    fontWeight: 'bold',
    color: 'white',
  },
  subtitulo: {
    fontSize: 16,
    color: 'white',
  },
  bottomCard: {
    flex: 1,
    backgroundColor: '#f4f4f4ff',
    borderTopLeftRadius: 30,
    borderTopRightRadius: 30,
    padding: 20,
  },
  bemVindo: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 20,
  },
  label: {
    fontSize: 14,
    color: '#333',
    marginBottom: 5,
  },
  input: {
    backgroundColor: 'white',
    borderRadius: 10,
    paddingHorizontal: 15,
    paddingVertical: 10,
    marginBottom: 15,
    fontSize: 16,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  botaoEntrar: {
    backgroundColor: '#3399FF',
    borderRadius: 10,
    padding: 15,
    alignItems: 'center',
    marginVertical: 10,
  },
  textoBotao: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
  link: {
    color: '#3399FF',
    textAlign: 'center',
    marginTop: 10,
  },
  registroArea: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: 20,
  },
  textoRegistro: {
    marginRight: 5,
    fontSize: 14,
  },
  linkRegistro: {
    color: '#007BFF',
    fontWeight: 'bold',
    fontSize: 14,
  },
});
