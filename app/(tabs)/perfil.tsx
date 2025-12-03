import React, { useState, useCallback } from "react";
import {
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
  Switch,
  Image,
  Platform,
  ActivityIndicator,
} from "react-native";
import * as ImagePicker from "expo-image-picker";
import Animated, { FadeInDown } from "react-native-reanimated";
import { Ionicons } from "@expo/vector-icons";
import * as Notifications from "expo-notifications";
import axios from "axios";
import { useFocusEffect, useRouter } from "expo-router";

// Contextos do Projeto
import { useTutorial } from '../contexts/TutorialContext';
import { TutorialOverlay } from '../contexts/TutorialOverlay';
import { useUser } from '../contexts/UserContext';

const API_URL = "http://192.168.3.8:3001"; // Lembre-se de trocar pelo seu IP se for rodar no celular

export default function Perfil() {
  const router = useRouter();
  const { passoAtual, proximoPasso } = useTutorial();
  const { userId, logoutUser } = useUser(); 

  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [notificacoes, setNotificacoes] = useState(true);
  const [fotoPerfil, setFotoPerfil] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const colors = {
    background: "#FFF",
    text: "#333",
    inputBackground: "#FFF",
    borderColor: "#CCC",
    blueButton: "#007BFF",
    cardBg: "#DDD",
  };

  // 1. Carregar dados
  const carregarDados = async () => {
    if (!userId) return;
    setLoading(true);
    try {
      const response = await axios.get(`${API_URL}/usuario/${userId}`);
      const dados = response.data;
      
      setNome(dados.nome || "");
      setEmail(dados.email || "");
      setNotificacoes(dados.notificacoes);
      if (dados.foto) {
        setFotoPerfil(dados.foto);
      }
    } catch (e) {
      console.error("Erro ao carregar perfil:", e);
    } finally {
      setLoading(false);
    }
  };

  useFocusEffect(
    useCallback(() => {
      carregarDados();
    }, [userId])
  );

  // 2. Salvar dados
  const salvarAlteracoes = async () => {
    if (!userId) return;
    try {
      await axios.put(`${API_URL}/usuario/${userId}`, {
        nome,
        email,
        foto: fotoPerfil,
        notificacoes
      });
      
      if (Platform.OS === 'web') {
        window.alert("Sucesso! Perfil atualizado.");
      } else {
        Alert.alert("Sucesso!", "Perfil atualizado no banco de dados.");
      }
    } catch (e) {
      console.error(e);
      if (Platform.OS === 'web') {
        window.alert("Erro ao salvar alterações.");
      } else {
        Alert.alert("Erro", "Não foi possível salvar as alterações.");
      }
    }
  };

  // 3. Alterar Foto
  const alterarFoto = async () => {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [1, 1],
      quality: 0.5,
      base64: true,
    });

    if (!result.canceled && result.assets[0].base64) {
      const base64Img = `data:image/jpeg;base64,${result.assets[0].base64}`;
      setFotoPerfil(base64Img);
    }
  };

  // 4. Notificações (Modificado)
  const agendarNotificacao = async () => {
    if (Platform.OS === "web") {
      if (Notification.permission === "granted") {
        // Exibe notificação simples na web
        new Notification("Notificações Ativadas", { body: "Você receberá notificações todo dia às 20 horas." });
      } else if (Notification.permission !== "denied") {
        Notification.requestPermission().then(perm => {
            if (perm === "granted") {
                new Notification("Notificações Ativadas", { body: "Você receberá notificações todo dia às 20 horas." });
            }
        });
      }
      return;
    }

    // Lógica para Celular
    const { status } = await Notifications.requestPermissionsAsync();
    if (status !== "granted") {
      Alert.alert("Permissões necessárias", "Ative as notificações nas configurações.");
      setNotificacoes(false); // Desliga o switch se não der permissão
      return;
    }

    try {
      await Notifications.cancelAllScheduledNotificationsAsync();
      
      await Notifications.scheduleNotificationAsync({
        content: {
          title: "Lembrete Diário",
          body: "⏰ Não se esqueça de verificar suas metas!",
        },
        trigger: {
          hour: 20, // Definido para 20 horas
          minute: 0,
          repeats: true,
        } as any,
      });
      
      // Mensagem na tela solicitada
      Alert.alert("Notificações Ativadas", "Você receberá notificações todo dia às 20 horas.");
      
    } catch (e) {
      Alert.alert("Erro", "Não foi possível agendar.");
    }
  };

  // 5. Logout
  const sair = () => {
    if (Platform.OS === 'web') {
        const confirmar = window.confirm("Deseja realmente desconectar?");
        if (confirmar) {
            logoutUser(); 
            router.replace('/'); 
        }
        return;
    }

    Alert.alert("Sair", "Deseja realmente desconectar?", [
      { text: "Cancelar", style: "cancel" },
      { 
        text: "Sair", 
        onPress: () => {
          logoutUser(); 
          router.replace('/'); 
        }
      }
    ]);
  };

  if (loading) {
      return (
          <View style={{flex:1, justifyContent:'center', alignItems:'center'}}>
              <ActivityIndicator size="large" color="#007BFF" />
          </View>
      );
  }

  return (
    <ScrollView style={[styles.container, { backgroundColor: colors.background }]}>
      
      {/* FOTO DE PERFIL */}
      <Animated.View entering={FadeInDown.delay(200)} style={styles.topSection}>
        <TouchableOpacity onPress={alterarFoto}>
          {fotoPerfil ? (
            <Image source={{ uri: fotoPerfil }} style={styles.foto} />
          ) : (
            <View style={[styles.iconWrapper, { backgroundColor: colors.cardBg }]}>
              <Ionicons name="person" size={50} color={colors.text} />
            </View>
          )}
        </TouchableOpacity>

        <TouchableOpacity onPress={alterarFoto} style={{ marginTop: 8 }}>
          <Text style={{ color: colors.blueButton, fontWeight: "600", fontSize: 15 }}>Alterar Foto</Text>
        </TouchableOpacity>
      </Animated.View>

      {/* FORMULÁRIO */}
      <Animated.View entering={FadeInDown.delay(400)}>
        <View style={styles.inputWrapper}>
          <Text style={[styles.label, { color: colors.text }]}>Nome</Text>
          <TextInput
            style={[styles.input, { backgroundColor: colors.inputBackground, color: colors.text, borderColor: colors.borderColor }]}
            placeholder="Digite seu nome"
            placeholderTextColor="#888"
            value={nome}
            onChangeText={setNome}
          />
        </View>

        <View style={styles.inputWrapper}>
          <Text style={[styles.label, { color: colors.text }]}>E-mail</Text>
          <TextInput
            style={[styles.input, { backgroundColor: colors.inputBackground, color: colors.text, borderColor: colors.borderColor }]}
            placeholder="Digite seu e-mail"
            placeholderTextColor="#888"
            value={email}
            onChangeText={setEmail}
            keyboardType="email-address"
          />
        </View>

        <TouchableOpacity style={[styles.saveButton, { backgroundColor: colors.blueButton }]} onPress={salvarAlteracoes}>
          <Text style={styles.saveButtonText}>Salvar no Banco de Dados</Text>
        </TouchableOpacity>
      </Animated.View>

      {/* CONFIGURAÇÕES */}
      <Animated.View entering={FadeInDown.delay(600)} style={styles.section}>
        <Text style={[styles.sectionTitle, { color: colors.text }]}>Configurações</Text>

        <View style={[styles.row, { borderColor: colors.borderColor }]}>
          <Text style={[styles.rowText, { color: colors.text }]}>Receber Notificações</Text>
          <Switch
            value={notificacoes}
            onValueChange={(val) => {
                setNotificacoes(val);
                if(!val) {
                    Notifications.cancelAllScheduledNotificationsAsync();
                } else {
                    agendarNotificacao();
                }
            }}
            trackColor={{ false: "#888", true: colors.blueButton }}
          />
        </View>
        
        {/* BOTÃO DE TESTAR REMOVIDO DAQUI */}

      </Animated.View>

      {/* LOGOUT */}
      <Animated.View entering={FadeInDown.delay(800)} style={styles.section}>
        <TouchableOpacity style={[styles.row, { borderColor: 'transparent', paddingBottom: 40 }]} onPress={sair}>
          <Text style={{ color: "#dc3545", fontWeight: "600", fontSize: 16 }}>Sair / Logout</Text>
        </TouchableOpacity>
      </Animated.View>

      <TutorialOverlay 
        visible={passoAtual === 3}
        titulo="4. Seu Perfil"
        texto="Configure sua conta. Seus dados agora ficam salvos na nuvem! Tudo pronto para começar!"
        botaoTexto="Concluir e Começar! 🚀"
        onProximo={proximoPasso}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20 },
  topSection: { alignItems: "center", marginBottom: 20 },
  foto: { width: 110, height: 110, borderRadius: 55, backgroundColor: "#DDD" },
  iconWrapper: { width: 110, height: 110, borderRadius: 55, justifyContent: "center", alignItems: "center" },
  inputWrapper: { marginVertical: 10 },
  label: { fontSize: 14, marginBottom: 5 },
  input: { borderWidth: 1, borderRadius: 10, padding: 12, fontSize: 16 },
  saveButton: { marginTop: 15, padding: 14, borderRadius: 10, alignItems: "center" },
  saveButtonText: { color: "#FFF", fontSize: 16, fontWeight: "700" },
  section: { marginTop: 30 },
  sectionTitle: { fontSize: 17, fontWeight: "700", marginBottom: 12 },
  row: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", paddingVertical: 14, borderBottomWidth: 1 },
  rowText: { fontSize: 16 },
});