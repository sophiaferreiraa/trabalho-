import axios from 'axios';
import { useFocusEffect } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { Dimensions, ScrollView, StyleSheet, Text, View } from 'react-native';
import { BarChart } from 'react-native-chart-kit';
import { useUser } from '../contexts/UserContext';

const screenWidth = Dimensions.get('window').width;
const API_URL = 'http://localhost:3001';

export default function Progresso() {
  const { userId } = useUser();
  const [dataGrafico, setDataGrafico] = useState<number[]>([0, 0, 0, 0, 0, 0, 0]);
  const [metasConcluidas, setMetasConcluidas] = useState('0%');
  const [tarefasConcluidas, setTarefasConcluidas] = useState(0);

  const carregarDados = async () => {
    if (!userId) return;
    try {
      const [resTarefas, resMetas] = await Promise.all([
        axios.get(`${API_URL}/tarefas/${userId}`),
        axios.get(`${API_URL}/metas/${userId}`)
      ]);

      const tarefas = resTarefas.data;
      const metas = resMetas.data;

      const concluidas = tarefas.filter((t: any) => t.completa).length;
      setTarefasConcluidas(concluidas);

      if (metas.length > 0) {
        const somaProgresso = metas.reduce((acc: number, m: any) => acc + (m.progresso || 0), 0);
        const media = Math.round(somaProgresso / metas.length);
        setMetasConcluidas(`${media}%`);
      } else {
        setMetasConcluidas('0%');
      }

      const diasContador = [0, 0, 0, 0, 0, 0, 0]; 
      tarefas.forEach((t: any) => {
        if (t.completa) {
          const dataObj = new Date(t.data + 'T12:00:00');
          const diaSemana = dataObj.getDay(); 
          let indexGrafico = diaSemana - 1;
          if (indexGrafico === -1) indexGrafico = 6; 
          if (indexGrafico >= 0 && indexGrafico <= 6) diasContador[indexGrafico] += 1;
        }
      });
      setDataGrafico(diasContador);

    } catch (error) {
      console.error("Erro gráficos:", error);
    }
  };

  useFocusEffect(useCallback(() => { carregarDados(); }, [userId]));

  const data = {
    labels: ["Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"],
    datasets: [{ data: dataGrafico }]
  };

  const chartConfig = {
    backgroundGradientFrom: "#FFFFFF", backgroundGradientTo: "#FFFFFF", decimalPlaces: 0,
    color: (opacity = 1) => `rgba(21, 101, 192, ${opacity})`,
    labelColor: (opacity = 1) => `#555555`,
    propsForBackgroundLines: { stroke: "#E0E0E0" },
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Seu progresso</Text>
      <Text style={styles.subtitle}>Acompanhe sua evolução semanal!</Text>
      <View style={styles.mainCard}>
        <Text style={styles.mainCardTitle}>Média de Progresso das Metas</Text>
        <Text style={styles.mainCardValue}>{metasConcluidas}</Text>
      </View>
      <View style={[styles.secondaryCards, { justifyContent: 'flex-start' }]}>
        <View style={styles.card}>
          <Text style={styles.cardValue}>{tarefasConcluidas}</Text>
          <Text style={styles.cardLabel}>Tarefas concluídas!</Text>
        </View>
      </View>
      <Text style={styles.chartTitle}>Tarefas Concluídas (Semana)</Text>
      <BarChart data={data} width={screenWidth - 32} height={220} fromZero chartConfig={chartConfig} showValuesOnTopOfBars style={styles.chart} yAxisLabel="" yAxisSuffix="" />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#FFFFFF", padding: 16 }, title: { fontSize: 28, fontWeight: 'bold', marginTop: 16 }, subtitle: { fontSize: 16, color: "#555555", marginTop: 4 }, mainCard: { backgroundColor: "#1E90FF", borderRadius: 16, padding: 16, marginTop: 24 }, mainCardTitle: { color: "#FFFFFF", fontSize: 16, marginBottom: 4 }, mainCardValue: { color: "#FFFFFF", fontSize: 36, fontWeight: 'bold' }, secondaryCards: { flexDirection: 'row', marginTop: 16 }, card: { backgroundColor: "#F5F5F5", borderRadius: 12, width: "48%", padding: 12, alignItems: 'center' }, cardValue: { fontSize: 24, fontWeight: 'bold', color: "#1A237E" }, cardLabel: { fontSize: 14, color: "#555555", marginTop: 4, textAlign: 'center' }, chartTitle: { fontSize: 18, fontWeight: 'bold', marginTop: 24, marginBottom: 8 }, chart: { borderRadius: 16 },
});