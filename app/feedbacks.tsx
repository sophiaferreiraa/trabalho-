import { useLocalSearchParams, useRouter } from 'expo-router';
import React from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';

// Dados de feedback de acordo com o perfil
const PERFIS_FEEDBACK: Record<string, { titulo: string; mensagem: string; explicacao: string; dicas: string[] }> = {
  A: {
    titulo: 'Perfil Perfeccionista/Ansioso',
    mensagem: 'Respire fundo. A busca pela perfeição pode ser paralisante, mas lembre-se que "feito é melhor que perfeito".',
    explicacao: 'Você tende a adiar tarefas por um medo intenso de não atingir um padrão de qualidade extremamente alto.',
    dicas: [
      'Quebre grandes tarefas em passos minúsculos e comemore cada um.',
      'Estabeleça um tempo limite para cada tarefa. Quando o tempo acabar, a tarefa acabou.',
      'Pratique a auto-compaixão. Errar faz parte do aprendizado.',
      'Peça feedback de pessoas de confiança durante o processo, não apenas no final.'
    ]
  },
  B: {
    titulo: 'Perfil Procrastinador Crônico',
    mensagem: 'Tudo bem se sentir sobrecarregado. O importante é dar um pequeno passo de cada vez para quebrar o ciclo.',
    explicacao: 'Sua procrastinação parece ser um hábito profundamente enraizado.',
    dicas: [
      'Use a "Regra dos 2 Minutos": se uma tarefa leva menos de 2 minutos, faça-a imediatamente.',
      'Defina 3 prioridades para o seu dia e foque apenas nelas.',
      'Crie um ambiente de trabalho livre de distrações.',
      'Use calendários e aplicativos de lista de tarefas para visualizar seus compromissos.'
    ]
  },
  C: {
    titulo: 'Perfil Medo/Insegurança',
    mensagem: 'Sua capacidade é maior do que suas dúvidas. Confie no seu potencial e permita-se tentar, sem medo do julgamento.',
    explicacao: 'A procrastinação aqui é um mecanismo de defesa.',
    dicas: [
      'Reenquadre o "fracasso" como uma oportunidade de aprendizado.',
      'Converse sobre seus medos com um amigo, mentor ou terapeuta.',
      'Comece com as partes da tarefa em que você se sente mais confiante.',
      'Lembre-se de sucessos passados para reforçar sua autoconfiança.'
    ]
  },
  D: {
    titulo: 'Perfil Rebeldia/Desmotivação',
    mensagem: 'É normal resistir a obrigações. Tente encontrar um significado pessoal no que você precisa fazer.',
    explicacao: 'Você adia tarefas porque as vê como imposições ou por puro tédio.',
    dicas: [
      'Conecte a tarefa a um objetivo maior que seja importante para você.',
      'Transforme a tarefa em um jogo ou desafio pessoal.',
      'Recompense a si mesmo após concluir uma tarefa que você considera chata.',
      'Negocie prazos e métodos sempre que possível para aumentar seu senso de controle.'
    ]
  },
  E: {
    titulo: 'Perfil Dispersão/Desatenção',
    mensagem: 'Ter muitas ideias é um dom! O desafio é canalizar essa energia criativa para a ação.',
    explicacao: 'Sua mente é um turbilhão de ideias e possibilidades, o que torna difícil focar em uma única tarefa prática.',
    dicas: [
      'Use a técnica do "Brain Dump": escreva todas as suas ideias em um papel para limpar a mente.',
      'Escolha UMA ideia ou tarefa e se comprometa a trabalhar nela por apenas 15 minutos.',
      'Use ferramentas visuais, como mapas mentais, para organizar seus pensamentos.',
      'Pratique mindfulness para treinar seu foco e atenção no presente.'
    ]
  },
};

export default function FeedbacksScreen() {
  const router = useRouter();
  const params = useLocalSearchParams<{ perfilId?: string }>();
  const perfilId = params.perfilId;

  const feedback = perfilId ? PERFIS_FEEDBACK[perfilId] : null;

  if (!feedback) {
    return (
      <SafeAreaView style={styles.safeArea}>
        <View style={styles.container}>
          <Text style={styles.titulo}>Não foi possível carregar as dicas</Text>
          <Text style={styles.texto}>Refazer o questionário pode resolver.</Text>
          <TouchableOpacity style={styles.botaoConcluir} onPress={() => router.replace('/questionario')}>
            <Text style={styles.textoBotao}>Refazer Questionário</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.titulo}>{feedback.titulo}</Text>
        <Text style={styles.mensagem}>{feedback.mensagem}</Text>

        <View style={styles.card}>
          <Text style={styles.cardTitulo}>Como isso funciona?</Text>
          <Text style={styles.texto}>{feedback.explicacao}</Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.cardTitulo}>Dicas para você</Text>
          {feedback.dicas.map((dica, index) => (
            <Text key={index} style={styles.dicaItem}>• {dica}</Text>
          ))}
        </View>

        {/* Aviso importante */}
        <Text style={styles.aviso}>
          Este questionário não substitui o acompanhamento com um profissional de saúde mental.
        </Text>

        <TouchableOpacity style={styles.botaoConcluir} onPress={() => router.replace('/agenda')}>
          <Text style={styles.textoBotao}>Ir para a Agenda</Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#F4F7FE' },
  container: { padding: 20, alignItems: 'center' },
  titulo: { fontSize: 26, fontWeight: 'bold', textAlign: 'center', marginBottom: 10, color: '#333' },
  mensagem: { fontSize: 16, textAlign: 'center', color: '#666', marginBottom: 30, fontStyle: 'italic' },
  card: {
    backgroundColor: 'white',
    borderRadius: 15,
    padding: 20,
    width: '100%',
    marginBottom: 20,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  cardTitulo: { fontSize: 18, fontWeight: 'bold', marginBottom: 10, color: '#007BFF' },
  texto: { fontSize: 16, lineHeight: 24, color: '#444' },
  dicaItem: { fontSize: 16, lineHeight: 24, color: '#444', marginBottom: 5 },
  aviso: {
    fontSize: 12,
    textAlign: 'center',
    color: '#888',
    marginBottom: 20,
    marginTop: 10,
    fontStyle: 'italic',
  },
  botaoConcluir: {
    backgroundColor: '#28a745',
    borderRadius: 10,
    paddingVertical: 15,
    paddingHorizontal: 30,
    marginTop: 10,
  },
  textoBotao: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
});