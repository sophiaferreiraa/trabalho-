import { useLocalSearchParams, useRouter } from 'expo-router';
import React from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { useTutorial } from './contexts/TutorialContext';

// Definição dos perfis baseados em Joseph Ferrari (2010)
const PERFIS_FEEDBACK: Record<string, any> = {
  AROUSAL: {
    icone: '⚡',
    titulo: 'Perfil Buscador de Emoção (Arousal)',
    mensagem: 'Você tende a adiar tarefas para sentir a adrenalina do prazo curto.',
    explicacao: 'Você acredita que trabalha melhor sob pressão. A procrastinação aqui não é por preguiça, mas uma busca inconsciente pela excitação e o desafio de fazer tudo na última hora.',
    dicas: [
      'Use a Técnica Pomodoro Adaptada: Divida tarefas em blocos de 25-50 min com pequenas recompensas ao final.',
      'Crie "Picos de Desafio": Estabeleça mini-prazos falsos antes do prazo real para gerar essa adrenalina de forma controlada.',
      'Estratégia "Just Start": Treine iniciar uma tarefa prometendo a si mesmo trabalhar nela por apenas 10 minutos.'
    ],
    leitura: 'Referência: Fiore / Burka & Yuen; Steel (motivation/temporal).'
  },
  AVOIDANT: {
    icone: '🛡️',
    titulo: 'Perfil Evitador (Avoidant)',
    mensagem: 'Você adia tarefas por medo de falhar, ansiedade ou perfeccionismo.',
    explicacao: 'A procrastinação funciona como um escudo para sua autoestima. O medo de que o resultado não seja perfeito ou o medo do julgamento alheio faz com que você evite começar.',
    dicas: [
      'Micro-compromissos: Foque na tolerância ao desconforto começando com partes minúsculas da tarefa.',
      'Reestruture o Perfeccionismo: Troque a meta de "fazer perfeito" por "fazer o possível hoje". Feito é melhor que perfeito.',
      'Pratique a Autocompaixão: Se perdoe por deslizes passados. A culpa só gera mais ansiedade e mais procrastinação.'
    ],
    leitura: 'Referência: Ferrari (2010); Pychyl; Sirois (regulação emocional).'
  },
  DECISIONAL: {
    icone: '⚖️',
    titulo: 'Perfil Indeciso (Decisional)',
    mensagem: 'Você adia a ação porque tem dificuldade em tomar decisões.',
    explicacao: 'Você fica preso tentando encontrar a "melhor" opção, abordagem ou momento. Essa paralisia por análise faz com que você não faça nada por medo de escolher o caminho errado.',
    dicas: [
      'Decisão Mínima Viável: Defina um critério simples para escolher e avance. Você pode ajustar a rota depois.',
      'Matriz Rápida: Use uma lista de Prós/Contras com um tempo limite rígido (ex: 10 minutos) para decidir.',
      'Treine a Autoeficácia: Tome pequenas decisões rápidas no dia a dia para ganhar confiança na sua capacidade de escolha.'
    ],
    leitura: 'Referência: Ferrari et al. (1995), Mann et al. (decisional).'
  },
  MISTO: {
    icone: '🧩',
    titulo: 'Perfil Misto / Complexo',
    mensagem: 'Você apresenta características fortes de mais de um tipo de procrastinação.',
    explicacao: 'Seus resultados foram muito equilibrados entre duas ou mais categorias (Arousal, Evitação ou Indecisão). Isso significa que seus motivos variam dependendo da situação ou do tipo de tarefa.',
    dicas: [
      'Combine estratégias: Use prazos curtos (Arousal) com metas de "feito é melhor que perfeito" (Evitação).',
      'Organização é chave: Use listas de tarefas simples para evitar a paralisia da decisão.',
      'Automonitoramento: Tente perceber, a cada tarefa, se você está adiando por busca de emoção ou por medo.'
    ],
    leitura: 'Referência: Joseph Ferrari — “Still Procrastinating?” (2010)'
  }
};

