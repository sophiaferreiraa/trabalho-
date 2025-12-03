import React, { useState, useCallback, useRef, useEffect } from "react";
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
  Animated,
  KeyboardAvoidingView, // Importante para comportamento com teclado
} from "react-native";
import axios from "axios";
import { useFocusEffect } from "expo-router";
// import * as Notifications from "expo-notifications"; // Comentado para evitar erro no Expo Go
import { useUser } from "../contexts/UserContext";
import { useTutorial } from "../contexts/TutorialContext";
import { TutorialOverlay } from "../contexts/TutorialOverlay"; 

const API_URL = "http://192.168.3.8:3001";

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
  cor: string;
};

const CORES_DISPONIVEIS = [
  "#007BFF", "#28a745", "#dc3545", "#ffc107", "#6f42c1", "#e83e8c", "#fd7e14", "#20c997",
];

// --- COMPONENTE DE ANIMAÇÃO DE SUCESSO ---
const SuccessOverlay = ({ visible, cor, titulo, onClose }: any) => {
  const scaleAnim = useRef(new Animated.Value(0)).current;
  const opacityAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    if (visible) {
      scaleAnim.setValue(0);
      opacityAnim.setValue(0);

      Animated.sequence([
        Animated.parallel([
          Animated.timing(opacityAnim, { toValue: 1, duration: 300, useNativeDriver: true }),
          Animated.spring(scaleAnim, { toValue: 1, friction: 6, useNativeDriver: true }),
        ])
      ]).start();
    }
  }, [visible, opacityAnim, scaleAnim]);

  if (!visible) return null;

  return (
    <Modal transparent visible={visible}>
      <View style={[styles.successOverlay, { backgroundColor: cor + 'E6' }]}> 
        <Animated.View style={[styles.successCard, { transform: [{ scale: scaleAnim }], opacity: opacityAnim }]}>
            <Text style={{fontSize: 60}}>🏆</Text>
            <Text style={styles.successTitle}>Parabéns!</Text>
            <Text style={styles.successSub}>Meta Concluída:</Text>
            <Text style={[styles.successMetaTitle, { color: cor }]}>{titulo}</Text>

            <TouchableOpacity style={styles.successBtn} onPress={onClose}>
                <Text style={styles.successBtnText}>Incrível!</Text>
            </TouchableOpacity>
        </Animated.View>
      </View>
    </Modal>
  );
};

// --- OVERLAY DE AJUDA/TUTORIAL MANUAL ---
const HelpTutorialOverlay = ({ step, setStep }: { step: number; setStep: (n: number) => void }) => {
  if (step === 0) return null;

  const textos = [
    {}, 
    {
      titulo: "1. O que é Planejamento de Meta?",
      texto: "Aqui você cria sua meta principal. Para te ajudar, pense nessas perguntas:\n\nQual objetivo você quer alcançar?\n\nPor que essa meta é importante para você?\n\nQuando você quer concluir essa meta?",
      botaoTexto: "Próximo -->",
    },
    {
      titulo: "2. Como criar uma nova meta?",
      texto: "Ao tocar em “+”, responda mentalmente ou escreva:\n\nQual será o nome da sua meta? (ex: “Terminar curso X”)\n\nO que exatamente você precisa fazer para alcançá-la?\n\nQual é a data final para concluir?",
      botaoTexto: "Entendi -->",
    },
    {
      titulo: "3. O que são marcos de progresso?",
      texto: "Eles ajudam a dividir sua meta em pequenos passos. Pergunte a si mesmo:\n\nQuais etapas menores preciso cumprir para chegar ao objetivo final?\n\nQual ordem faz mais sentido para realizar esses passos?\n\nQuais desses passos posso fazer primeiro?",
      botaoTexto: "Próximo -->",
    },
    {
      titulo: "4. Como criar marcos (mini metas)?",
      texto: "Ao adicionar um marco, pense:\n\nQual é o próximo pequeno passo para avançar na minha meta?\n\nQual nome descreve bem esse passo?\n\nAté quando eu quero terminar essa etapa?",
      botaoTexto: "Finalizar ✔",
    },
  ];

  const t = textos[step];

  return (
    <Modal transparent animationType="fade" visible>
      <View style={styles.tutorialBg}>
        <View style={styles.tutorialBox}>
          <Text style={styles.tutorialTitle}>{t.titulo}</Text>
          <Text style={styles.tutorialMsg}>{t.texto}</Text>

          <TouchableOpacity
            style={styles.tutorialBtn}
            onPress={() =>
              step === textos.length - 1 ? setStep(0) : setStep(step + 1)
            }
          >
            <Text style={{ color: "white", fontWeight: "bold" }}>{t.botaoTexto}</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
  );
};

