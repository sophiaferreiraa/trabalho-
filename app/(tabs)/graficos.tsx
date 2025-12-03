import axios from 'axios';
import { useFocusEffect } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { Dimensions, ScrollView, StyleSheet, Text, View, ActivityIndicator, Platform } from 'react-native';
import { StackedBarChart } from 'react-native-chart-kit';
import { useUser } from '../contexts/UserContext';
import { useTutorial } from '../contexts/TutorialContext';
import { TutorialOverlay } from '../contexts/TutorialOverlay';

const screenWidth = Dimensions.get('window').width;
const API_URL = 'http://192.168.3.8:3001';

export default function Progresso() {
  const { userId } = useUser();
  const { passoAtual, proximoPasso } = useTutorial();
  
  // Estado: Array de arrays.
  // NOVA ORDEM LÓGICA: [Baixa(Verde), Média(Amarelo), Alta(Vermelho)]
  // Isso garante que o Vermelho seja desenhado por último (no topo)
  const [dataGrafico, setDataGrafico] = useState<number[][]>([
    [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0]
  ]);
  
  const [metasConcluidas, setMetasConcluidas] = useState('0%');
  const [tarefasConcluidas, setTarefasConcluidas] = useState(0);
  const [loading, setLoading] = useState(false);

  // --- ALTERAÇÃO 1: Inversão dos índices ---
  // Index 0: Baixa (Verde) -> Base da pilha
  // Index 1: Média (Amarelo) -> Meio
  // Index 2: Alta (Vermelho) -> Topo da pilha
  const getCorIndex = (cor: string) => {
    if (cor === '#28a745') return 0; // Baixa
    if (cor === '#ffc107') return 1; // Média
    if (cor === '#dc3545') return 2; // Alta
    return 0; // Padrão (Verde)
  };

  const carregarDados = async () => {
    if (!userId) return;
    setLoading(true);
    try {
      const [resTarefas, resMetas] = await Promise.all([
        axios.get(`${API_URL}/tarefas/${userId}`),
        axios.get(`${API_URL}/metas/${userId}`)
      ]);

      const tarefas = resTarefas.data;
      const metas = resMetas.data;

      // Calcular Tarefas Concluídas
      const concluidas = tarefas.filter((t: any) => t.completa).length;
      setTarefasConcluidas(concluidas);

      // Calcular Metas
      if (metas.length > 0) {
        const somaProgresso = metas.reduce((acc: number, m: any) => acc + (m.progresso || 0), 0);
        const media = Math.round(somaProgresso / metas.length);
        setMetasConcluidas(`${media}%`);
      } else {
        setMetasConcluidas('0%');
      }

      // Processar Gráfico
      const diasContador = [
        [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0], [0,0,0]
      ]; 

      tarefas.forEach((t: any) => {
        if (t.completa) {
          const dataObj = new Date(t.data + 'T12:00:00');
          const diaSemana = dataObj.getDay(); // 0 = Dom ... 6 = Sab

          // Ajuste para ordem do gráfico: 0=Segunda ... 6=Domingo
          let indexGrafico = diaSemana - 1;
          if (indexGrafico === -1) indexGrafico = 6; 

          if (indexGrafico >= 0 && indexGrafico <= 6) {
            const indexCor = getCorIndex(t.cor);
            diasContador[indexGrafico][indexCor] += 1;
          }
        }
      });
      
      setDataGrafico(diasContador);

    } catch (error) {
      console.error("Erro gráficos:", error);
    } finally {
      setLoading(false);
    }
  };

  useFocusEffect(useCallback(() => { carregarDados(); }, [userId]));

  // --- ALTERAÇÃO 2: Ordem das Cores ---
  // Tem que bater com a ordem dos índices definida em getCorIndex
  // 0: Verde, 1: Amarelo, 2: Vermelho
  const data = {
    labels: ["Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"],
    legend: ["Baixa", "Média", "Alta"], 
    data: dataGrafico, 
    barColors: ["#28a745", "#ffc107", "#dc3545"] 
  };

  const chartConfig = {
    backgroundGradientFrom: "#FFFFFF",
    backgroundGradientTo: "#FFFFFF",
    decimalPlaces: 0,
    color: (opacity = 1) => `rgba(51, 51, 51, ${opacity})`,
    labelColor: (opacity = 1) => `#333`, // Cor mais escura para contraste
    propsForBackgroundLines: {
      strokeWidth: 1,
      stroke: "#E0E0E0",
      strokeDasharray: "0",
    },
    // --- ALTERAÇÃO 3: Fonte Gordinha (Bold) ---
    propsForLabels: {
      fontSize: 13,
      fontWeight: "bold", // Isso deixa a fonte "gordinha" como no resto do app
      fontFamily: Platform.OS === 'ios' ? 'System' : 'Roboto',
    },
    barPercentage: 0.6,
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 40 }}>
      <Text style={styles.title}>Seu progresso</Text>
      <Text style={styles.subtitle}>Visualize seu desempenho por prioridade.</Text>
      
      <View style={styles.mainCard}>
        <Text style={styles.mainCardTitle}>Média de Progresso das Metas</Text>
        <Text style={styles.mainCardValue}>{metasConcluidas}</Text>
      </View>

      <View style={styles.secondaryCards}>
        <View style={styles.card}>
          <Text style={styles.cardValue}>{tarefasConcluidas}</Text>
          <Text style={styles.cardLabel}>Tarefas concluídas</Text>
        </View>
        
        {/* Legenda Manual */}
        <View style={[styles.card, {backgroundColor: '#FFF', borderWidth: 1, borderColor: '#eee'}]}>
             <Text style={[styles.cardLabel, {fontSize: 12, fontWeight:'bold'}]}>Prioridades:</Text>
             <View style={{flexDirection:'row', alignItems:'center', marginTop:4}}>
                <View style={{width:10, height:10, borderRadius:5, backgroundColor:'#dc3545', marginRight:6}}/>
                <Text style={{fontSize:12, fontWeight:'bold', color:'#555'}}>Alta</Text>
             </View>
             <View style={{flexDirection:'row', alignItems:'center', marginTop:2}}>
                <View style={{width:10, height:10, borderRadius:5, backgroundColor:'#ffc107', marginRight:6}}/>
                <Text style={{fontSize:12, fontWeight:'bold', color:'#555'}}>Média</Text>
             </View>
             <View style={{flexDirection:'row', alignItems:'center', marginTop:2}}>
                <View style={{width:10, height:10, borderRadius:5, backgroundColor:'#28a745', marginRight:6}}/>
                <Text style={{fontSize:12, fontWeight:'bold', color:'#555'}}>Baixa</Text>
             </View>
        </View>
      </View>

      <Text style={styles.chartTitle}>Conclusão Semanal (por Cor)</Text>
      
      {loading ? (
        <ActivityIndicator size="large" color="#007BFF" style={{marginTop: 20}} />
      ) : (
        <StackedBarChart
          data={data}
          width={screenWidth - 32}
          height={250}
          chartConfig={chartConfig}
          style={styles.chart}
          hideLegend={true}
        />
      )}

      <TutorialOverlay 
        visible={passoAtual === 2}
        titulo="3. Seus Resultados"
        texto="Agora você pode ver quais prioridades de tarefas você mais conclui durante a semana!"
        botaoTexto="Ir para Perfil 👉"
        onProximo={proximoPasso}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#FFFFFF", padding: 16 },
  title: { fontSize: 26, fontWeight: '800', marginTop: 16, color: '#333' }, // Mais "gordinha"
  subtitle: { fontSize: 16, color: "#666", marginTop: 4, marginBottom: 10 },
  
  mainCard: { backgroundColor: "#007BFF", borderRadius: 16, padding: 20, marginTop: 10, elevation: 3 },
  mainCardTitle: { color: "rgba(255,255,255,0.9)", fontSize: 14, marginBottom: 5, fontWeight: '700' },
  mainCardValue: { color: "#FFFFFF", fontSize: 42, fontWeight: '900' }, // Fonte bem grossa
  
  secondaryCards: { flexDirection: 'row', marginTop: 16, justifyContent: 'space-between' },
  card: { backgroundColor: "#F4F7FE", borderRadius: 12, width: "48%", padding: 16, justifyContent: 'center' },
  cardValue: { fontSize: 28, fontWeight: '800', color: "#333" },
  cardLabel: { fontSize: 14, color: "#666", marginTop: 4, fontWeight: '600' },
  
  chartTitle: { fontSize: 18, fontWeight: '800', marginTop: 30, marginBottom: 15, color: '#333' },
  chart: { borderRadius: 16, marginVertical: 8, paddingRight: 0 },
});