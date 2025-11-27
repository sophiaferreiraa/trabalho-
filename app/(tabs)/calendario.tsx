import axios from 'axios';
import { Stack, useFocusEffect } from 'expo-router';
import React, { useCallback, useMemo, useState } from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from 'react-native';
import { Calendar, LocaleConfig } from 'react-native-calendars';
import { useUser } from '../contexts/UserContext';

const API_URL = 'http://localhost:3001'; 

LocaleConfig.locales['pt-br'] = {
  monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
  monthNamesShort: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
  dayNames: ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'],
  dayNamesShort: ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'],
  today: 'Hoje'
};
LocaleConfig.defaultLocale = 'pt-br';

type Tarefa = { 
  id: number; 
  data: string; 
  titulo: string; 
  horario: string; 
  cor: string; 
  completa: boolean; 
};

type MarcoCalendario = {
  id: number;
  descricao: string;
  vencimento: string;
  cor: string; // Cor da meta pai
  meta_titulo: string;
};

// Pesos para ordenação da lista abaixo do calendário
const PESO_PRIORIDADE: Record<string, number> = {
  '#dc3545': 1, 
  '#ffc107': 2, 
  '#28a745': 3, 
};

const formatDateLocal = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export default function CalendarioScreen() {
    const { userId } = useUser();
    const [tarefas, setTarefas] = useState<Tarefa[]>([]);
    const [marcos, setMarcos] = useState<MarcoCalendario[]>([]);
    const [diaSelecionado, setDiaSelecionado] = useState(formatDateLocal(new Date()));

    const carregarDados = async () => {
        if (!userId) return;
        try {
            const [resTarefas, resMarcos] = await Promise.all([
                axios.get(`${API_URL}/tarefas/${userId}`),
                axios.get(`${API_URL}/calendario-marcos/${userId}`)
            ]);
            setTarefas(resTarefas.data);
            setMarcos(resMarcos.data);
        } catch (e) {
            console.error("Erro ao buscar dados do calendário:", e);
        }
    };

    useFocusEffect(
        useCallback(() => {
            carregarDados();
        }, [userId])
    );

    // --- LÓGICA DE MARCAÇÃO ATUALIZADA ---
    const markedDates = useMemo(() => {
        const marks: { [key: string]: any } = {};

        // 1. Configurar Tarefas (Bolinha Azul pequena em baixo)
        tarefas.forEach(tarefa => {
            marks[tarefa.data] = {
                ...marks[tarefa.data],
                marked: true,       // Ativa a bolinha pequena
                dotColor: '#007BFF' // Cor azul conforme solicitado na imagem
            };
        });

        // 2. Configurar Marcos (Bolinha Completa/Fundo Colorido)
        marcos.forEach(marco => {
            if(marco.vencimento) {
                marks[marco.vencimento] = {
                    ...marks[marco.vencimento],
                    selected: true,          // Isso cria o preenchimento completo (bolinha grande)
                    selectedColor: marco.cor, // Cor do marco vinda do banco
                    selectedTextColor: 'white'
                };
            }
        });

        // 3. Configurar o Dia Selecionado pelo Usuário (Clique)
        // Se o dia clicado NÃO for um marco (não tiver cor própria), pintamos de azul padrão
        // para o usuário saber onde clicou. Se for marco, mantemos a cor do marco.
        const currentMark = marks[diaSelecionado] || {};
        
        if (!currentMark.selected) {
            marks[diaSelecionado] = {
                ...currentMark,
                selected: true,
                selectedColor: '#007BFF', // Azul padrão de seleção
                selectedTextColor: 'white'
            };
        }

        return marks;
    }, [tarefas, marcos, diaSelecionado]);

    const itensDoDia = useMemo(() => {
        // Filtra Tarefas
        const tarefasDia = tarefas.filter(t => t.data === diaSelecionado);
        
        // Filtra Marcos
        const marcosDia = marcos.filter(m => m.vencimento === diaSelecionado);

        // Ordena tarefas
        const tarefasOrdenadas = tarefasDia.sort((a, b) => {
            const pesoA = PESO_PRIORIDADE[a.cor] || 4;
            const pesoB = PESO_PRIORIDADE[b.cor] || 4;
            if (pesoA !== pesoB) return pesoA - pesoB;
            return a.horario.localeCompare(b.horario);
        });

        return { tarefas: tarefasOrdenadas, marcos: marcosDia };

    }, [tarefas, marcos, diaSelecionado]);

    return (
        <SafeAreaView style={styles.safeArea}>
            <Stack.Screen options={{ title: 'Calendário de Tarefas' }} />
            <ScrollView>
                <Calendar
                    current={diaSelecionado}
                    onDayPress={(day) => setDiaSelecionado(day.dateString)}
                    markedDates={markedDates}
                    monthFormat={'MMMM yyyy'}
                    theme={{
                        backgroundColor: '#F4F7FE', calendarBackground: '#F4F7FE', textSectionTitleColor: '#b6c1cd',
                        selectedDayBackgroundColor: '#007BFF', selectedDayTextColor: '#ffffff', todayTextColor: '#007BFF',
                        dayTextColor: '#2d4150', textDisabledColor: '#d9e1e8', dotColor: '#007BFF', selectedDotColor: '#ffffff',
                        arrowColor: '#007BFF', monthTextColor: '#007BFF', indicatorColor: 'blue',
                    }}
                />

                <View style={styles.taskListContainer}>
                    <Text style={styles.taskListTitle}>
                        Para {diaSelecionado.split('-').reverse().join('/')}:
                    </Text>
                    
                    {/* Exibe Marcos Primeiro */}
                    {itensDoDia.marcos.length > 0 && itensDoDia.marcos.map(marco => (
                        <View key={`marco-${marco.id}`} style={[styles.taskCard, { borderLeftWidth: 5, borderLeftColor: marco.cor }]}>
                             <View style={styles.taskInfo}>
                                <Text style={styles.taskTitle}>🏆 Marco: {marco.descricao}</Text>
                                <Text style={styles.taskTime}>Meta: {marco.meta_titulo}</Text>
                            </View>
                        </View>
                    ))}

                    {/* Exibe Tarefas */}
                    {itensDoDia.tarefas.length > 0 ? (
                        itensDoDia.tarefas.map(tarefa => (
                            <View key={`tarefa-${tarefa.id}`} style={[styles.taskCard, tarefa.completa && styles.taskCardCompleted]}>
                                 <View style={[styles.taskColorBar, { backgroundColor: tarefa.cor }]} />
                                 <View style={styles.taskInfo}>
                                    <Text style={styles.taskTitle}>{tarefa.titulo}</Text>
                                    <Text style={styles.taskTime}>{tarefa.horario}</Text>
                                </View>
                            </View>
                        ))
                    ) : (
                        itensDoDia.marcos.length === 0 && <Text style={styles.noTasksText}>Nenhum item para este dia.</Text>
                    )}
                </View>
            </ScrollView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    safeArea: { flex: 1, backgroundColor: '#F4F7FE' },
    taskListContainer: { padding: 20, marginTop: 10 },
    taskListTitle: { fontSize: 18, fontWeight: 'bold', color: '#333', marginBottom: 15 },
    taskCard: { flexDirection: 'row', alignItems: 'center', backgroundColor: 'white', borderRadius: 15, padding: 15, marginBottom: 10, elevation: 2 },
    taskCardCompleted: { opacity: 0.5, backgroundColor: '#e0e0e0' },
    taskColorBar: { width: 6, height: '100%', borderRadius: 3, marginRight: 15 },
    taskInfo: { flex: 1 },
    taskTitle: { fontSize: 16, fontWeight: 'bold', color: '#333' },
    taskTime: { fontSize: 14, color: 'gray', marginTop: 3 },
    noTasksText: { textAlign: 'center', color: 'gray', marginTop: 20, fontSize: 16 },
});