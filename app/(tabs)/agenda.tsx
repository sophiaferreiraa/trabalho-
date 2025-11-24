import { Picker } from '@react-native-picker/picker';
import { useFocusEffect } from '@react-navigation/native';
import axios from 'axios';
import { useRouter } from 'expo-router';
import React, { useCallback, useMemo, useState } from 'react';
import { Alert, Button, Modal, SafeAreaView, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { useUser } from '../contexts/UserContext';

// Use o IP da sua rede (NÃO use localhost)
const API_URL = 'http://200.131.182.129:3001';

type Meta = { id: number; titulo: string; };
type Tarefa = { id: number; data: string; titulo: string; horario: string; cor: string; completa: boolean; meta_id?: number; };

const DIAS = ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'];
const formatDateLocal = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export default function AgendaScreen() {
  const { userId } = useUser();
  const hojeObj = new Date();
  const dicaDoDia = ["Divida tarefas.", "Comece pelo difícil.", "Pomodoro.", "Prazos reais.", "Celebre.", "Organize.", "Pausas."][hojeObj.getDay()];
  const router = useRouter();

  const [diaSelecionado, setDiaSelecionado] = useState(hojeObj);
  const [tarefas, setTarefas] = useState<Tarefa[]>([]);
  const [metas, setMetas] = useState<Meta[]>([]);
  
  // Modal
  const [modalVisivel, setModalVisivel] = useState(false);
  const [tituloTarefa, setTituloTarefa] = useState('');
  const [horarioTarefa, setHorarioTarefa] = useState('');
  const [metaSelecionada, setMetaSelecionada] = useState<number | undefined>(undefined);
  const [tarefaEmEdicao, setTarefaEmEdicao] = useState<Tarefa | null>(null);

  const carregarDados = async () => {
    if (!userId) return;
    try {
      const [resT, resM] = await Promise.all([
        axios.get(`${API_URL}/tarefas/${userId}`),
        axios.get(`${API_URL}/metas/${userId}`)
      ]);
      setTarefas(resT.data);
      setMetas(resM.data);
    } catch (e) { console.error(e); }
  };

  useFocusEffect(useCallback(() => { carregarDados(); }, [userId]));

  const tarefasFiltradas = useMemo(() => {
    const dataFormatada = formatDateLocal(diaSelecionado);
    return tarefas.filter(t => t.data === dataFormatada);
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

  // --- VALIDAÇÃO HORÁRIO ---
  const changeHorario = (t: string) => {
    let n = t.replace(/\D/g, '');
    if (n.length > 4) n = n.substring(0, 4);
    if (n.length > 2) n = `${n.substring(0, 2)}:${n.substring(2)}`;
    setHorarioTarefa(n);
  };

  // --- MODAL ---
  const abrirNovo = () => {
    setTarefaEmEdicao(null); setTituloTarefa(''); setHorarioTarefa(''); setMetaSelecionada(undefined);
    setModalVisivel(true);
  };

  const abrirEdicao = (t: Tarefa) => {
    setTarefaEmEdicao(t); setTituloTarefa(t.titulo); setHorarioTarefa(t.horario); setMetaSelecionada(t.meta_id);
    setModalVisivel(true);
  };

  const salvarTarefa = async () => {
    if (!tituloTarefa || !horarioTarefa) return Alert.alert('Erro', 'Preencha título e horário.');
    if (horarioTarefa.length < 5) return Alert.alert('Erro', 'Horário incompleto (HH:MM).');

    try {
      if (tarefaEmEdicao) {
        // ALTERAR
        await axios.put(`${API_URL}/tarefas/editar/${tarefaEmEdicao.id}`, {
          titulo: tituloTarefa, horario: horarioTarefa, meta_id: metaSelecionada
        });
      } else {
        // CRIAR
        const cor = ['#28a745', '#ffc107', '#007bff', '#dc3545'][Math.floor(Math.random() * 4)];
        await axios.post(`${API_URL}/tarefas`, {
          usuario_id: userId, titulo: tituloTarefa, horario: horarioTarefa,
          data: formatDateLocal(diaSelecionado), cor, meta_id: metaSelecionada
        });
      }
      await carregarDados();
      setModalVisivel(false);
    } catch (e) { Alert.alert('Erro', 'Falha ao salvar. Verifique conexão.'); }
  };

  const excluirTarefa = async () => {
    if (!tarefaEmEdicao) return;
    Alert.alert('Descartar', 'Apagar tarefa?', [
      { text: 'Não' },
      { text: 'Sim', onPress: async () => {
          try {
            await axios.delete(`${API_URL}/tarefas/${tarefaEmEdicao.id}`);
            await carregarDados();
            setModalVisivel(false);
          } catch (e) { Alert.alert('Erro', 'Falha ao excluir.'); }
        } 
      }
    ]);
  };

  const toggleCompleta = async (t: Tarefa) => {
    try {
      const novos = tarefas.map(tk => tk.id === t.id ? { ...tk, completa: !tk.completa } : tk);
      setTarefas(novos);
      await axios.put(`${API_URL}/tarefas/${t.id}`, { completa: !t.completa });
    } catch (e) { carregarDados(); }
  };

  const getMetaNome = (id?: number) => metas.find(m => m.id === id)?.titulo || 'Sem meta';

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>Agenda</Text>
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
            
            <Text style={{marginBottom:5}}>Meta:</Text>
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
  modalBox: { backgroundColor:'white', width:'90%', padding:20, borderRadius:10 },
  modalTitle: { fontSize:20, fontWeight:'bold', marginBottom:20, textAlign:'center' },
  input: { borderWidth:1, borderColor:'#ccc', padding:10, borderRadius:5, marginBottom:15 },
  pickerBox: { borderWidth:1, borderColor:'#ccc', borderRadius:5, marginBottom:20 },
  rowBtns: { flexDirection:'row', alignItems:'center', justifyContent:'space-between' },
  actionBtn: { padding:10, borderRadius:5 },
  tipCard: { marginHorizontal: 20, marginBottom: 20, backgroundColor: '#E9F5FF', borderRadius: 15, padding: 20, borderWidth: 1, borderColor: '#BDE0FF' },
  tipTitle: { fontWeight: 'bold', fontSize: 16, marginBottom: 5, color: '#0056B3' },
  tipText: { color: '#003D7A', lineHeight: 20 },
});