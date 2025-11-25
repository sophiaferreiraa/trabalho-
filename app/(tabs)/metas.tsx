import React, { useEffect, useState } from "react";
import {
  Alert,
  Button,
  Modal,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
  Platform,
} from "react-native";
import axios from "axios";
import * as Notifications from "expo-notifications";
import { useUser } from "../contexts/UserContext";
import { useTutorial } from "../contexts/TutorialContext";
import { TutorialOverlay } from "../contexts/TutorialOverlay";

// Mantendo o padrão do projeto (localhost), altere se necessário para o IP específico
const API_URL = "http://localhost:3001";

type Marco = {
  id: number;
  descricao: string;
  progresso: number;
  vencimento: string;
};

type Meta = {
  id: number;
  titulo: string;
  descricao: string;
  progresso: number;
  vencimento: string;
  marcos: Marco[];
};

export default function MetasScreen() {
  const { userId } = useUser();
  const { passoAtual, proximoPasso } = useTutorial(); // Funcionalidade antiga (Tutorial)

  const [metas, setMetas] = useState<Meta[]>([]);
  const [modalVisivel, setModalVisivel] = useState(false);
  const [metaEmEdicao, setMetaEmEdicao] = useState<Meta | null>(null);

  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [vencimento, setVencimento] = useState("");
  // Funcionalidade nova (Marcos)
  const [marcos, setMarcos] = useState<Marco[]>([]);

  // Carregar metas
  const carregarMetas = async () => {
    if (!userId) return;
    try {
      const response = await axios.get(`${API_URL}/metas/${userId}`);
      setMetas(response.data);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    carregarMetas();
  }, [userId]);

  // Formatar data DD/MM/AAAA
  const changeData = (t: string) => {
    let n = t.replace(/\D/g, "");
    if (n.length > 8) n = n.substring(0, 8);
    if (n.length > 4) n = `${n.substring(0, 2)}/${n.substring(2, 4)}/${n.substring(4)}`;
    else if (n.length > 2) n = `${n.substring(0, 2)}/${n.substring(2)}`;
    setVencimento(n);
  };

  // Abrir modal nova meta
  const abrirNovo = () => {
    setMetaEmEdicao(null);
    setTitulo("");
    setDescricao("");
    setVencimento("");
    setMarcos([]);
    setModalVisivel(true);
  };

  // Abrir modal edição
  const abrirEdicao = (m: Meta) => {
    setMetaEmEdicao(m);
    setTitulo(m.titulo);
    setDescricao(m.descricao);
    setVencimento(m.vencimento);
    setMarcos(m.marcos || []);
    setModalVisivel(true);
  };

  // Funcionalidade nova: Calcular progresso baseado nos marcos
  const calcularProgressoMeta = () => {
    if (marcos.length === 0) return 0;
    const soma = marcos.reduce((acc, m) => acc + m.progresso, 0);
    return Math.round(soma / marcos.length);
  };

  // Salvar meta (Unificado)
  const salvarMeta = async () => {
    if (!descricao || !vencimento) {
      return Alert.alert("Erro", "Preencha descrição e vencimento.");
    }
    if (vencimento.length < 10) {
      return Alert.alert("Erro", "Data incompleta (DD/MM/AAAA).");
    }

    // O progresso agora é calculado, não digitado manualmente
    const progresso = calcularProgressoMeta();

    try {
      if (metaEmEdicao) {
        await axios.put(`${API_URL}/metas/${metaEmEdicao.id}`, {
          titulo: titulo || `Meta ${metaEmEdicao.id}`,
          descricao,
          vencimento,
          progresso,
          marcos,
        });
      } else {
        await axios.post(`${API_URL}/metas`, {
          usuario_id: userId,
          titulo: titulo || `Meta ${metas.length + 1}`,
          descricao,
          vencimento,
          progresso,
          marcos,
        });
      }
      await carregarMetas();
      setModalVisivel(false);
    } catch (e) {
      Alert.alert("Erro", "Falha ao salvar.");
      console.error(e);
    }
  };

  // Excluir meta
  const excluirMeta = async () => {
    if (!metaEmEdicao) return;
    Alert.alert("Descartar", "Apagar meta?", [
      { text: "Não" },
      {
        text: "Sim",
        onPress: async () => {
          try {
            await axios.delete(`${API_URL}/metas/${metaEmEdicao.id}`);
            await carregarMetas();
            setModalVisivel(false);
          } catch (e) {
            Alert.alert("Erro", "Falha ao excluir.");
          }
        },
      },
    ]);
  };

  // --- Funcionalidades Novas de Marcos ---

  const adicionarMarco = () => {
    const novo: Marco = {
      id: Date.now(),
      descricao: "",
      progresso: 0,
      vencimento: "",
    };
    setMarcos([...marcos, novo]);
  };

  const excluirMarco = (idx: number) => {
    const copy = [...marcos];
    copy.splice(idx, 1);
    setMarcos(copy);
  };

  const notificarMarco = async (marco: Marco) => {
    if (!marco.vencimento) return;
    const [dia, mes, ano] = marco.vencimento.split("/");
    // Define notificação para as 20:00 da data
    const data = new Date(Number(ano), Number(mes) - 1, Number(dia), 20, 0);

    if (Platform.OS !== "web") {
      await Notifications.scheduleNotificationAsync({
        content: {
          title: "Lembrete de Marco",
          body: `Não esqueça do marco: ${marco.descricao}`,
        },
        trigger: { seconds: Math.max((data.getTime() - Date.now()) / 1000, 1), repeats: false } as any,
      });
    } else {
      if (Notification.permission === "granted") {
        new Notification(`Marco: ${marco.descricao}`);
      } else if (Notification.permission !== "denied") {
        Notification.requestPermission().then((perm) => {
          if (perm === "granted") new Notification(`Marco: ${marco.descricao}`);
        });
      }
    }
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      {/* Header Estilizado (Código Antigo) */}
      <View style={styles.header}>
        <View>
          <Text style={styles.headerTitle}>Metas</Text>
          <Text style={{ color: "rgba(255,255,255,0.85)", fontSize: 14 }}>
            Defina seus objetivos de longo prazo.
          </Text>
        </View>
        <TouchableOpacity style={styles.addButton} onPress={abrirNovo}>
          <Text style={{ color: "white", fontSize: 24 }}>+</Text>
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.grid}>
        {metas.length > 0 ? (
          metas.map((m) => (
            <TouchableOpacity
              key={m.id}
              style={styles.card}
              onPress={() => abrirEdicao(m)}
            >
              <Text style={styles.cardTitle}>{m.titulo}</Text>
              <Text style={{ color: "gray" }}>Descrição: {m.descricao}</Text>
              <View style={{ marginTop: 10 }}>
                {m.progresso === 100 ? (
                  <Text style={{ fontStyle: "italic", color: "gray" }}>
                    Concluído!
                  </Text>
                ) : (
                  <>
                    <View style={styles.progressBg}>
                      <View
                        style={[styles.progressFill, { width: `${m.progresso}%` }]}
                      />
                    </View>
                    <Text style={{ fontSize: 12, color: "gray", marginTop: 5 }}>
                      Vence em: {m.vencimento}
                    </Text>
                  </>
                )}
              </View>
            </TouchableOpacity>
          ))
        ) : (
          <View style={{ marginTop: 50, alignItems: "center" }}>
            <Text>Nenhuma meta.</Text>
          </View>
        )}
      </ScrollView>

      {/* Modal Unificado */}
      <Modal visible={modalVisivel} transparent animationType="slide">
        <View style={styles.modalBg}>
          <View style={styles.modalBox}>
            <Text style={styles.modalTitle}>
              {metaEmEdicao ? "Editar" : "Nova"} Meta
            </Text>

            <TextInput
              style={styles.input}
              placeholder="Título"
              value={titulo}
              onChangeText={setTitulo}
            />
            <TextInput
              style={styles.input}
              placeholder="Descrição"
              value={descricao}
              onChangeText={setDescricao}
            />
            <TextInput
              style={styles.input}
              placeholder="Vencimento (DD/MM/AAAA)"
              value={vencimento}
              onChangeText={changeData}
              keyboardType="numeric"
              maxLength={10}
            />
            {/* Campo de progresso manual removido, pois agora é calculado pelos marcos */}
            
            <View style={{ marginTop: 10, maxHeight: 200 }}>
               <Text style={{ fontWeight: "bold", marginBottom: 5 }}>Marcos / Sub-metas</Text>
               <ScrollView nestedScrollEnabled style={{ marginBottom: 10 }}>
                {marcos.map((marco, idx) => (
                  <View
                    key={marco.id}
                    style={{
                      marginBottom: 10,
                      borderWidth: 1,
                      borderRadius: 5,
                      padding: 5,
                      borderColor: marco.progresso < 100 ? "#dc3545" : "#CCC",
                      backgroundColor: marco.progresso < 100 ? "#FFF" : "#F0FFF0",
                    }}
                  >
                    <View style={{ flexDirection: "row", alignItems: "center", justifyContent: "space-between" }}>
                      <TextInput
                        placeholder="Descrição do marco"
                        value={marco.descricao}
                        onChangeText={(text) => {
                          const copy = [...marcos];
                          copy[idx].descricao = text;
                          setMarcos(copy);
                        }}
                        style={[styles.input, { flex: 1, marginBottom: 5, height: 40 }]}
                      />
                      <TouchableOpacity
                        onPress={() => {
                          const copy = [...marcos];
                          copy[idx].progresso = copy[idx].progresso === 100 ? 0 : 100;
                          setMarcos(copy);
                          if (copy[idx].progresso === 100) notificarMarco(copy[idx]);
                        }}
                        style={{
                          width: 24, height: 24, borderWidth: 1, borderColor: "#007BFF", borderRadius: 4,
                          justifyContent: "center", alignItems: "center", marginLeft: 8,
                          backgroundColor: marco.progresso === 100 ? "#007BFF" : "transparent"
                        }}
                      >
                         {marco.progresso === 100 && <Text style={{color:'white', fontWeight:'bold'}}>✓</Text>}
                      </TouchableOpacity>
                      <TouchableOpacity onPress={() => excluirMarco(idx)}>
                        <Text style={{ color: "red", marginLeft: 8, fontWeight:'bold' }}>X</Text>
                      </TouchableOpacity>
                    </View>

                    <TextInput
                      style={[styles.input, { marginTop: 5, height: 40 }]}
                      placeholder="Vencimento do marco"
                      value={marco.vencimento}
                      onChangeText={(text) => {
                        const copy = [...marcos];
                        copy[idx].vencimento = text;
                        setMarcos(copy);
                        if (text.length === 10) notificarMarco(copy[idx]);
                      }}
                      keyboardType="numeric"
                      maxLength={10}
                    />
                  </View>
                ))}
               </ScrollView>
               <Button title="Adicionar Marco" onPress={adicionarMarco} />
            </View>

            <View style={styles.rowBtns}>
              {metaEmEdicao && (
                <TouchableOpacity
                  onPress={excluirMeta}
                  style={[styles.actionBtn, { backgroundColor: "#dc3545" }]}
                >
                  <Text style={{ color: "white" }}>Descartar</Text>
                </TouchableOpacity>
              )}
              <View style={{ flex: 1, flexDirection: "row", justifyContent: "flex-end" }}>
                <Button title="Cancelar" color="gray" onPress={() => setModalVisivel(false)} />
                <View style={{ width: 10 }} />
                <Button title={metaEmEdicao ? "Alterar" : "Salvar"} onPress={salvarMeta} />
              </View>
            </View>
          </View>
        </View>
      </Modal>

      {/* Tutorial Overlay (Funcionalidade Antiga) */}
      <TutorialOverlay
        visible={passoAtual === 0}
        titulo="1. Suas Metas"
        texto="Aqui você define seus objetivos de longo prazo. Cadastre uma meta e adicione marcos para acompanhar o progresso!"
        botaoTexto="Ir para Agenda 👉"
        onProximo={proximoPasso}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: "#FFFFFF" },
  header: {
    backgroundColor: "#007BFF",
    padding: 20,
    paddingTop: 50,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderBottomLeftRadius: 30,
    borderBottomRightRadius: 30,
  },
  headerTitle: { fontSize: 24, fontWeight: "bold", color: "white" },
  addButton: {
    backgroundColor: "rgba(255,255,255,0.3)",
    width: 44,
    height: 44,
    borderRadius: 22,
    justifyContent: "center",
    alignItems: "center",
  },
  grid: { flexDirection: "row", flexWrap: "wrap", justifyContent: "space-around", padding: 10 },
  card: {
    width: "46%",
    backgroundColor: "white",
    borderRadius: 15,
    padding: 15,
    marginVertical: 10,
    borderWidth: 1,
    borderColor: "#ddd",
    minHeight: 150,
    justifyContent: "space-between",
  },
  cardTitle: { fontSize: 18, fontWeight: "bold", marginBottom: 5 },
  progressBg: { height: 8, backgroundColor: "#ddd", borderRadius: 4, width: "100%" },
  progressFill: { height: "100%", backgroundColor: "#767676", borderRadius: 4 },
  modalBg: { flex: 1, backgroundColor: "rgba(0,0,0,0.5)", justifyContent: "center", alignItems: "center" },
  modalBox: { backgroundColor: "white", width: "90%", maxHeight: "80%", padding: 20, borderRadius: 10 },
  modalTitle: { fontSize: 20, fontWeight: "bold", marginBottom: 20, textAlign: "center" },
  input: { borderWidth: 1, borderColor: "#ccc", padding: 10, borderRadius: 5, marginBottom: 15 },
  rowBtns: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginTop: 10 },
  actionBtn: { padding: 10, borderRadius: 5 },
});