import AsyncStorage from '@react-native-async-storage/async-storage';
import { Stack } from 'expo-router';
import React, { useEffect, useMemo, useState } from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from 'react-native';
import { Calendar, LocaleConfig } from 'react-native-calendars';

// Configuração opcional para traduzir o calendário
LocaleConfig.locales['pt-br'] = {
  monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
  monthNamesShort: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
  dayNames: ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'],
  dayNamesShort: ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'],
  today: 'Hoje'
};
LocaleConfig.defaultLocale = 'pt-br';

// MUDANÇA: Use a mesma chave definida em agenda.tsx
const TAREFAS_STORAGE_KEY = '@TaskFlow:tarefas';

// Reutilize o tipo Tarefa (pode ser importado de um arquivo central no futuro)
type Tarefa = { id: string; data: string; titulo: string; horario: string; completa: boolean; metaId?: string };

export default function CalendarioScreen() {
    const [tarefas, setTarefas] = useState<Tarefa[]>([]);
    // Inicializa com a data atual formatada
    const [diaSelecionado, setDiaSelecionado] = useState(new Date().toISOString().split('T')[0]);

    // Carrega todas as tarefas salvas quando a tela abre
    useEffect(() => {
        const carregarTarefas = async () => {
             try {
                const tarefasSalvas = await AsyncStorage.getItem(TAREFAS_STORAGE_KEY);
                if (tarefasSalvas) {
                    setTarefas(JSON.parse(tarefasSalvas));
                }
            } catch (e) {
                console.error("Falha ao carregar tarefas no calendário", e);
                // Opcional: Mostrar um alerta para o usuário
            }
        };
        carregarTarefas();
    }, []); // Executa apenas uma vez

    // Cria as marcações para o calendário (pontos nos dias com tarefas e seleção)
    const markedDates = useMemo(() => {
        const marks: { [key: string]: any } = {};
        tarefas.forEach(tarefa => {
            // Adiciona a marcação de ponto se não existir ou mantém a existente
            marks[tarefa.data] = { ...marks[tarefa.data], marked: true, dotColor: '#007BFF' };
        });
        // Sobrescreve/adiciona a marcação para o dia atualmente selecionado
        marks[diaSelecionado] = { ...marks[diaSelecionado], selected: true, selectedColor: '#007BFF', // Cor de fundo do dia selecionado
                                   selectedTextColor: 'white' // Cor do texto do dia selecionado
                                 };
        return marks;
    }, [tarefas, diaSelecionado]);

    // Filtra as tarefas apenas para o dia selecionado
    const tarefasDoDia = useMemo(() => {
        return tarefas.filter(t => t.data === diaSelecionado);
    }, [tarefas, diaSelecionado]);

    return (
        <SafeAreaView style={styles.safeArea}>
            <Stack.Screen options={{ title: 'Calendário de Tarefas' }} />
            <ScrollView>
                <Calendar
                    current={diaSelecionado} // Garante que o calendário abra no mês do dia selecionado
                    onDayPress={(day) => setDiaSelecionado(day.dateString)}
                    markedDates={markedDates}
                    monthFormat={'MMMM yyyy'} // Formato do mês no cabeçalho
                    theme={{
                        backgroundColor: '#F4F7FE',
                        calendarBackground: '#F4F7FE',
                        textSectionTitleColor: '#b6c1cd',
                        selectedDayBackgroundColor: '#007BFF',
                        selectedDayTextColor: '#ffffff',
                        todayTextColor: '#007BFF',
                        dayTextColor: '#2d4150',
                        textDisabledColor: '#d9e1e8',
                        dotColor: '#007BFF',
                        selectedDotColor: '#ffffff',
                        arrowColor: '#007BFF',
                        disabledArrowColor: '#d9e1e8',
                        monthTextColor: '#007BFF',
                        indicatorColor: 'blue',
                        textDayFontWeight: '300',
                        textMonthFontWeight: 'bold',
                        textDayHeaderFontWeight: '300',
                        textDayFontSize: 16,
                        textMonthFontSize: 16,
                        textDayHeaderFontSize: 16
                    }}
                />

                <View style={styles.taskListContainer}>
                    {/* Formata a data para exibição (opcional) */}
                    <Text style={styles.taskListTitle}>Tarefas para {new Date(diaSelecionado + 'T00:00:00').toLocaleDateString('pt-BR')}:</Text>
                    {tarefasDoDia.length > 0 ? (
                        tarefasDoDia.map(tarefa => (
                            // Reutiliza o estilo do card de agenda.tsx
                            <View key={tarefa.id} style={[styles.taskCard, tarefa.completa && styles.taskCardCompleted]}>
                                 <View style={[styles.taskColorBar, { backgroundColor: tarefa.cor }]} />
                                 <View style={styles.taskInfo}>
                                    <Text style={styles.taskTitle}>{tarefa.titulo}</Text>
                                    <Text style={styles.taskTime}>{tarefa.horario}</Text>
                                    {/* Poderia adicionar a meta aqui também se quisesse carregar as metas */}
                                </View>
                                {/* Não precisa do botão de completar aqui, apenas visualização */}
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

// Estilos adaptados/reutilizados de agenda.tsx
const styles = StyleSheet.create({
    safeArea: { flex: 1, backgroundColor: '#F4F7FE' },
    taskListContainer: { padding: 20, marginTop: 10 },
    taskListTitle: { fontSize: 18, fontWeight: 'bold', color: '#333', marginBottom: 15 },
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
    taskCardCompleted: { opacity: 0.5, backgroundColor: '#e0e0e0' }, // Mantém o estilo para consistência visual
    taskColorBar: { width: 6, height: '100%', borderRadius: 3, marginRight: 15 },
    taskInfo: { flex: 1 },
    taskTitle: { fontSize: 16, fontWeight: 'bold', color: '#333' },
    taskTime: { fontSize: 14, color: 'gray', marginTop: 3 },
    noTasksText: { textAlign: 'center', color: 'gray', marginTop: 20, fontSize: 16 },
});