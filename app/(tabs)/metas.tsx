import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { Alert, Button, Modal, SafeAreaView, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { useUser } from '../contexts/UserContext';
import { useTutorial } from '../contexts/TutorialContext';
import { TutorialOverlay } from '../contexts/TutorialOverlay';

const API_URL = 'http://localhost:3001';

type Meta = { id: number; titulo: string; descricao: string; progresso: number; vencimento: string; };

export default function MetasScreen() {
  const { userId } = useUser();
  const { passoAtual, proximoPasso } = useTutorial(); // <--- Tutorial Hook
  const [metas, setMetas] = useState<Meta[]>([]);
  const [modalVisivel, setModalVisivel] = useState(false);
  const [metaEmEdicao, setMetaEmEdicao] = useState<Meta | null>(null);

  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [vencimento, setVencimento] = useState('');
  const [progresso, setProgresso] = useState('');

  const carregarMetas = async () => {
    if (!userId) return;
    try {
      const response = await axios.get(`${API_URL}/metas/${userId}`);
      setMetas(response.data);
    } catch (e) { console.error(e); }
  };

  useEffect(() => { carregarMetas(); }, [userId]);

  const changeData = (t: string) => {
    let n = t.replace(/\D/g, '');
    if (n.length > 8) n = n.substring(0, 8);
    if (n.length > 4) n = `${n.substring(0, 2)}/${n.substring(2, 4)}/${n.substring(4)}`;
    else if (n.length > 2) n = `${n.substring(0, 2)}/${n.substring(2)}`;
    setVencimento(n);
  };

  const changeProg = (t: string) => {
    const n = t.replace(/\D/g, '');
    if (n !== '' && parseInt(n) > 100) return;
    setProgresso(n);
  };

  const abrirNovo = () => {
    setMetaEmEdicao(null); setTitulo(''); setDescricao(''); setVencimento(''); setProgresso('');
    setModalVisivel(true);
  };

  const abrirEdicao = (m: Meta) => {
    setMetaEmEdicao(m); setTitulo(m.titulo); setDescricao(m.descricao); setVencimento(m.vencimento); setProgresso(m.progresso.toString());
    setModalVisivel(true);
  };

  const salvarMeta = async () => {
    if (!descricao || !vencimento) return Alert.alert('Erro', 'Preencha descrição e vencimento.');
    if (vencimento.length < 10) return Alert.alert('Erro', 'Data incompleta (DD/MM/AAAA).');

    try {
      if (metaEmEdicao) {
        await axios.put(`${API_URL}/metas/${metaEmEdicao.id}`, {
          titulo: titulo || `Meta ${metaEmEdicao.id}`, descricao, vencimento, progresso: parseInt(progresso) || 0
        });
      } else {
        await axios.post(`${API_URL}/metas`, {
          usuario_id: userId, titulo: titulo || `Meta ${metas.length + 1}`, descricao, vencimento, progresso: parseInt(progresso) || 0
        });
      }
      await carregarMetas();
      setModalVisivel(false);
    } catch (e) { Alert.alert('Erro', 'Falha ao salvar.'); }
  };

  const excluirMeta = async () => {
    if (!metaEmEdicao) return;
    Alert.alert('Descartar', 'Apagar meta?', [
      { text: 'Não' },
      { text: 'Sim', onPress: async () => {
          try {
            await axios.delete(`${API_URL}/metas/${metaEmEdicao.id}`);
            await carregarMetas();
            setModalVisivel(false);
          } catch (e) { Alert.alert('Erro', 'Falha ao excluir.'); }
        }
      }
    ]);
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.header}>
        <View><Text style={styles.headerTitle}>Metas</Text><Text style={{color:'white'}}>Defina seus objetivos</Text></View>
        <TouchableOpacity style={styles.addButton} onPress={abrirNovo}><Text style={{color:'white', fontSize:24}}>+</Text></TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.grid}>
        {metas.length > 0 ? metas.map(m => (
          <TouchableOpacity key={m.id} style={styles.card} onPress={() => abrirEdicao(m)}>
            <Text style={styles.cardTitle}>{m.titulo}</Text>
            <Text style={{color:'gray'}}>Descrição: {m.descricao}</Text>
            <View style={{marginTop:10}}>
              {m.progresso === 100 ? <Text style={{fontStyle:'italic', color:'gray'}}>Concluído!</Text> : 
                <>
                  <View style={styles.progressBg}><View style={[styles.progressFill, {width: `${m.progresso}%`}]} /></View>
                  <Text style={{fontSize:12, color:'gray', marginTop:5}}>Vence em: {m.vencimento}</Text>
                </>
              }
            </View>
          </TouchableOpacity>
        )) : <View style={{marginTop:50, alignItems:'center'}}><Text>Nenhuma meta.</Text></View>}
      </ScrollView>

      <Modal visible={modalVisivel} transparent animationType="slide">
        <View style={styles.modalBg}>
          <View style={styles.modalBox}>
            <Text style={styles.modalTitle}>{metaEmEdicao ? 'Editar' : 'Nova'} Meta</Text>
            <TextInput style={styles.input} placeholder="Título" value={titulo} onChangeText={setTitulo} />
            <TextInput style={styles.input} placeholder="Descrição" value={descricao} onChangeText={setDescricao} />
            <TextInput style={styles.input} placeholder="Vencimento (DD/MM/AAAA)" value={vencimento} onChangeText={changeData} keyboardType="numeric" maxLength={10} />
            <TextInput style={styles.input} placeholder="Progresso (0-100)" value={progresso} onChangeText={changeProg} keyboardType="numeric" maxLength={3} />
            
            <View style={styles.rowBtns}>
              {metaEmEdicao && (
                <TouchableOpacity onPress={excluirMeta} style={[styles.actionBtn, {backgroundColor: '#dc3545'}]}>
                  <Text style={{color:'white'}}>Descartar</Text>
                </TouchableOpacity>
              )}
              <View style={{flex:1, flexDirection:'row', justifyContent:'flex-end'}}>
                <Button title="Cancelar" color="gray" onPress={() => setModalVisivel(false)} />
                <View style={{width:10}}/>
                <Button title={metaEmEdicao ? "Alterar" : "Salvar"} onPress={salvarMeta} />
              </View>
            </View>
          </View>
        </View>
      </Modal>

      {/* --- TUTORIAL OVERLAY --- */}
      <TutorialOverlay 
        visible={passoAtual === 0}
        titulo="1. Suas Metas"
        texto="Aqui você define seus objetivos de longo prazo. É o ponto de partida! Cadastre uma meta e depois você poderá dividi-la em tarefas menores."
        botaoTexto="Ir para Agenda 👉"
        onProximo={proximoPasso}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#FFFFFF' },
  header: { backgroundColor: '#007BFF', padding: 20, paddingTop: 50, flexDirection:'row', justifyContent:'space-between', alignItems:'center', borderBottomLeftRadius:30, borderBottomRightRadius:30 },
  headerTitle: { fontSize: 24, fontWeight: 'bold', color: 'white' },
  addButton: { backgroundColor: 'rgba(255,255,255,0.3)', width: 44, height: 44, borderRadius: 22, justifyContent: 'center', alignItems: 'center' },
  grid: { flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'space-around', padding: 10 },
  card: { width: '46%', backgroundColor: 'white', borderRadius: 15, padding: 15, marginVertical: 10, borderWidth: 1, borderColor: '#ddd', minHeight: 150, justifyContent:'space-between' },
  cardTitle: { fontSize: 18, fontWeight: 'bold', marginBottom: 5 },
  progressBg: { height: 8, backgroundColor: '#ddd', borderRadius: 4, width: '100%' },
  progressFill: { height: '100%', backgroundColor: '#767676', borderRadius: 4 },
  modalBg: { flex:1, backgroundColor:'rgba(0,0,0,0.5)', justifyContent:'center', alignItems:'center' },
  modalBox: { backgroundColor:'white', width:'85%', padding:20, borderRadius:10 },
  modalTitle: { fontSize:20, fontWeight:'bold', marginBottom:20, textAlign:'center' },
  input: { borderWidth:1, borderColor:'#ccc', padding:10, borderRadius:5, marginBottom:15 },
  rowBtns: { flexDirection:'row', alignItems:'center', justifyContent:'space-between', marginTop:10 },
  actionBtn: { padding:10, borderRadius:5 }
});