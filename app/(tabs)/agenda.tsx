import { Picker } from '@react-native-picker/picker';
import { useFocusEffect } from '@react-navigation/native';
import axios from 'axios';
import { useRouter } from 'expo-router';
import React, { useCallback, useMemo, useState, useRef, useEffect } from 'react';
import { Alert, Button, Modal, SafeAreaView, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View, Animated, Easing } from 'react-native';
import { useUser } from '../contexts/UserContext';
import { useTutorial } from '../contexts/TutorialContext';
import { TutorialOverlay } from '../contexts/TutorialOverlay';

const API_URL = 'http://localhost:3001';

type Meta = { id: number; titulo: string; };
type Tarefa = { id: number; data: string; titulo: string; horario: string; cor: string; completa: boolean; meta_id?: number; };
type MarcoCheck = { id: number; descricao: string; vencimento: string; concluido: boolean; cor: string; meta_titulo: string; meta_id: number; };

const DIAS = ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'];
const formatDateLocal = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const PRIORIDADES = [
  { label: 'Alta', value: '#dc3545', displayColor: '#dc3545' },
  { label: 'Média', value: '#ffc107', displayColor: '#ffc107' },
  { label: 'Baixa', value: '#28a745', displayColor: '#28a745' },
];

const PESO_PRIORIDADE: Record<string, number> = {
  '#dc3545': 1, '#ffc107': 2, '#28a745': 3,
};

// --- COMPONENTE DE ANIMAÇÃO DE SUCESSO ---
const SuccessOverlay = ({ visible, cor, novoProgresso, onClose }: any) => {
  const scaleAnim = useRef(new Animated.Value(0)).current;
  const opacityAnim = useRef(new Animated.Value(0)).current;
  const progressAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    if (visible) {
      scaleAnim.setValue(0);
      opacityAnim.setValue(0);
      progressAnim.setValue(0);

      Animated.sequence([
        Animated.parallel([
          Animated.timing(opacityAnim, { toValue: 1, duration: 300, useNativeDriver: true }),
          Animated.spring(scaleAnim, { toValue: 1, friction: 6, useNativeDriver: true }),
        ]),
        Animated.timing(progressAnim, { 
            toValue: novoProgresso, 
            duration: 1500, 
            easing: Easing.out(Easing.exp),
            useNativeDriver: false 
        })
      ]).start();
    }
  }, [visible, novoProgresso, opacityAnim, progressAnim, scaleAnim]); // Dependências adicionadas

  if (!visible) return null;

  const widthInterpolated = progressAnim.interpolate({
    inputRange: [0, 100],
    outputRange: ['0%', '100%'],
    extrapolate: 'clamp'
  });

  return (
    <Modal transparent visible={visible}>
      <View style={[styles.successOverlay, { backgroundColor: cor + 'E6' }]}> 
        <Animated.View style={[styles.successCard, { transform: [{ scale: scaleAnim }], opacity: opacityAnim }]}>
            <Text style={{fontSize: 60}}>🎉</Text>
            <Text style={styles.successTitle}>Parabéns!</Text>
            <Text style={styles.successSub}>Marco Concluído</Text>

            <View style={styles.successProgressContainer}>
                <View style={styles.progressBarBg}>
                    <Animated.View style={[styles.progressBarFill, { backgroundColor: cor, width: widthInterpolated }]} />
                </View>
                <Text style={styles.progressText}>Progresso da Meta: {novoProgresso}%</Text>
            </View>

            <TouchableOpacity style={styles.successBtn} onPress={onClose}>
                <Text style={styles.successBtnText}>Continuar</Text>
            </TouchableOpacity>
        </Animated.View>
      </View>
    </Modal>
  );
};

