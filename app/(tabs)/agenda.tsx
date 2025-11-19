import { Picker } from '@react-native-picker/picker';
import { useFocusEffect } from '@react-navigation/native';
import axios from 'axios';
import { useRouter } from 'expo-router';
import React, { useCallback, useMemo, useState } from 'react';
import { Button, Modal, SafeAreaView, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';

const API_URL = 'http://192.168.3.8:3001';

type Meta = { id: number; titulo: string; };
type Tarefa = {
  id: number;
  data: string;
  titulo: string;
  horario: string;
  cor: string;
  completa: boolean;
  metaId?: number; // corrigido: agora usamos metaId (consistente com o backend)
};

const DICAS_DA_SEMANA = [
  "Divida tarefas grandes em partes menores.",
  "Comece pela tarefa mais difícil.",
  "Técnica Pomodoro: 25min foco, 5min pausa.",
  "Defina prazos realistas.",
  "Celebre pequenas vitórias.",
  "Organize seu espaço de trabalho.",
  "Lembre-se de fazer pausas."
];

const DIAS_SEMANA_ABREV = ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'];

// Função para formatar data no formato YYYY-MM-DD usando data local
const formatDateLocal = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export default function AgendaScreen() {
  const hojeObj = new Date();
  const dicaDoDia = DICAS_DA_SEMANA[hojeObj.getDay()];
  const router = useRouter();

  const [diaSelecionado, setDiaSelecionado] = useState(hojeObj);
  const [tarefas, setTarefas] = useState<Tarefa[]>([]);
  const [metas, setMetas] = useState<Meta[]>([]);
  const [modalVisivel, setModalVisivel] = useState(false);
  const [tituloTarefa, setTituloTarefa] = useState('');
  const [horarioTarefa, setHorarioTarefa] = useState('');
  const [metaSelecionada, setMetaSelecionada] = useState<number | undefined>(undefined);

  const carregarDados = async () => {
    try {
      const [resTarefas, resMetas] = await Promise.all([
        axios.get(`${API_URL}/tarefas`),
        axios.get(`${API_URL}/metas`)
      ]);
      setTarefas(resTarefas.data);
      setMetas(resMetas.data);
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
    }
  };

  useFocusEffect(
    useCallback(() => {
      carregarDados();
    }, [])
  );

  // Filtra tarefas pelo dia selecionado (usando a data formatada localmente)
  const tarefasFiltradas = useMemo(() => {
    const dataFormatada = formatDateLocal(diaSelecionado);
    return tarefas.filter(t => t.data === dataFormatada);
  }, [tarefas, diaSelecionado]);

  const diasDaSemanaVisiveis = useMemo(() => {
    const dias = [];
    const hojeRef = new Date();
    const diaDaSemanaHoje = hojeRef.getDay();
    for (let i = 0; i < 7; i++) {
      const dia = new Date(hojeRef);
      dia.setDate(hojeRef.getDate() + (i - diaDaSemanaHoje));
      dias.push(dia);
    }
    return dias;
  }, []);

  const handleAddTarefa = async () => {
    if (!tituloTarefa || !horarioTarefa) {
      alert('Preencha título e horário.');
      return;
    }

    const dataFormatada = formatDateLocal(diaSelecionado);
    const corAleatoria = ['#28a745', '#ffc107', '#007bff', '#dc3545'][Math.floor(Math.random() * 4)];

    try {
      // NOTE: enviamos metaId (campo compatível com o backend)
      await axios.post(`${API_URL}/tarefas`, {
        titulo: tituloTarefa,
        horario: horarioTarefa,
        data: dataFormatada,
        cor: corAleatoria,
        metaId: metaSelecionada
      });

      await carregarDados(); // Recarrega para mostrar a tarefa nova
      setTituloTarefa('');
      setHorarioTarefa('');
      setMetaSelecionada(undefined);
      setModalVisivel(false);
    } catch (error) {
      console.error('Erro ao salvar tarefa:', error);
      alert('Erro ao salvar tarefa.');
    }
  };

  const handleMarcarCompleta = async (tarefa: Tarefa) => {
    try {
      const novasTarefas = tarefas.map(t => t.id === tarefa.id ? { ...t, completa: !t.completa } : t);
      setTarefas(novasTarefas);

      await axios.put(`${API_URL}/tarefas/${tarefa.id}`, {
        completa: !tarefa.completa
      });
    } catch (error) {
      console.error('Erro ao atualizar tarefa:', error);
      carregarDados();
    }
  };

  const getTituloMeta = (metaId?: number) => {
    const meta = metas.find(m => m.id === metaId);
    return meta ? meta.titulo : 'Sem meta';
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>Vamos focar hoje?</Text>
          <View style={styles.headerButtons}>
            <TouchableOpacity style={styles.actionButton} onPress={() => router.push('/calendario')}>
              <Text style={styles.addButtonText}>📅</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton} onPress={() => setModalVisivel(true)}>
              <Text style={styles.addButtonText}>+</Text>
            </TouchableOpacity>
          </View>
        </View>

        <View style={styles.daySelector}>
          {diasDaSemanaVisiveis.map((dia, index) => {
            const isSelected = dia.toDateString() === diaSelecionado.toDateString();
            return (
              <TouchableOpacity
                key={index}
                style={[styles.day, isSelected && styles.daySelected]}
                onPress={() => setDiaSelecionado(dia)}
              >
                <Text style={[styles.dayText, isSelected && styles.dayTextSelected]}>
                  {DIAS_SEMANA_ABREV[dia.getDay()]}
                </Text>
                <Text style={[styles.dateText, isSelected && styles.dayTextSelected]}>
                  {dia.getDate()}
                </Text>
              </TouchableOpacity>
            );
          })}
        </View>

        <View style={styles.taskList}>
          {tarefasFiltradas.length > 0 ? tarefasFiltradas.map(tarefa => (
            <View key={tarefa.id} style={[styles.taskCard, tarefa.completa && styles.taskCardCompleted]}>
              <View style={[styles.taskColorBar, { backgroundColor: tarefa.cor }]} />
              <View style={styles.taskInfo}>
                <Text style={styles.taskTitle}>{tarefa.titulo}</Text>
                <Text style={styles.taskTime}>{tarefa.horario}</Text>
                <Text style={styles.metaLabel}>Meta: {getTituloMeta(tarefa.metaId)}</Text>
              </View>
              <TouchableOpacity onPress={() => handleMarcarCompleta(tarefa)}>
                <View style={[styles.taskCompleteButton, { borderColor: tarefa.cor }]}>
                  {tarefa.completa && <View style={[styles.taskCompleteButtonInner, { backgroundColor: tarefa.cor }]} />}
                </View>
              </TouchableOpacity>
            </View>
          )) : (
            <Text style={styles.noTasksText}>Nenhuma tarefa para este dia.</Text>
          )}
        </View>

        <View style={styles.tipCard}>
          <Text style={styles.tipTitle}>Dica do dia</Text>
          <Text style={styles.tipText}>{dicaDoDia}</Text>
        </View>
      </ScrollView>

      <Modal animationType="slide" transparent visible={modalVisivel} onRequestClose={() => setModalVisivel(false)}>
        <View style={styles.modalContainer}>
          <View style={styles.modalView}>
            <Text style={styles.modalTitle}>Nova Tarefa</Text>
            <TextInput
              placeholder="Título da tarefa"
              placeholderTextColor="#555"
              style={styles.input}
              value={tituloTarefa}
              onChangeText={setTituloTarefa}
            />
            <TextInput
              placeholder="Horário (ex: 10:00 - 11:00)"
              placeholderTextColor="#555"
              style={styles.input}
              value={horarioTarefa}
              onChangeText={setHorarioTarefa}
            />

            <Text style={styles.metaSelectLabel}>Associar a uma meta:</Text>
            <View style={styles.pickerContainer}>
                <Picker
                  selectedValue={metaSelecionada}
                  onValueChange={(value) => setMetaSelecionada(value)}
                  style={styles.picker}
                >
                  <Picker.Item label="Nenhuma" value={undefined} />
                  {metas.map(meta => (
                    <Picker.Item key={meta.id} label={meta.titulo} value={meta.id} />
                  ))}
                </Picker>
            </View>

            <View style={styles.modalButtons}>
              <Button title="Cancelar" onPress={() => setModalVisivel(false)} color="gray" />
              <Button title="Salvar" onPress={handleAddTarefa} />
            </View>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#F4F7FE' },
  container: { flex: 1 },
  header: { backgroundColor: '#007BFF', paddingHorizontal: 20, paddingTop: 50, paddingBottom: 20, borderBottomLeftRadius: 20, borderBottomRightRadius: 20, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  headerTitle: { fontSize: 26, fontWeight: 'bold', color: 'white' },
  headerButtons: { flexDirection: 'row' },
  actionButton: { backgroundColor: 'rgba(255, 255, 255, 0.3)', width: 44, height: 44, borderRadius: 22, justifyContent: 'center', alignItems: 'center', marginLeft: 10 },
  addButtonText: { color: 'white', fontSize: 24 },
  daySelector: { flexDirection: 'row', justifyContent: 'space-around', paddingVertical: 15, marginHorizontal: 5, borderBottomWidth: 1, borderBottomColor: '#E0E0E0' },
  day: { width: 44, height: 44, borderRadius: 22, justifyContent: 'center', alignItems: 'center' },
  daySelected: { backgroundColor: '#007BFF' },
  dayText: { fontWeight: 'bold', fontSize: 16, color: '#333' },
  dateText: { fontSize: 12, color: 'gray' },
  dayTextSelected: { color: 'white' },
  taskList: { padding: 20, minHeight: 100 },
  taskCard: { flexDirection: 'row', alignItems: 'center', backgroundColor: 'white', borderRadius: 15, padding: 15, marginBottom: 10, elevation: 2 },
  taskCardCompleted: { opacity: 0.6, backgroundColor: '#f0f0f0' },
  taskColorBar: { width: 6, height: '100%', borderRadius: 3, marginRight: 15 },
  taskInfo: { flex: 1 },
  taskTitle: { fontSize: 16, fontWeight: 'bold', color: '#333' },
  taskTime: { fontSize: 14, color: 'gray', marginTop: 3 },
  metaLabel: { fontSize: 12, color: '#007BFF', marginTop: 4, fontStyle: 'italic' },
  taskCompleteButton: { width: 24, height: 24, borderRadius: 12, borderWidth: 2, justifyContent: 'center', alignItems: 'center', marginLeft: 10 },
  taskCompleteButtonInner: { width: 14, height: 14, borderRadius: 7 },
  noTasksText: { textAlign: 'center', marginTop: 30, color: 'gray', fontSize: 16 },
  tipCard: { marginHorizontal: 20, marginBottom: 20, backgroundColor: '#E9F5FF', borderRadius: 15, padding: 20, borderWidth: 1, borderColor: '#BDE0FF' },
  tipTitle: { fontWeight: 'bold', fontSize: 16, marginBottom: 5, color: '#0056B3' },
  tipText: { color: '#003D7A', lineHeight: 20 },
  modalContainer: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: 'rgba(0,0,0,0.5)' },
  modalView: { backgroundColor: 'white', borderRadius: 20, padding: 25, width: '90%', elevation: 5 },
  modalTitle: { fontSize: 22, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
  input: { borderWidth: 1, borderColor: '#ccc', backgroundColor: '#F8F8F8', paddingHorizontal: 15, paddingVertical: 12, borderRadius: 10, marginBottom: 15, fontSize: 16, color: '#333' },
  metaSelectLabel: { fontSize: 16, marginBottom: 8, color: '#333', marginTop: 5 },
  pickerContainer: { borderWidth: 1, borderColor: '#ccc', borderRadius: 10, marginBottom: 20, backgroundColor: '#F8F8F8' },
  picker: { width: '100%', height: 50 },
  modalButtons: { flexDirection: 'row', justifyContent: 'space-around', marginTop: 15 },
});