export default function FeedbacksScreen() {
  const router = useRouter();
  const { perfilId } = useLocalSearchParams<{ perfilId?: string }>();
  const { iniciarTutorial } = useTutorial();

  // Garante que o perfilId existe no dicionário, senão usa MISTO
  const feedback = perfilId && PERFIS_FEEDBACK[perfilId] ? PERFIS_FEEDBACK[perfilId] : PERFIS_FEEDBACK['MISTO'];

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.icone}>{feedback.icone}</Text>
        <Text style={styles.titulo}>{feedback.titulo}</Text>
        <Text style={styles.mensagem}>{feedback.mensagem}</Text>

        <View style={styles.card}>
          <Text style={styles.cardTitulo}>Análise do Comportamento</Text>
          <Text style={styles.texto}>{feedback.explicacao}</Text>
        </View>
        
        <View style={styles.card}>
          <Text style={styles.cardTitulo}>Dicas Práticas</Text>
          {feedback.dicas.map((dica: string, index: number) => (
            <View key={index} style={styles.dicaBox}>
              <Text style={styles.dicaItem}>• {dica}</Text>
            </View>
          ))}
        </View>

        <Text style={styles.leitura}>{feedback.leitura}</Text>

        {/* --- AVISO DE ISENÇÃO DE RESPONSABILIDADE --- */}
        <View style={styles.avisoBox}>
          <Text style={styles.avisoTitulo}>⚠️ Importante</Text>
          <Text style={styles.avisoTexto}>
            Este questionário tem caráter educativo e <Text style={{fontWeight:'bold'}}>não substitui um diagnóstico profissional</Text>. 
            Os resultados baseiam-se em perfis comportamentais gerais. Para uma avaliação clínica detalhada ou tratamento de transtornos relacionados, procure um psicólogo ou especialista.
          </Text>
        </View>

        <TouchableOpacity style={styles.botaoConcluir} onPress={() => iniciarTutorial()}>
            <Text style={styles.textoBotao}>Prosseguir</Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#F4F7FE' },
  container: { padding: 20, alignItems: 'center', paddingBottom: 40 },
  icone: { fontSize: 60, marginBottom: 10 },
  titulo: { fontSize: 24, fontWeight: 'bold', textAlign: 'center', marginBottom: 10, color: '#007BFF' },
  mensagem: { fontSize: 16, textAlign: 'center', color: '#555', marginBottom: 20, fontStyle: 'italic', paddingHorizontal: 10 },
  card: { backgroundColor: 'white', borderRadius: 15, padding: 20, width: '100%', marginBottom: 15, elevation: 2, shadowColor: '#000', shadowOpacity: 0.1, shadowRadius: 5 },
  cardTitulo: { fontSize: 18, fontWeight: 'bold', marginBottom: 10, color: '#333' },
  texto: { fontSize: 15, lineHeight: 22, color: '#444' },
  dicaBox: { marginBottom: 8 },
  dicaItem: { fontSize: 15, lineHeight: 22, color: '#444' },
  leitura: { fontSize: 12, color: '#888', textAlign: 'center', marginBottom: 20 },
  
  // Estilos do Aviso
  avisoBox: { 
    width: '100%', backgroundColor: '#FFF3CD', 
    padding: 15, borderRadius: 10, marginBottom: 20, 
    borderWidth: 1, borderColor: '#FFEEBA' 
  },
  avisoTitulo: { fontWeight: 'bold', color: '#856404', marginBottom: 5, fontSize: 14 },
  avisoTexto: { color: '#856404', fontSize: 13, lineHeight: 18, textAlign: 'justify' },

  botaoConcluir: { backgroundColor: '#28a745', borderRadius: 10, paddingVertical: 15, paddingHorizontal: 30, width: '100%', alignItems: 'center' },
  textoBotao: { color: 'white', fontSize: 16, fontWeight: 'bold' },
});