export default function AgendaScreen() {
  const { userId } = useUser();
  const { passoAtual, proximoPasso } = useTutorial(); 
  const hojeObj = new Date();
  const dicaDoDia = ["Divida tarefas.", "Comece pelo difícil.", "Pomodoro.", "Prazos reais.", "Celebre.", "Organize.", "Pausas."][hojeObj.getDay()];
  const router = useRouter();

  const [diaSelecionado, setDiaSelecionado] = useState(hojeObj);
  const [tarefas, setTarefas] = useState<Tarefa[]>([]);
  const [metas, setMetas] = useState<Meta[]>([]);
  
  const [modalVisivel, setModalVisivel] = useState(false);
  const [tituloTarefa, setTituloTarefa] = useState('');
  const [horarioTarefa, setHorarioTarefa] = useState('');
  const [metaSelecionada, setMetaSelecionada] = useState<number | undefined>(undefined);
  const [corSelecionada, setCorSelecionada] = useState<string>('#28a745');
  const [tarefaEmEdicao, setTarefaEmEdicao] = useState<Tarefa | null>(null);

  const [marcoCheck, setMarcoCheck] = useState<MarcoCheck | null>(null);
  const [modalMarcoVisivel, setModalMarcoVisivel] = useState(false);
  const [showCelebration, setShowCelebration] = useState(false);
  const [novoProgressoMeta, setNovoProgressoMeta] = useState(0);

  // --- CORREÇÃO: Envolver carregarDados no useCallback ---
  const carregarDados = useCallback(async () => {
    if (!userId) return;
    try {
      const [resT, resM, resMarcos] = await Promise.all([
        axios.get(`${API_URL}/tarefas/${userId}`),
        axios.get(`${API_URL}/metas/${userId}`),
        axios.get(`${API_URL}/calendario-marcos/${userId}`)
      ]);
      setTarefas(resT.data);
      setMetas(resM.data);

      const hojeStr = formatDateLocal(new Date());
      const marcosHoje = resMarcos.data.filter((m: MarcoCheck) => 
          m.vencimento === hojeStr && !m.concluido
      );

      if (marcosHoje.length > 0) {
          setMarcoCheck(marcosHoje[0]);
          setModalMarcoVisivel(true);
      }

    } catch (error) { console.error(error); } // Usando 'error' para evitar aviso
  }, [userId]);

  useFocusEffect(useCallback(() => { carregarDados(); }, [carregarDados]));

  const confirmarMarco = async () => {
      if (!marcoCheck) return;
      try {
          const response = await axios.patch(`${API_URL}/marcos/check/${marcoCheck.id}`);
          if (response.data.success) {
              setNovoProgressoMeta(response.data.novoProgresso);
              setModalMarcoVisivel(false); 
              setShowCelebration(true); 
          }
      } catch (error) {
          console.error(error);
          Alert.alert("Erro", "Não foi possível concluir o marco.");
      }
  };

  const adiarMarco = () => {
      setModalMarcoVisivel(false);
  };

  const tarefasFiltradas = useMemo(() => {
    const dataFormatada = formatDateLocal(diaSelecionado);
    const listaDoDia = tarefas.filter(t => t.data === dataFormatada);

    return listaDoDia.sort((a, b) => {
      const pesoA = PESO_PRIORIDADE[a.cor] || 4;
      const pesoB = PESO_PRIORIDADE[b.cor] || 4;
      if (pesoA !== pesoB) return pesoA - pesoB;
      return a.horario.localeCompare(b.horario);
    });
  }, [tarefas, diaSelecionado]);

  const diasVisiveis = useMemo(() => {
    const dias = [];
    const hoje = new Date();
    for (let i = 0; i < 7; i++) {
      const d = new Date(hoje);
      d.setDate(hoje.getDate() + (i - hoje.getDay()));
      dias.push(d);
    }
    return dias;
  }, []);

  const changeHorario = (t: string) => {
    let n = t.replace(/\D/g, '');
    if (n.length > 4) n = n.substring(0, 4);
    if (n.length > 2) n = `${n.substring(0, 2)}:${n.substring(2)}`;
    setHorarioTarefa(n);
  };

  const abrirNovo = () => {
    setTarefaEmEdicao(null); setTituloTarefa(''); setHorarioTarefa(''); setMetaSelecionada(undefined);
    setCorSelecionada('#28a745'); setModalVisivel(true);
  };
  const abrirEdicao = (t: Tarefa) => {
    setTarefaEmEdicao(t); setTituloTarefa(t.titulo); setHorarioTarefa(t.horario); setMetaSelecionada(t.meta_id);
    setCorSelecionada(t.cor); setModalVisivel(true);
  };
  const salvarTarefa = async () => {
    if (!tituloTarefa || !horarioTarefa) return Alert.alert('Erro', 'Preencha título e horário.');
    if (horarioTarefa.length < 5) return Alert.alert('Erro', 'Horário incompleto (HH:MM).');
    try {
      if (tarefaEmEdicao) {
        await axios.put(`${API_URL}/tarefas/editar/${tarefaEmEdicao.id}`, {
          titulo: tituloTarefa, horario: horarioTarefa, meta_id: metaSelecionada, cor: corSelecionada
        });
      } else {
        await axios.post(`${API_URL}/tarefas`, {
          usuario_id: userId, titulo: tituloTarefa, horario: horarioTarefa,
          data: formatDateLocal(diaSelecionado), cor: corSelecionada, meta_id: metaSelecionada
        });
      }
      await carregarDados(); setModalVisivel(false);
    } catch (error) { console.error(error); Alert.alert('Erro', 'Falha ao salvar.'); }
  };
  const excluirTarefa = async () => {
    if (!tarefaEmEdicao) return;
    Alert.alert('Descartar', 'Apagar tarefa?', [{ text: 'Não' }, { text: 'Sim', onPress: async () => {
          try { await axios.delete(`${API_URL}/tarefas/${tarefaEmEdicao.id}`); await carregarDados(); setModalVisivel(false); } 
          catch (error) { console.error(error); Alert.alert('Erro', 'Falha ao excluir.'); }
    }}]);
  };
  const toggleCompleta = async (t: Tarefa) => {
    try {
      const novos = tarefas.map(tk => tk.id === t.id ? { ...tk, completa: !tk.completa } : tk);
      setTarefas(novos);
      await axios.put(`${API_URL}/tarefas/${t.id}`, { completa: !t.completa });
    } catch (error) { console.error(error); carregarDados(); }
  };
  const getMetaNome = (id?: number) => metas.find(m => m.id === id)?.titulo || 'Sem meta';

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView style={styles.container}>
        <View style={styles.header}>
          <View>
            <Text style={styles.headerTitle}>Agenda</Text>
            <Text style={{color:'rgba(255,255,255,0.85)', fontSize: 14}}>Gerencie suas tarefas diárias.</Text>
          </View>
          <View style={{flexDirection:'row'}}>
            <TouchableOpacity onPress={() => router.push('/calendario')} style={styles.btn}><Text style={styles.btnTxt}>📅</Text></TouchableOpacity>
            <TouchableOpacity onPress={abrirNovo} style={styles.btn}><Text style={styles.btnTxt}>+</Text></TouchableOpacity>
          </View>
        </View>

        <View style={styles.daysRow}>
          {diasVisiveis.map((d, i) => (
            <TouchableOpacity key={i} style={[styles.day, formatDateLocal(d) === formatDateLocal(diaSelecionado) && styles.dayActive]} onPress={() => setDiaSelecionado(d)}>
              <Text style={styles.dayTxt}>{DIAS[d.getDay()]}</Text>
              <Text style={styles.dayNum}>{d.getDate()}</Text>
            </TouchableOpacity>
          ))}
        </View>

        <View style={{padding: 20}}>
          {tarefasFiltradas.map(t => (
            <View key={t.id} style={[styles.card, t.completa && styles.cardDone]}>
              <View style={[styles.colorBar, { backgroundColor: t.cor }]} />
              <TouchableOpacity style={{flex:1}} onPress={() => abrirEdicao(t)}>
                <Text style={styles.taskTitle}>{t.titulo}</Text>
                <Text style={{color:'gray'}}>{t.horario}</Text>
                <Text style={{color: '#007BFF', fontSize:12}}>{getMetaNome(t.meta_id)}</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={() => toggleCompleta(t)} style={[styles.check, {borderColor: t.cor}]}>
                {t.completa && <View style={{width:14, height:14, borderRadius:7, backgroundColor: t.cor}}/>}
              </TouchableOpacity>
            </View>
          ))}
          {tarefasFiltradas.length === 0 && <Text style={{textAlign:'center', color:'gray'}}>Nada para hoje.</Text>}
        </View>
        
        <View style={styles.tipCard}>
          <Text style={styles.tipTitle}>Dica do dia</Text>
          <Text style={styles.tipText}>{dicaDoDia}</Text>
        </View>
      </ScrollView>

      <Modal visible={modalVisivel} transparent animationType="slide">
        <View style={styles.modalBg}>
          <View style={styles.modalBox}>
            <Text style={styles.modalTitle}>{tarefaEmEdicao ? 'Editar' : 'Nova'} Tarefa</Text>
            <TextInput style={styles.input} placeholder="Título" value={tituloTarefa} onChangeText={setTituloTarefa} />
            <TextInput style={styles.input} placeholder="Horário (HH:MM)" value={horarioTarefa} onChangeText={changeHorario} keyboardType="numeric" maxLength={5} />
            <Text style={{marginBottom: 8, fontWeight:'bold', color: '#555'}}>Importância:</Text>
            <View style={styles.priorityContainer}>
              {PRIORIDADES.map((p) => (
                <TouchableOpacity key={p.value} style={[ styles.priorityBtn, { backgroundColor: p.displayColor, opacity: corSelecionada === p.value ? 1 : 0.3 } ]} onPress={() => setCorSelecionada(p.value)}>
                  <Text style={styles.priorityText}>{p.label}</Text>
                </TouchableOpacity>
              ))}
            </View>
            <Text style={{marginBottom:5}}>Meta Associada:</Text>
            <View style={styles.pickerBox}>
              <Picker selectedValue={metaSelecionada} onValueChange={setMetaSelecionada}>
                <Picker.Item label="Nenhuma" value={undefined} />
                {metas.map(m => <Picker.Item key={m.id} label={m.titulo} value={m.id} />)}
              </Picker>
            </View>
            <View style={styles.rowBtns}>
              {tarefaEmEdicao && (
                <TouchableOpacity onPress={excluirTarefa} style={[styles.actionBtn, {backgroundColor: '#dc3545'}]}>
                  <Text style={{color:'white'}}>Descartar</Text>
                </TouchableOpacity>
              )}
              <View style={{flex:1, flexDirection:'row', justifyContent:'flex-end'}}>
                <Button title="Cancelar" color="gray" onPress={() => setModalVisivel(false)} />
                <View style={{width:10}}/>
                <Button title={tarefaEmEdicao ? "Alterar" : "Salvar"} onPress={salvarTarefa} />
              </View>
            </View>
          </View>
        </View>
      </Modal>

      <Modal visible={modalMarcoVisivel} transparent animationType="fade">
          <View style={styles.modalBg}>
              <View style={[styles.modalBox, {borderTopWidth: 10, borderTopColor: marcoCheck?.cor}]}>
                  <Text style={{fontSize: 40, textAlign: 'center'}}>🎯</Text>
                  <Text style={styles.modalTitle}>Marco de Hoje!</Text>
                  <Text style={{fontSize: 16, textAlign: 'center', marginBottom: 5}}>
                      Você concluiu o marco:
                  </Text>
                  {/* --- CORREÇÃO DE ASPAS AQUI --- */}
                  <Text style={{fontSize: 18, fontWeight: 'bold', textAlign: 'center', color: '#333', marginBottom: 20}}>
                      &quot;{marcoCheck?.descricao}&quot;
                  </Text>
                  
                  <View style={styles.rowBtns}>
                      <TouchableOpacity onPress={adiarMarco} style={{padding: 15}}>
                          <Text style={{color: 'gray', fontWeight: 'bold'}}>Ainda não</Text>
                      </TouchableOpacity>
                      <TouchableOpacity 
                          onPress={confirmarMarco} 
                          style={{backgroundColor: marcoCheck?.cor || '#007BFF', paddingVertical: 12, paddingHorizontal: 25, borderRadius: 25, elevation: 5}}
                      >
                          <Text style={{color: 'white', fontWeight: 'bold'}}>Sim, Concluí!</Text>
                      </TouchableOpacity>
                  </View>
              </View>
          </View>
      </Modal>

      <SuccessOverlay 
          visible={showCelebration} 
          cor={marcoCheck?.cor || '#007BFF'} 
          novoProgresso={novoProgressoMeta}
          onClose={() => { setShowCelebration(false); carregarDados(); }}
      />

      <TutorialOverlay visible={passoAtual === 1} titulo="2. Organize seu Dia" texto="Defina prioridades para suas tarefas!" botaoTexto="Ir para Gráficos 👉" onProximo={proximoPasso} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#F4F7FE' }, container: { flex: 1 },
  header: { backgroundColor: '#007BFF', padding: 20, paddingTop: 50, flexDirection: 'row', justifyContent:'space-between', alignItems:'center', borderBottomLeftRadius:20, borderBottomRightRadius:20 },
  headerTitle: { color:'white', fontSize:24, fontWeight:'bold' },
  btn: { backgroundColor:'rgba(255,255,255,0.3)', borderRadius:20, width:40, height:40, justifyContent:'center', alignItems:'center', marginLeft:10 },
  btnTxt: { color:'white', fontSize:20 },
  daysRow: { flexDirection: 'row', justifyContent:'space-around', padding:10 },
  day: { alignItems:'center', padding:10, borderRadius:20 },
  dayActive: { backgroundColor: '#007BFF' },
  dayTxt: { fontWeight:'bold' }, dayNum: { fontSize:12 },
  card: { backgroundColor:'white', padding:15, marginBottom:10, borderRadius:10, flexDirection:'row', alignItems:'center', elevation:2 },
  cardDone: { opacity: 0.6, backgroundColor: '#f0f0f0' },
  colorBar: { width:5, height:'100%', marginRight:10, borderRadius:5 },
  taskTitle: { fontWeight:'bold', fontSize:16 },
  check: { width:24, height:24, borderRadius:12, borderWidth:2, justifyContent:'center', alignItems:'center' },
  modalBg: { flex:1, backgroundColor:'rgba(0,0,0,0.5)', justifyContent:'center', alignItems:'center' },
  modalBox: { backgroundColor:'white', width:'85%', padding:25, borderRadius:15, elevation: 10 },
  modalTitle: { fontSize:22, fontWeight:'bold', marginBottom:15, textAlign:'center', color: '#333' },
  input: { borderWidth:1, borderColor:'#ccc', padding:10, borderRadius:5, marginBottom:15 },
  pickerBox: { borderWidth:1, borderColor:'#ccc', borderRadius:5, marginBottom:20 },
  rowBtns: { flexDirection:'row', alignItems:'center', justifyContent:'space-between' },
  actionBtn: { padding:10, borderRadius:5 },
  tipCard: { marginHorizontal: 20, marginBottom: 20, backgroundColor: '#E9F5FF', borderRadius: 15, padding: 20, borderWidth: 1, borderColor: '#BDE0FF' },
  tipTitle: { fontWeight: 'bold', fontSize: 16, marginBottom: 5, color: '#0056B3' },
  tipText: { color: '#003D7A', lineHeight: 20 },
  priorityContainer: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 20 },
  priorityBtn: { flex: 1, padding: 10, marginHorizontal: 3, borderRadius: 5, alignItems: 'center', justifyContent: 'center' },
  priorityText: { color: 'white', fontWeight: 'bold', fontSize: 12 },
  successOverlay: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  successCard: { width: '80%', backgroundColor: 'white', padding: 30, borderRadius: 20, alignItems: 'center', elevation: 20 },
  successTitle: { fontSize: 28, fontWeight: 'bold', color: '#333', marginTop: 10 },
  successSub: { fontSize: 16, color: 'gray', marginBottom: 20 },
  successProgressContainer: { width: '100%', marginBottom: 20 },
  progressBarBg: { height: 15, backgroundColor: '#E0E0E0', borderRadius: 10, overflow: 'hidden', marginBottom: 5 },
  progressBarFill: { height: '100%', borderRadius: 10 },
  progressText: { textAlign: 'center', fontWeight: 'bold', color: '#555' },
  successBtn: { backgroundColor: '#333', paddingVertical: 12, paddingHorizontal: 30, borderRadius: 25 },
  successBtnText: { color: 'white', fontWeight: 'bold', fontSize: 16 },
});