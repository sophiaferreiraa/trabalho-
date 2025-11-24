import axios from 'axios';
import { Stack, useFocusEffect } from 'expo-router';
import React, { useCallback, useMemo, useState } from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from 'react-native';
import { Calendar, LocaleConfig } from 'react-native-calendars';

const API_URL = 'http://localhost:3001';

LocaleConfig.locales['pt-br'] = {
  monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
  monthNamesShort: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
  dayNames: ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'],
  dayNamesShort: ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'],
  today: 'Hoje'
};
LocaleConfig.defaultLocale = 'pt-br';

type Tarefa = { id: number; data: string; titulo: string; horario: string; cor: string; completa: boolean; };

const formatDateLocal = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export default function CalendarioScreen() {
    const [tarefas, setTarefas] = useState<Tarefa[]>([]);
    const [diaSelecionado, setDiaSelecionado] = useState(formatDateLocal(new Date()));

    const carregarTarefas = async () => {
        try {
            const response = await axios.get(`${API_URL}/tarefas`);
            setTarefas(response.data);
        } catch (e) {
            console.error("Erro ao buscar tarefas:", e);
        }
    };

    useFocusEffect(
        useCallback(() => {
            carregarTarefas();
        }, [])
    );

    const markedDates = useMemo(() => {
        const marks: { [key: string]: any } = {};
        tarefas.forEach(tarefa => {
            marks[tarefa.data] = { ...marks[tarefa.data], marked: true, dotColor: '#007BFF' };
        });
        marks[diaSelecionado] = { ...marks[diaSelecionado], selected: true, selectedColor: '#007BFF', selectedTextColor: 'white' };
        return marks;
    }, [tarefas, diaSelecionado]);

    const tarefasDoDia = useMemo(() => {
        return tarefas.filter(t => t.data === diaSelecionado);
    }, [tarefas, diaSelecionado]);

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
                    <Text style={styles.taskListTitle}>Tarefas para {diaSelecionado.split('-').reverse().join('/')}:</Text>
                    {tarefasDoDia.length > 0 ? (
                        tarefasDoDia.map(tarefa => (
                            <View key={tarefa.id} style={[styles.taskCard, tarefa.completa && styles.taskCardCompleted]}>
                                 <View style={[styles.taskColorBar, { backgroundColor: tarefa.cor }]} />
                                 <View style={styles.taskInfo}>
                                    <Text style={styles.taskTitle}>{tarefa.titulo}</Text>
                                    <Text style={styles.taskTime}>{tarefa.horario}</Text>
                                </View>
                            </View>
                        ))
                    ) : (
                        <Text style={styles.noTasksText}>Nenhuma tarefa para este dia.</Text>
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
