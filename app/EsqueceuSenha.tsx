import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert } from "react-native";
import { useRouter } from "expo-router";

export default function EsqueceuSenha() {
  const [email, setEmail] = useState<string>("");
  const router = useRouter();

  const handleSend = () => {
    if (!email.includes("@")) {
      Alert.alert("Erro", "Digite um e-mail válido.");
      return;
    }

    Alert.alert("Sucesso", "Se esse e-mail estiver cadastrado, enviaremos uma nova senha.");
    router.back();
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Recuperar Senha</Text>
      <Text style={styles.subtitle}>Digite seu e-mail para receber uma nova senha</Text>

      <TextInput
        style={styles.input}
        placeholder="Seu e-mail"
        keyboardType="email-address"
        value={email}
        onChangeText={setEmail}
      />

      <TouchableOpacity style={styles.button} onPress={handleSend}>
        <Text style={styles.buttonText}>Enviar</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => router.back()}>
        <Text style={styles.backLink}>Voltar ao Login</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#f4f4f4ff",
    alignItems: "center",
    justifyContent: "center",
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 10,
    color: "#007BFF",
  },
  subtitle: {
    fontSize: 14,
    textAlign: "center",
    marginBottom: 20,
    color: "#333",
  },
  input: {
    width: "100%",
    backgroundColor: "white",
    borderRadius: 10,
    paddingHorizontal: 15,
    paddingVertical: 10,
    marginBottom: 15,
    fontSize: 16,
    borderWidth: 1,
    borderColor: "#ddd",
  },
  button: {
    backgroundColor: "#3399FF",
    borderRadius: 10,
    padding: 15,
    alignItems: "center",
    width: "100%",
    marginBottom: 15,
  },
  buttonText: {
    color: "white",
    fontSize: 16,
    fontWeight: "bold",
  },
  backLink: {
    color: "#007BFF",
    fontSize: 14,
  },
});