export default function MetasScreen() {
  const { userId } = useUser();
  const { passoAtual, proximoPasso } = useTutorial();

  const [helpStep, setHelpStep] = useState(0);
  const [metas, setMetas] = useState<Meta[]>([]);
  const [modalVisivel, setModalVisivel] = useState(false);
  const [metaEmEdicao, setMetaEmEdicao] = useState<Meta | null>(null);

  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [vencimento, setVencimento] = useState("");
  const [marcos, setMarcos] = useState<Marco[]>([]);
  const [corSelecionada, setCorSelecionada] = useState("#007BFF");

  const [showCelebration, setShowCelebration] = useState(false);
  const [celebratedMeta, setCelebratedMeta] = useState<{titulo: string, cor: string} | null>(null);

  useEffect(() => {
    // Permissão de notificação removida para evitar crash no Expo Go
  }, []);

  const carregarMetas = useCallback(async () => {
    if (!userId) return;
    try {
      const response = await axios.get(`${API_URL}/metas/${userId}`);
      const metasAtivas = response.data.filter((m: Meta) => m.progresso < 100);
      setMetas(metasAtivas);
    } catch (e) {
      console.error(e);
    }
  }, [userId]);

  useFocusEffect(useCallback(() => { carregarMetas(); }, [carregarMetas]));

  const changeData = (t: string) => {
    let n = t.replace(/\D/g, "");
    if (n.length > 8) n = n.substring(0, 8);
    if (n.length > 4) n = `${n.substring(0, 2)}/${n.substring(2, 4)}/${n.substring(4)}`;
    else if (n.length > 2) n = `${n.substring(0, 2)}/${n.substring(2)}`;
    setVencimento(n);
  };

  const abrirNovo = () => {
    setMetaEmEdicao(null);
    setTitulo("");
    setDescricao("");
    setVencimento("");
    setMarcos([]);
    setCorSelecionada("#007BFF");
    setModalVisivel(true);
  };

  const abrirEdicao = (m: Meta) => {
    setMetaEmEdicao(m);
    setTitulo(m.titulo);
    setDescricao(m.descricao);
    setVencimento(m.vencimento ? m.vencimento.split('-').reverse().join('/') : ''); 
    setMarcos(m.marcos || []);
    setCorSelecionada(m.cor || "#007BFF");
    setModalVisivel(true);
  };

  const calcularProgressoMeta = () => {
    if (marcos.length === 0) return 0;
    const soma = marcos.reduce((acc, m) => acc + m.progresso, 0);
    return Math.round(soma / marcos.length);
  };

  const salvarMeta = async () => {
    if (!descricao || !vencimento) return Alert.alert("Erro", "Preencha descrição e vencimento.");
    if (vencimento.length < 10) return Alert.alert("Erro", "Data incompleta (DD/MM/AAAA).");

    const progresso = calcularProgressoMeta();
    const vencimentoISO = vencimento.split('/').reverse().join('-');
    const tituloFinal = titulo || (metaEmEdicao ? `Meta ${metaEmEdicao.id}` : `Meta ${metas.length + 1}`);

    try {
      if (metaEmEdicao) {
        await axios.put(`${API_URL}/metas/${metaEmEdicao.id}`, {
          titulo: tituloFinal,
          descricao,
          vencimento: vencimentoISO,
          progresso,
          marcos,
          cor: corSelecionada
        });
      } else {
        await axios.post(`${API_URL}/metas`, {
          usuario_id: userId,
          titulo: tituloFinal,
          descricao,
          vencimento: vencimentoISO,
          progresso,
          marcos,
          cor: corSelecionada
        });
      }

      setModalVisivel(false);
      await carregarMetas();

      if (progresso === 100) {
        setCelebratedMeta({ titulo: tituloFinal, cor: corSelecionada });
        setShowCelebration(true);
      }
    } catch (e) {
      Alert.alert("Erro", "Falha ao salvar.");
      console.error(e);
    }
  };

  const excluirMeta = async () => {
    if (!metaEmEdicao) return;
    Alert.alert("Descartar", "Apagar meta e seus marcos?", [
      { text: "Não" },
      {
        text: "Sim", style: "destructive",
        onPress: async () => {
          try {
            await axios.delete(`${API_URL}/metas/${metaEmEdicao.id}`);
            await carregarMetas();
            setModalVisivel(false);
          } catch (e) {
            console.error(e);
            Alert.alert("Erro", "Falha ao excluir.");
          }
        },
      },
    ]);
  };

  const adicionarMarco = () => {
    setMarcos([...marcos, { id: Date.now(), descricao: "", progresso: 0, vencimento: "" }]);
  };

  const excluirMarco = (idx: number) => {
    const copy = [...marcos];
    copy.splice(idx, 1);
    setMarcos(copy);
  };

  const notificarMarcoPositivo = async (marco: Marco) => {
    // Código de notificação comentado para evitar crash no Expo Go
    /*
    const frasesMotivacionais = ["Você é incrível!", "Arrasou!", "Excelente trabalho!", "Continue assim!"];
    const frase = frasesMotivacionais[Math.floor(Math.random() * frasesMotivacionais.length)];
    if (Platform.OS !== "web") {
      await Notifications.scheduleNotificationAsync({
        content: { title: "🎉 Marco Concluído!", body: `${marco.descricao}\n${frase}` },
        trigger: null,
      });
    }
    */
  };

  return (
    <SafeAreaView style={styles.safeArea}>
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

      <TouchableOpacity
        style={styles.helpButton}
        onPress={() => setHelpStep(1)}
      >
        <Text style={styles.helpButtonText}>Como funciona o planejamento de metas?</Text>
      </TouchableOpacity>

      <ScrollView contentContainerStyle={styles.grid}>
        {metas.length > 0 ? (
          metas.map((m) => (
            <TouchableOpacity
              key={m.id}
              style={[styles.card, { borderLeftWidth: 5, borderLeftColor: m.cor || '#007BFF' }]}
              onPress={() => abrirEdicao(m)}
            >
              <Text style={styles.cardTitle}>{m.titulo}</Text>
              <Text style={{ color: "gray" }}>{m.descricao}</Text>
              <View style={{ marginTop: 10 }}>
                {m.progresso === 100 ? (
                  <Text style={{ fontStyle: "italic", color: "gray" }}>Concluído!</Text>
                ) : (
                  <>
                    <View style={styles.progressBg}>
                      <View style={[styles.progressFill, { width: `${m.progresso}%`, backgroundColor: m.cor || '#767676' }]} />
                    </View>
                    <Text style={{ fontSize: 12, color: "gray", marginTop: 5 }}>
                      Vence: {m.vencimento ? m.vencimento.split('-').reverse().join('/') : 'N/A'}
                    </Text>
                  </>
                )}
              </View>
            </TouchableOpacity>
          ))
        ) : (
          <View style={{ marginTop: 50, alignItems: "center" }}>
            <Text>Nenhuma meta ativa.</Text>
          </View>
        )}
      </ScrollView>

      {/* Modal de Edição - Agora com ScrollView */}
      <Modal visible={modalVisivel} transparent animationType="slide">
        <KeyboardAvoidingView 
          behavior={Platform.OS === "ios" ? "padding" : "height"}
          style={styles.modalBg}
        >
          <View style={styles.modalBox}>
            <ScrollView contentContainerStyle={{ paddingBottom: 20 }}> 
              <Text style={styles.modalTitle}>{metaEmEdicao ? "Editar" : "Nova"} Meta</Text>

              <TextInput style={styles.input} placeholder="Título" value={titulo} onChangeText={setTitulo} />
              <TextInput style={styles.input} placeholder="Descrição" value={descricao} onChangeText={setDescricao} />
              <TextInput style={styles.input} placeholder="Vencimento (DD/MM/AAAA)" value={vencimento} onChangeText={changeData} keyboardType="numeric" maxLength={10} />

              <Text style={{fontWeight:'bold', marginBottom:5}}>Cor da Meta:</Text>
              <View style={{flexDirection: 'row', justifyContent: 'space-between', marginBottom: 15}}>
                {CORES_DISPONIVEIS.map(cor => (
                  <TouchableOpacity 
                    key={cor}
                    onPress={() => setCorSelecionada(cor)}
                    style={{
                      width: 30, height: 30, borderRadius: 15, backgroundColor: cor,
                      borderWidth: corSelecionada === cor ? 3 : 0, borderColor: '#333'
                    }}
                  />
                ))}
              </View>
              
              <Text style={{ fontWeight: "bold", marginBottom: 5 }}>Marcos / Sub-metas:</Text>
              <View style={{ marginBottom: 10 }}> 
                {marcos.map((marco, idx) => (
                  <View key={marco.id || idx} style={{ marginBottom: 10, borderWidth: 1, borderRadius: 5, padding: 5, borderColor: marco.progresso < 100 ? "#CCC" : "#28a745", backgroundColor: marco.progresso < 100 ? "#FFF" : "#d4edda" }}>
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
                    <TextInput
                      placeholder="Vencimento marco"
                      value={marco.vencimento && marco.vencimento.includes('-') ? marco.vencimento.split('-').reverse().join('/') : marco.vencimento}
                      onChangeText={(text) => {
                        let n = text.replace(/\D/g, "");
                        if (n.length > 8) n = n.substring(0, 8);
                        if (n.length > 4) n = `${n.substring(0, 2)}/${n.substring(2, 4)}/${n.substring(4)}`;
                        else if (n.length > 2) n = `${n.substring(0, 2)}/${n.substring(2)}`;
                        const copy = [...marcos];
                        copy[idx].vencimento = n;
                        setMarcos(copy);
                      }}
                      keyboardType="numeric"
                      style={[styles.input, { height: 40 }]}
                      maxLength={10}
                    />
                    <View style={{ flexDirection: "row", marginTop: 5 }}>
                      <TouchableOpacity
                        onPress={() => {
                          if (!marcos[idx].vencimento || marcos[idx].vencimento.length !== 10) return Alert.alert("Erro", "Preencha a data corretamente.");
                          const copy = [...marcos];
                          copy[idx].progresso = copy[idx].progresso === 100 ? 0 : 100;
                          setMarcos(copy);
                        }}
                        style={{ backgroundColor: marco.progresso === 100 ? "#28a745" : "#007BFF", flex: 1, padding: 5, borderRadius: 5, alignItems: "center", marginRight: 5 }}
                      >
                        <Text style={{ color: "white" }}>{marco.progresso === 100 ? "Concluído" : "Concluir"}</Text>
                      </TouchableOpacity>
                      <TouchableOpacity onPress={() => excluirMarco(idx)} style={{ backgroundColor: "#dc3545", flex: 1, padding: 5, borderRadius: 5, alignItems: "center" }}>
                        <Text style={{ color: "white" }}>Excluir</Text>
                      </TouchableOpacity>
                    </View>
                  </View>
                ))}
              </View>
              <Button title="Adicionar Marco" onPress={adicionarMarco} color="#007BFF" />

              <View style={styles.rowBtns}>
                {metaEmEdicao && (
                  <TouchableOpacity onPress={excluirMeta} style={[styles.actionBtn, { backgroundColor: "#dc3545" }]}>
                    <Text style={{ color: "white" }}>Descartar</Text>
                  </TouchableOpacity>
                )}
                <View style={{ flex: 1, flexDirection: "row", justifyContent: "flex-end" }}>
                  <Button title="Cancelar" color="gray" onPress={() => setModalVisivel(false)} />
                  <View style={{ width: 10 }} />
                  <Button title={metaEmEdicao ? "Alterar" : "Salvar"} onPress={salvarMeta} color={corSelecionada} />
                </View>
              </View>
            </ScrollView>
          </View>
        </KeyboardAvoidingView>
      </Modal>

      <SuccessOverlay 
          visible={showCelebration} 
          cor={celebratedMeta?.cor || '#007BFF'} 
          titulo={celebratedMeta?.titulo}
          onClose={() => setShowCelebration(false)}
      />

      <TutorialOverlay
        visible={passoAtual === 0}
        titulo="1. Suas Metas"
        texto="Cadastre uma meta e defina sua cor. Adicione marcos para que eles apareçam coloridos no seu Calendário!"
        botaoTexto="Ir para Agenda 👉"
        onProximo={proximoPasso}
      />

      <HelpTutorialOverlay step={helpStep} setStep={setHelpStep} />
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
  helpButton: {
    backgroundColor: "#1669d6ff",
    padding: 10,
    borderRadius: 8,
    margin: 10,
    alignItems: "center",
  },
  helpButtonText: { color: "white", fontWeight: "bold" },
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
  progressFill: { height: "100%", borderRadius: 4 },
  modalBg: { flex: 1, backgroundColor: "rgba(0,0,0,0.5)", justifyContent: "center", alignItems: "center" },
  modalBox: { backgroundColor: "white", width: "90%", height: "80%", padding: 20, borderRadius: 10 }, // Height fixo ajuda no scroll
  modalTitle: { fontSize: 20, fontWeight: "bold", marginBottom: 15, textAlign: "center" },
  input: { borderWidth: 1, borderColor: "#ccc", padding: 10, borderRadius: 5, marginBottom: 10 },
  rowBtns: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginTop: 20 },
  actionBtn: { padding: 10, borderRadius: 5 },
  successOverlay: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  successCard: { width: '80%', backgroundColor: 'white', padding: 30, borderRadius: 20, alignItems: 'center', elevation: 20 },
  successTitle: { fontSize: 28, fontWeight: 'bold', color: '#333', marginTop: 10 },
  successSub: { fontSize: 16, color: 'gray', marginTop: 10 },
  successMetaTitle: { fontSize: 20, fontWeight: 'bold', marginVertical: 10, textAlign: 'center' },
  successBtn: { backgroundColor: '#333', paddingVertical: 12, paddingHorizontal: 30, borderRadius: 25, marginTop: 20 },
  successBtnText: { color: 'white', fontWeight: 'bold', fontSize: 16 },
  tutorialBg: { flex: 1, backgroundColor: "rgba(0,0,0,0.7)", justifyContent: "center", alignItems: "center" },
  tutorialBox: { backgroundColor: "#fff", padding: 20, borderRadius: 8, width: "85%" },
  tutorialTitle: { fontSize: 18, fontWeight: "bold", marginBottom: 10, color: "#333" },
  tutorialMsg: { marginBottom: 20, color: "#555" },
  tutorialBtn: { backgroundColor: "#007BFF", padding: 10, borderRadius: 5, alignItems: "center" },
});