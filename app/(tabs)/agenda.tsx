import AsyncStorage from '@react-native-async-storage/async-storage';
import { Picker } from '@react-native-picker/picker';
import { useFocusEffect } from '@react-navigation/native';
import { useRouter } from 'expo-router'; // <--- Importação adicionada
import React, { useCallback, useEffect, useMemo, useState } from 'react'; // <--- React importado
import { Button, Modal, SafeAreaView, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';

// --- Chaves de Armazenamento ---
const TAREFAS_STORAGE_KEY = '@TaskFlow:tarefas';
const METAS_STORAGE_KEY = '@TaskFlow:metas';

// --- Tipos ---
type Meta = {
  id: string;
  titulo: string;
};

type Tarefa = {
  id: string;
  data: string; // <-- MUDANÇA: 'dia: number' para 'data: string' (formato 'AAAA-MM-DD')
  titulo: string;
  horario: string;
  cor: string;
  completa: boolean;
  metaId?: string;
};

// --- Constantes ---
const DICAS_DA_SEMANA = [
  "Divida tarefas grandes em partes menores para evitar a procrastinação!",
  "Comece pela tarefa mais difícil primeiro para tirar o peso das costas.",
  "Técnica Pomodoro: trabalhe por 25 minutos e descanse 5. Repita.",
  "Defina prazos realistas para cada uma de suas tarefas.",
  "Celebre pequenas vitórias ao completar cada tarefa do seu dia.",
  "Organize seu espaço de trabalho antes de começar. Um ambiente limpo ajuda a focar.",
  "Lembre-se de fazer pausas. Descansar também é produtivo."
];

const DIAS_SEMANA_ABREV = ['D', 'S', 'T', 'Q', 'Q', 'S', 'S']; // Abreviações

// --- Componente Principal ---
export default function AgendaScreen() {
  const hojeObj = new Date();
  const dicaDoDia = DICAS_DA_SEMANA[hojeObj.getDay()];

  // --- Estados ---
  const [diaSelecionado, setDiaSelecionado] = useState(hojeObj); // <-- MUDANÇA: Armazena o objeto Date
  const [tarefas, setTarefas] = useState<Tarefa[]>([]);
  const [metas, setMetas] = useState<Meta[]>([]);
  const [modalVisivel, setModalVisivel] = useState(false);
  const [tituloTarefa, setTituloTarefa] = useState('');
  const [horarioTarefa, setHorarioTarefa] = useState('');
  const [metaSelecionada, setMetaSelecionada] = useState<string | undefined>();

  const router = useRouter(); // Hook para navegação

  // --- Funções de Persistência ---
  const carregarDados = async () => {
    try {
      const [tarefasSalvas, metasSalvas] = await Promise.all([
        AsyncStorage.getItem(TAREFAS_STORAGE_KEY),
        AsyncStorage.getItem(METAS_STORAGE_KEY)
      ]);
      if (tarefasSalvas) setTarefas(JSON.parse(tarefasSalvas));
      if (metasSalvas) setMetas(JSON.parse(metasSalvas));
    } catch (e) {
      console.error('Falha ao carregar dados.', e);
      // Considerar mostrar um alerta para o usuário
    }
  };

  const salvarTarefas = async (novasTarefas: Tarefa[]) => {
    try {
      await AsyncStorage.setItem(TAREFAS_STORAGE_KEY, JSON.stringify(novasTarefas));
    } catch (e) {
      console.error('Falha ao salvar tarefas.', e);
      // Considerar mostrar um alerta para o usuário
    }
  };

  // --- Efeitos ---
  // Carrega dados na montagem inicial
  useEffect(() => {
    carregarDados();
  }, []);

  // Recarrega Metas quando a tela ganha foco
  useFocusEffect(
    useCallback(() => {
      const carregarMetas = async () => {
        try {
          const metasSalvas = await AsyncStorage.getItem(METAS_STORAGE_KEY);
          if (metasSalvas) setMetas(JSON.parse(metasSalvas));
        } catch (e) {
          console.error("Falha ao recarregar metas", e);
        }
      };
      carregarMetas();
    }, [])
  );

  // --- Memos / Cálculos ---
  // Filtra tarefas com base na data completa do dia selecionado
  const tarefasFiltradas = useMemo(() => {
    const dataFormatada = diaSelecionado.toISOString().split('T')[0]; // 'AAAA-MM-DD'
    return tarefas.filter(t => t.data === dataFormatada);
  }, [tarefas, diaSelecionado]);

  // Gera os 7 dias da semana atual para o seletor
  const diasDaSemanaVisiveis = useMemo(() => {
    const dias = [];
    const hojeRef = new Date(); // Referência do dia atual
    const diaDaSemanaHoje = hojeRef.getDay();
    for (let i = 0; i < 7; i++) {
      const dia = new Date(hojeRef);
      dia.setDate(hojeRef.getDate() + (i - diaDaSemanaHoje));
      dias.push(dia);
    }
    return dias;
  }, []); // Executa apenas uma vez

  // --- Manipuladores de Eventos ---
  const handleAddTarefa = () => {
    if (!tituloTarefa || !horarioTarefa) {
      alert('Por favor, preencha o título e o horário.');
      return;
    }

    const dataFormatada = diaSelecionado.toISOString().split('T')[0]; // Formato 'AAAA-MM-DD'

    const novaTarefa: Tarefa = {
      id: Date.now().toString(),
      data: dataFormatada, // <-- Salva a data correta
      titulo: tituloTarefa,
      horario: horarioTarefa,
      cor: ['#28a745', '#ffc107', '#007bff', '#dc3545'][Math.floor(Math.random() * 4)],
      completa: false,
      metaId: metaSelecionada,
    };

    const novasTarefas = [...tarefas, novaTarefa];
    setTarefas(novasTarefas);
    salvarTarefas(novasTarefas); // Salva no AsyncStorage

    // Limpa o formulário e fecha o modal
    setTituloTarefa('');
    setHorarioTarefa('');
    setMetaSelecionada(undefined);
    setModalVisivel(false);
  };

  const handleMarcarCompleta = (id: string) => {
    const novasTarefas = tarefas.map(t => (t.id === id ? { ...t, completa: !t.completa } : t));
    setTarefas(novasTarefas);
    salvarTarefas(novasTarefas); // Salva a alteração
  };

  const getTituloMeta = (metaId?: string) => {
    const meta = metas.find(m => m.id === metaId);
    return meta ? meta.titulo : 'Sem meta associada';
  };

  // --- Renderização ---
  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView style={styles.container}>
        {/* Cabeçalho com Botão de Navegação para Calendário */}
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

        {/* Seletor de Dias da Semana Atual */}
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

        {/* Lista de Tarefas do Dia Selecionado */}
        <View style={styles.taskList}>
          {tarefasFiltradas.length > 0 ? tarefasFiltradas.map(tarefa => (
            <View key={tarefa.id} style={[styles.taskCard, tarefa.completa && styles.taskCardCompleted]}>
              <View style={[styles.taskColorBar, { backgroundColor: tarefa.cor }]} />
              <View style={styles.taskInfo}>
                <Text style={styles.taskTitle}>{tarefa.titulo}</Text>
                <Text style={styles.taskTime}>{tarefa.horario}</Text>
                <Text style={styles.metaLabel}>Meta: {getTituloMeta(tarefa.metaId)}</Text>
              </View>
              <TouchableOpacity onPress={() => handleMarcarCompleta(tarefa.id)}>
                <View style={[styles.taskCompleteButton, { borderColor: tarefa.cor }]}>
                  {tarefa.completa && <View style={[styles.taskCompleteButtonInner, { backgroundColor: tarefa.cor }]} />}
                </View>
              </TouchableOpacity>
            </View>
          )) : (
            <Text style={styles.noTasksText}>Nenhuma tarefa para este dia.</Text>
          )}
        </View>

        {/* Card de Dica do Dia */}
        <View style={styles.tipCard}>
          <Text style={styles.tipTitle}>Dica do dia</Text>
          <Text style={styles.tipText}>{dicaDoDia}</Text>
        </View>
      </ScrollView>

      {/* Modal para Adicionar Tarefa */}
      <Modal animationType="slide" transparent visible={modalVisivel} onRequestClose={() => setModalVisivel(false)}>
        <View style={styles.modalContainer}>
          <View style={styles.modalView}>
            <Text style={styles.modalTitle}>Nova Tarefa</Text>
            <TextInput
              placeholder="Título da tarefa"
              placeholderTextColor="#555"
              style={[styles.input, { color: '#000' }]}
              value={tituloTarefa}
              onChangeText={setTituloTarefa}
            />
            <TextInput
              placeholder="Horário (ex: 10:00 - 11:00)"
              placeholderTextColor="#555"
              style={[styles.input, { color: '#000' }]}
              value={horarioTarefa}
              onChangeText={setHorarioTarefa}
            />

            <Text style={styles.metaSelectLabel}>Associar a uma meta:</Text>
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

// --- Estilos ---
const styles = StyleSheet.create({
    safeArea: { flex: 1, backgroundColor: '#F4F7FE' },
    container: { flex: 1 },
    header: {
        backgroundColor: '#007BFF',
        paddingHorizontal: 20,
        paddingTop: 50, // Ajuste conforme necessário
        paddingBottom: 20,
        borderBottomLeftRadius: 20,
        borderBottomRightRadius: 20,
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    headerTitle: { fontSize: 26, fontWeight: 'bold', color: 'white' },
    headerButtons: {
        flexDirection: 'row',
    },
    actionButton: { // Botões no header (Calendário e Adicionar)
        backgroundColor: 'rgba(255, 255, 255, 0.3)',
        width: 44,
        height: 44,
        borderRadius: 22,
        justifyContent: 'center',
        alignItems: 'center',
        marginLeft: 10,
    },
    addButtonText: { color: 'white', fontSize: 24 }, // Ícone ou Texto '+'
    daySelector: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        paddingVertical: 15,
        marginHorizontal: 5,
        borderBottomWidth: 1, // Linha divisória sutil
        borderBottomColor: '#E0E0E0',
    },
    day: { // Círculo para Dia da Semana e Data
        width: 44,
        height: 44,
        borderRadius: 22,
        justifyContent: 'center',
        alignItems: 'center',
    },
    daySelected: { backgroundColor: '#007BFF' },
    dayText: { // Letra do dia da semana (D, S, T...)
        fontWeight: 'bold',
        fontSize: 16,
        color: '#333'
    },
    dateText: { // Número do dia (1, 2, 3...)
        fontSize: 12,
        color: 'gray'
    },
    dayTextSelected: { color: 'white' }, // Cor do texto quando selecionado
    taskList: { padding: 20, minHeight: 100 }, // Altura mínima para evitar colapso
    taskCard: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: 'white',
        borderRadius: 15,
        padding: 15,
        marginBottom: 10,
        elevation: 2,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 2,
    },
    taskCardCompleted: { opacity: 0.6, backgroundColor: '#f0f0f0' }, // Feedback visual para concluído
    taskColorBar: { width: 6, height: '100%', borderRadius: 3, marginRight: 15 },
    taskInfo: { flex: 1 },
    taskTitle: { fontSize: 16, fontWeight: 'bold', color: '#333' },
    taskTime: { fontSize: 14, color: 'gray', marginTop: 3 },
    metaLabel: { fontSize: 12, color: '#007BFF', marginTop: 4, fontStyle: 'italic' }, // Estilo para meta
    taskCompleteButton: {
        width: 24,
        height: 24,
        borderRadius: 12,
        borderWidth: 2,
        justifyContent: 'center',
        alignItems: 'center',
        marginLeft: 10,
    },
    taskCompleteButtonInner: {
        width: 14,
        height: 14,
        borderRadius: 7,
    },
    noTasksText: { textAlign: 'center', marginTop: 30, color: 'gray', fontSize: 16 }, // Mais margem
    tipCard: {
        marginHorizontal: 20,
        marginBottom: 20, // Apenas margem inferior
        backgroundColor: '#E9F5FF',
        borderRadius: 15,
        padding: 20,
        borderWidth: 1,
        borderColor: '#BDE0FF',
    },
    tipTitle: { fontWeight: 'bold', fontSize: 16, marginBottom: 5, color: '#0056B3' },
    tipText: { color: '#003D7A', lineHeight: 20 }, // Melhorar legibilidade
    modalContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: 'rgba(0,0,0,0.5)',
    },
    modalView: {
        backgroundColor: 'white',
        borderRadius: 20,
        padding: 25,
        width: '90%',
        elevation: 5,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.25,
        shadowRadius: 4,
    },
    modalTitle: { fontSize: 22, fontWeight: 'bold', marginBottom: 20, textAlign: 'center' },
    input: {
        borderWidth: 1,
        borderColor: '#ccc',
        backgroundColor: '#F8F8F8',
        paddingHorizontal: 15,
        paddingVertical: 12,
        borderRadius: 10,
        marginBottom: 15,
        fontSize: 16,
        color: '#333' // Garante que o texto digitado seja escuro
    },
    metaSelectLabel: { fontSize: 16, marginBottom: 8, color: '#333', marginTop: 5 }, // Espaço acima
    picker: { // Estilos para o Picker podem variar entre plataformas
        width: '100%',
        height: 50, // Tentar altura fixa
        marginBottom: 20,
        backgroundColor: '#F8F8F8',
        borderWidth: 1, // Nota: borderWidth pode não funcionar visualmente em todos os Pickers nativos
        borderColor: '#ccc',
        borderRadius: 10,
    },
    modalButtons: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        marginTop: 15,
    },
});