import { useRouter } from 'expo-router';
import React, { useState } from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, TouchableOpacity, View, Alert, Dimensions } from 'react-native';

const DADOS_QUESTIONARIO = [
  // --- AROUSAL (Buscador de Emoção) ---
  { id: 1, subescala: 'AROUSAL', texto: 'Gosto da adrenalina de fazer algo na última hora.' },
  { id: 2, subescala: 'AROUSAL', texto: 'Sinto que trabalho melhor quando estou sob pressão de prazos curtos.' },
  { id: 3, subescala: 'AROUSAL', texto: 'Frequentemente espero o prazo estar próximo para sentir motivação.' },
  
  // --- AVOIDANT (Evitador) ---
  { id: 4, subescala: 'AVOIDANT', texto: 'Adio tarefas por medo de falhar ou de que o resultado não fique perfeito.' },
  { id: 5, subescala: 'AVOIDANT', texto: 'Prefiro fazer coisas fáceis e agradáveis a encarar tarefas que me deixam ansioso.' },
  { id: 6, subescala: 'AVOIDANT', texto: 'A preocupação excessiva com o resultado final me impede de começar.' },

  // --- DECISIONAL (Indeciso) ---
  { id: 7, subescala: 'DECISIONAL', texto: 'Fico preso tentando decidir por onde começar e acabo não fazendo nada.' },
  { id: 8, subescala: 'DECISIONAL', texto: 'Demoro muito para tomar decisões simples e isso atrasa meu trabalho.' },
  { id: 9, subescala: 'DECISIONAL', texto: 'Evito tomar decisões sobre um projeto por medo de escolher o caminho errado.' },
];

const { width } = Dimensions.get('window');

