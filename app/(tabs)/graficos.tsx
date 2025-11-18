import React from 'react';
import { Dimensions, ScrollView, StyleSheet, Text, View } from 'react-native';
import { BarChart } from 'react-native-chart-kit';

const screenWidth = Dimensions.get('window').width;

export default function Progresso() {
  const data = {
    labels: ["Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"],
    datasets: [
      {
        
        data: [2, 3, 4, 5, 6, 2, 3],
      }
    ]
  };

  const chartConfig = {
    backgroundGradientFrom: "#FFFFFF",
    backgroundGradientTo: "#FFFFFF",
    decimalPlaces: 0,
    color: (opacity = 1, index?: number) => {
      const cores = ["#1565C0","#1976D2","#1E88E5","#2196F3","#00BCD4","#42A5F5","#0D47A1"];
      return cores[index ?? 0];
    },
    labelColor: (opacity = 1) => `#555555`,
    propsForBackgroundLines: { stroke: "#E0E0E0" },
  };

  return (
    <ScrollView style={styles.container}>
      {/* Header */}
      <Text style={styles.title}>Seu progresso</Text>
      <Text style={styles.subtitle}>Acompanhe sua evolução semanal!</Text>

      {/* Card Principal */}
      <View style={styles.mainCard}>
        <Text style={styles.mainCardTitle}>Metas Concluídas</Text>
        <Text style={styles.mainCardValue}>56%</Text>
        <Text style={styles.mainCardSubtext}>+12% que semana passada</Text>
      </View>

      {/* Card de Tarefas Concluídas */}
      <View style={[styles.secondaryCards, { justifyContent: 'flex-start' }]}>
        <View style={styles.card}>
          <Text style={styles.cardValue}>25</Text>
          <Text style={styles.cardLabel}>Tarefas concluídas!</Text>
        </View>
      </View>

      {/* Gráfico de Barras */}
      <Text style={styles.chartTitle}>Evolução Semanal</Text>
      <BarChart
        data={data}
        width={screenWidth - 32}
        height={220}
        fromZero
        chartConfig={chartConfig}
        showValuesOnTopOfBars
        style={styles.chart}
        yAxisLabel=""
        yAxisSuffix=""
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    padding: 16,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginTop: 16,
  },
  subtitle: {
    fontSize: 16,
    color: "#555555",
    marginTop: 4,
  },
  mainCard: {
    backgroundColor: "#1E90FF",
    borderRadius: 16,
    padding: 16,
    marginTop: 24,
  },
  mainCardTitle: {
    color: "#FFFFFF",
    fontSize: 16,
    marginBottom: 4,
  },
  mainCardValue: {
    color: "#FFFFFF",
    fontSize: 36,
    fontWeight: 'bold',
  },
  mainCardSubtext: {
    color: "#FFFFFF",
    fontSize: 14,
    marginTop: 4,
  },
  secondaryCards: {
    flexDirection: 'row',
    marginTop: 16,
  },
  card: {
    backgroundColor: "#F5F5F5",
    borderRadius: 12,
    width: "48%",
    padding: 12,
    alignItems: 'center',
  },
  cardValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: "#1A237E",
  },
  cardLabel: {
    fontSize: 14,
    color: "#555555",
    marginTop: 4,
    textAlign: 'center',
  },
  chartTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 24,
    marginBottom: 8,
  },
  chart: {
    borderRadius: 16,
  },
});
