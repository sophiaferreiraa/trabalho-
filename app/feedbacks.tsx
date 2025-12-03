import { useLocalSearchParams, useRouter } from 'expo-router';
import React from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { useTutorial } from './contexts/TutorialContext';

// Definição dos perfis com foco em REFORÇO POSITIVO e DICAS FOCALIZADAS
const PERFIS_FEEDBACK: Record<string, any> = {
  AROUSAL: {
    icone: '⚡',
    // titulo e mensagem removidos conforme solicitado (parte azul)
    explicacao: 'Seu estilo é dinâmico. Você não está fugindo do trabalho, mas sim buscando o estímulo certo para agir. Vamos canalizar essa busca por adrenalina de forma estratégica para você brilhar sem o estresse de última hora.',
    dicas: [
      'Crie seus próprios desafios: Estabeleça "prazos falsos" curtos para gerar motivação antes da hora real.',
      'Gamificação: Tente bater seus próprios recordes de tempo em tarefas rotineiras para torná-las emocionantes.',
      'Comece com intensidade: Ataque a tarefa mais difícil do dia primeiro para aproveitar sua energia natural.'
    ],
    leitura: 'Referência: Estratégias de Motivação e Flow.'
  },
  AVOIDANT: {
    icone: '🛡️',
    // titulo e mensagem removidos conforme solicitado (parte azul)
    explicacao: 'Esse cuidado é uma grande virtude. Às vezes, o desejo de fazer tudo perfeito pode fazer o primeiro passo parecer difícil. Vamos transformar esse padrão em pequenos avanços seguros para você mostrar todo o seu potencial.',
    dicas: [
      'Feito é melhor que perfeito: Permita-se fazer um rascunho inicial imperfeito apenas para começar.',
      'Quebre a tarefa: Divida o trabalho em partes tão pequenas (micro-passos) que seja impossível falhar.',
      'Comemore o início: Valorize o ato de começar uma tarefa difícil, independentemente do resultado imediato.'
    ],
  },
  DECISIONAL: {
    icone: '⚖️',
    // titulo e mensagem removidos conforme solicitado (parte azul)
    explicacao: 'Pensar antes de agir é sábio. O desafio surge quando a busca pela "melhor" opção impede o movimento. Vamos focar em simplificar suas escolhas para que suas ótimas ideias saiam do papel mais rápido.',
    dicas: [
      'Regra dos 2 Minutos: Se a decisão leva menos que isso para ser executada, decida e faça agora.',
      'Limite suas opções: Reduza propositalmente as alternativas (ex: escolha entre apenas duas) para facilitar a ação.',
      'Defina prazos para decidir: Dê a si mesmo um tempo limite rígido apenas para a fase de planejamento.'
    ],
  },
  MISTO: {
    icone: '🧩',
    // titulo e mensagem removidos conforme solicitado (parte azul)
    explicacao: 'Isso mostra que você tem diferentes recursos internos. O segredo é identificar o que está te travando em cada momento específico: falta de estímulo, perfeccionismo ou dúvida sobre o caminho a seguir.',
    dicas: [
      'Identifique o gatilho: Antes de adiar, pergunte-se: "Estou entediado, com medo ou indeciso?"',
      'Combine estratégias: Use prazos curtos para gerar ânimo e divida tarefas grandes para reduzir o medo.',
      'Organização flexível: Tenha um plano, mas permita-se ajustá-lo sem culpa se a situação mudar.'
    ],

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
        
        {/* AQUI FORAM REMOVIDOS O TÍTULO E A MENSAGEM (PARTE AZUL) */}

        <View style={styles.card}>
          <Text style={styles.cardTitulo}>Como você funciona</Text>
          <Text style={styles.texto}>{feedback.explicacao}</Text>
        </View>
        
        <View style={styles.card}>
          <Text style={styles.cardTitulo}>Estratégias para você</Text>
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
            Este feedback foca em seus pontos fortes e estilo de trabalho. 
            Ele tem caráter educativo e <Text style={{fontWeight:'bold'}}>não substitui um diagnóstico profissional</Text>.
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
  // Estilos de título e mensagem removidos ou não utilizados
  card: { backgroundColor: 'white', borderRadius: 15, padding: 20, width: '100%', marginBottom: 15, elevation: 2, shadowColor: '#000', shadowOpacity: 0.1, shadowRadius: 5 },
  cardTitulo: { fontSize: 18, fontWeight: 'bold', marginBottom: 10, color: '#333' },
  texto: { fontSize: 15, lineHeight: 22, color: '#444' },
  dicaBox: { marginBottom: 10 },
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