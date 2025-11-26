import React, { useState, useEffect } from "react";
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
} from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import * as ImagePicker from "expo-image-picker";
import Animated, { FadeInDown } from "react-native-reanimated";
import { Ionicons } from "@expo/vector-icons";
import * as Notifications from "expo-notifications";

// Contextos do Projeto
import { useTutorial } from '../contexts/TutorialContext';
import { TutorialOverlay } from '../contexts/TutorialOverlay';
import { useUser } from '../contexts/UserContext';
// import { useTheme } removido

export default function Perfil() {
  // --- HOOKS DOS CONTEXTOS ---
  const { passoAtual, proximoPasso } = useTutorial();
  const { logoutUser } = useUser(); 

  // --- ESTADOS LOCAIS ---
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [notificacoes, setNotificacoes] = useState(true);
  const [fotoPerfil, setFotoPerfil] = useState<string | null>(null);

  // Cores fixas (Modo Claro Padrão)
  const colors = {
    background: "#FFF",
    text: "#333",
    inputBackground: "#FFF",
    borderColor: "#CCC",
    blueButton: "#007BFF",
    cardBg: "#DDD",
  };

  // --------------------------
  // CARREGAR DADOS SALVOS
  // --------------------------
  useEffect(() => {
    const carregarDados = async () => {
      try {
        const nomeSalvo = await AsyncStorage.getItem("nome");
        const emailSalvo = await AsyncStorage.getItem("email");
        const notifSalvo = await AsyncStorage.getItem("notificacoes");
        const fotoSalva = await AsyncStorage.getItem("fotoPerfil");

        if (nomeSalvo) setNome(nomeSalvo);
        if (emailSalvo) setEmail(emailSalvo);
        if (notifSalvo) setNotificacoes(JSON.parse(notifSalvo));
        if (fotoSalva) setFotoPerfil(fotoSalva);
      } catch (e) {
        console.error("Erro ao carregar dados do perfil", e);
      }
    };
    carregarDados();

    // Permissão de notificação (Web)
    if (Platform.OS === "web") {
      if (Notification.permission === "default") {
        Notification.requestPermission();
      }
    }
  }, []);

  // --------------------------
  // SALVAR DADOS
  // --------------------------
  const salvarAlteracoes = async () => {
    try {
      await AsyncStorage.setItem("nome", nome);
      await AsyncStorage.setItem("email", email);
      await AsyncStorage.setItem("notificacoes", JSON.stringify(notificacoes));
      Alert.alert("Sucesso!", "As informações foram salvas.");
    } catch (e) {
      Alert.alert("Erro", "Não foi possível salvar.");
    }
  };

  // --------------------------
  // ALTERAR FOTO
  // --------------------------
  const alterarFoto = async () => {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 1,
    });

    if (!result.canceled) {
      setFotoPerfil(result.assets[0].uri);
      await AsyncStorage.setItem("fotoPerfil", result.assets[0].uri);
    }
  };

  // --------------------------
  // ATIVAR NOTIFICAÇÕES (AGENDAR ÀS 14:30)
  // --------------------------
  const agendarNotificacao = async () => {
    // Web: notificação imediata via Notification API
    if (Platform.OS === "web") {
      if (Notification.permission === "granted") {
        new Notification("Lembrete diário", { body: "⏰ Lembre-se de concluir suas metas hoje!" });
      } else {
        Alert.alert("Bloqueado", "Ative as notificações no navegador.");
      }
      return;
    }

    // Solicita permissão (iOS/Android)
    const { status } = await Notifications.requestPermissionsAsync();
    if (status !== "granted") {
      Alert.alert("Permissões necessárias", "Ative as notificações nas configurações do celular.");
      return;
    }

    // Agendar notificação diária às 14:30
    try {
      await Notifications.scheduleNotificationAsync({
        content: {
          title: "Lembrete Diário",
          body: "⏰ Não se esqueça de verificar suas metas!",
        },
        trigger: {
          hour: 14,
          minute: 30,
          repeats: true,
        } as Notifications.CalendarTriggerInput,
      });

      Alert.alert("Sucesso", "Você receberá um lembrete diário às 14h30!");
    } catch (e) {
      console.error("Erro ao agendar notificação", e);
      Alert.alert("Erro", "Não foi possível agendar a notificação.");
    }
  };

  // --------------------------
  // LOGOUT
  // --------------------------
  const sair = () => {
    Alert.alert("Sair", "Deseja realmente desconectar?", [
      { text: "Cancelar", style: "cancel" },
      { 
        text: "Sair", 
        onPress: () => {
          logoutUser(); 
        }
      }
    ]);
  };

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
          <Text style={styles.saveButtonText}>Salvar Alterações</Text>
        </TouchableOpacity>
      </Animated.View>

      {/* CONFIGURAÇÕES */}
      <Animated.View entering={FadeInDown.delay(600)} style={styles.section}>
        <Text style={[styles.sectionTitle, { color: colors.text }]}>Configurações</Text>

        {/* Switch Notificações */}
        <View style={[styles.row, { borderColor: colors.borderColor }]}>
          <Text style={[styles.rowText, { color: colors.text }]}>Notificações Push</Text>
          <Switch
            value={notificacoes}
            onValueChange={setNotificacoes}
            trackColor={{ false: "#888", true: colors.blueButton }}
          />
        </View>

        {/* Botão para Testar/Ativar Notificações */}
        <TouchableOpacity
          style={[styles.saveButton, { backgroundColor: "#28a745", marginTop: 10, marginBottom: 15 }]}
          onPress={agendarNotificacao}
        >
          <Text style={styles.saveButtonText}>Ativar Lembrete às 14:30</Text>
        </TouchableOpacity>
      </Animated.View>

      {/* LOGOUT */}
      <Animated.View entering={FadeInDown.delay(800)} style={styles.section}>
        <TouchableOpacity style={[styles.row, { borderColor: 'transparent' }]} onPress={sair}>
          <Text style={{ color: "#dc3545", fontWeight: "600", fontSize: 16 }}>Sair / Logout</Text>
        </TouchableOpacity>
      </Animated.View>

      {/* TUTORIAL OVERLAY */}
      <TutorialOverlay 
        visible={passoAtual === 3}
        titulo="4. Seu Perfil"
        texto="Configure sua conta e ative as notificações para não perder nada. Tudo pronto para começar!"
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