export default function QuestionarioScreen() {
  const router = useRouter();
  const [etapa, setEtapa] = useState<number>(0);
  const [respostas, setRespostas] = useState<Record<number, number>>({});

  const perguntaAtual = DADOS_QUESTIONARIO[etapa];
  const totalEtapas = DADOS_QUESTIONARIO.length;
  const progresso = ((etapa + 1) / totalEtapas) * 100;
  const notaSelecionada = respostas[perguntaAtual.id];

  const handleSelect = (nota: number) => {
    setRespostas({ ...respostas, [perguntaAtual.id]: nota });
  };

  const calcularResultado = () => {
    const scores = { AROUSAL: 0, AVOIDANT: 0, DECISIONAL: 0 };

    DADOS_QUESTIONARIO.forEach((item) => {
      const nota = respostas[item.id] || 0;
      scores[item.subescala as keyof typeof scores] += nota;
    });

    // Ordena do maior para o menor
    const sortedScores = Object.entries(scores).sort((a, b) => b[1] - a[1]);
    const [primeiro, segundo] = sortedScores;
    
    // Com menos perguntas (max 15 pts), uma diferença de 3 pontos já é significativa.
    if ((primeiro[1] - segundo[1]) < 7) {
      return 'MISTO';
    }

    return primeiro[0]; 
  };

  const handleProximo = () => {
    if (!respostas[perguntaAtual.id]) {
      Alert.alert("Opção necessária", "Por favor, selecione uma nota de 1 a 5.");
      return;
    }

    if (etapa < totalEtapas - 1) {
      setEtapa(etapa + 1);
    } else {
      const perfilFinal = calcularResultado();
      router.replace({
        pathname: '/feedbacks',
        params: { perfilId: perfilFinal },
      });
    }
  };

  const handleAnterior = () => {
    if (etapa > 0) setEtapa(etapa - 1);
    else router.back();
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.container}>
        
        {/* Cabeçalho */}
        <View style={styles.header}>
          <TouchableOpacity onPress={handleAnterior} style={styles.btnVoltar}>
            <Text style={styles.setaVoltar}>←</Text>
          </TouchableOpacity>
          <View style={styles.barraContainer}>
            <View style={[styles.barraProgresso, { width: `${progresso}%` }]} />
          </View>
          <Text style={styles.textoPasso}>{etapa + 1}/{totalEtapas}</Text>
        </View>

        <ScrollView contentContainerStyle={styles.scrollContent}>
          <Text style={styles.labelInstrucao}>O quanto você concorda?</Text>
          
          <View style={styles.cardPergunta}>
            <Text style={styles.textoPergunta}>{perguntaAtual.texto}</Text>
          </View>

          {/* Escala Visual Melhorada */}
          <View style={styles.escalaContainer}>
            <View style={styles.labelsExtremos}>
              <Text style={styles.textoExtremo}>Discordo</Text>
              <Text style={styles.textoExtremo}>Concordo</Text>
            </View>

            <View style={styles.linhaSelecao}>
              {/* Linha de fundo conectando os botões */}
              <View style={styles.linhaConectora} />

              <View style={styles.botoesRow}>
                {[1, 2, 3, 4, 5].map((num) => {
                  const isSelected = notaSelecionada === num;
                  return (
                    <TouchableOpacity
                      key={num}
                      activeOpacity={0.8}
                      style={[
                        styles.bolinha,
                        isSelected && styles.bolinhaSelecionada
                      ]}
                      onPress={() => handleSelect(num)}
                    >
                      <Text style={[
                        styles.numeroBolinha,
                        isSelected && styles.numeroSelecionado
                      ]}>{num}</Text>
                    </TouchableOpacity>
                  );
                })}
              </View>
            </View>
          </View>

        </ScrollView>

        <TouchableOpacity style={styles.btnContinuar} onPress={handleProximo}>
          <Text style={styles.txtBtnContinuar}>
            {etapa === totalEtapas - 1 ? "Ver Resultado" : "Próxima"}
          </Text>
        </TouchableOpacity>

      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#F8F9FA' },
  container: { flex: 1, padding: 24 },
  
  header: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginBottom: 40 },
  btnVoltar: { padding: 5 },
  setaVoltar: { fontSize: 24, color: '#333', fontWeight: 'bold' },
  barraContainer: { flex: 1, height: 6, backgroundColor: '#E0E0E0', borderRadius: 3, marginHorizontal: 15 },
  barraProgresso: { height: '100%', backgroundColor: '#007BFF', borderRadius: 3 },
  textoPasso: { fontSize: 14, color: '#666', fontWeight: '600' },

  scrollContent: { flexGrow: 1, justifyContent: 'center' },
  
  labelInstrucao: { textAlign: 'center', color: '#888', textTransform: 'uppercase', fontSize: 12, letterSpacing: 1, marginBottom: 10 },
  cardPergunta: { minHeight: 120, justifyContent: 'center', marginBottom: 30 },
  textoPergunta: { fontSize: 24, fontWeight: 'bold', color: '#222', textAlign: 'center', lineHeight: 32 },

  escalaContainer: { marginTop: 10, alignItems: 'center' },
  labelsExtremos: { flexDirection: 'row', justifyContent: 'space-between', width: '100%', paddingHorizontal: 5, marginBottom: 10 },
  textoExtremo: { fontSize: 12, color: '#666', fontWeight: '500' },

  linhaSelecao: { width: '100%', height: 60, justifyContent: 'center' },
  linhaConectora: { position: 'absolute', height: 4, width: '90%', backgroundColor: '#E0E0E0', borderRadius: 2, alignSelf: 'center', top: 28 }, // Linha atrás dos botões
  
  botoesRow: { flexDirection: 'row', justifyContent: 'space-between', width: '100%' },
  bolinha: {
    width: 50, height: 50, borderRadius: 25,
    backgroundColor: '#FFF', borderWidth: 2, borderColor: '#DDD',
    justifyContent: 'center', alignItems: 'center',
    elevation: 3, shadowColor: '#000', shadowOpacity: 0.1, shadowRadius: 3, shadowOffset: {width:0, height:2}
  },
  bolinhaSelecionada: {
    backgroundColor: '#007BFF', borderColor: '#007BFF',
    transform: [{scale: 1.15}], // Efeito de aumento ao selecionar
    elevation: 6
  },
  numeroBolinha: { fontSize: 18, color: '#888', fontWeight: 'bold' },
  numeroSelecionado: { color: '#FFF' },

  btnContinuar: { backgroundColor: '#007BFF', paddingVertical: 18, borderRadius: 16, alignItems: 'center', shadowColor: '#007BFF', shadowOpacity: 0.3, shadowRadius: 8, shadowOffset: {width:0, height:4}, elevation: 5 },
  txtBtnContinuar: { color: '#FFF', fontSize: 18, fontWeight: 'bold' },
});