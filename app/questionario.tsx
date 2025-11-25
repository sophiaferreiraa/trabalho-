import { useRouter } from 'expo-router';
import React, { useState } from 'react';
import { SafeAreaView, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';

const DADOS_QUESTIONARIO = [
  {
    id: 1,
    pergunta: 'Como a procrastinação afeta seu bem-estar?',
    tipo: 'multi',
    opcoes: [
      { texto: 'Sinto-me sobrecarregado(a) e/ou preocupado(a)', perfil: 'A', pontos: 1 },
      { texto: 'Não atinjo metas/objetivos', perfil: 'B', pontos: 1 },
      { texto: 'Minha autoestima é afetada', perfil: 'C', pontos: 1 },
      { texto: 'Tenho problemas de sono', perfil: 'A', pontos: 1 },
      { texto: 'Tenho tendência a perder prazos', perfil: 'B', pontos: 1 },
      { texto: 'Perco boas oportunidades', perfil: 'B', pontos: 1 },
    ],
  },
  {
    id: 2,
    pergunta: 'Qual é a maior razão para você adiar tarefas?',
    tipo: 'single',
    opcoes: [
      { texto: 'Espero o esforço de última hora', perfil: 'B', pontos: 2 },
      { texto: 'Assumo tarefas demais', perfil: 'E', pontos: 2 },
      { texto: 'Não quero seguir regras', perfil: 'D', pontos: 2 },
      { texto: 'Me preocupo com os riscos', perfil: 'C', pontos: 2 },
      { texto: 'Me perco em ideias', perfil: 'E', pontos: 2 },
      { texto: 'Preciso que tudo esteja perfeito', perfil: 'A', pontos: 2 },
    ],
  },
  {
    id: 3,
    pergunta: 'O que te motiva a concluir tarefas?',
    tipo: 'multi',
    opcoes: [
      { texto: 'Pressão externa', perfil: 'B', pontos: 1 },
      { texto: 'Medo das consequências', perfil: 'C', pontos: 1 },
      { texto: 'Objetivos pessoais', perfil: 'A', pontos: 1 },
      { texto: 'Apoio de outras pessoas', perfil: 'B', pontos: 1 },
      { texto: 'Recompensas ou incentivos', perfil: 'D', pontos: 1 },
    ],
  },
  {
    id: 4,
    pergunta: 'Há quanto tempo você sente essas dificuldades?',
    tipo: 'single',
    opcoes: [
      { texto: 'Nos últimos meses', perfil: 'A', pontos: 1 },
      { texto: 'Há alguns anos', perfil: 'B', pontos: 1 },
      { texto: 'Desde sempre', perfil: 'B', pontos: 1 },
      { texto: 'Não tenho certeza', perfil: 'E', pontos: 1 },
    ],
  },
  {
    id: 5,
    pergunta: 'Quais sintomas físicos você sente quando procrastina?',
    tipo: 'multi',
    opcoes: [
      { texto: 'Dores de cabeça', perfil: 'A', pontos: 1 },
      { texto: 'Falta de energia', perfil: 'B', pontos: 1 },
      { texto: 'Músculos tensos', perfil: 'C', pontos: 1 },
      { texto: 'Inquietação', perfil: 'B', pontos: 1 },
      { texto: 'Problemas de sono', perfil: 'A', pontos: 1 },
      { texto: 'Desconforto no estômago', perfil: 'C', pontos: 1 },
    ],
  },
  {
    id: 6,
    pergunta: 'O que você gostaria de melhorar em si mesmo?',
    tipo: 'multi',
    opcoes: [
      { texto: 'Minha falta de energia', perfil: 'B', pontos: 1 },
      { texto: 'Minha carga emocional', perfil: 'A', pontos: 1 },
      { texto: 'Minha exaustão emocional', perfil: 'A', pontos: 1 },
      { texto: 'Minha preocupação excessiva', perfil: 'C', pontos: 1 },
      { texto: 'O hábito de pensar demais', perfil: 'E', pontos: 1 },
      { texto: 'Minha irritabilidade', perfil: 'D', pontos: 1 },
    ],
  },
  {
    id: 7,
    pergunta: 'Quais atividades você costuma procrastinar?',
    tipo: 'multi',
    opcoes: [
      { texto: 'Exercícios físicos', perfil: 'B', pontos: 1 },
      { texto: 'Beber água adequadamente', perfil: 'B', pontos: 1 },
      { texto: 'Estudar ou aprender algo novo', perfil: 'E', pontos: 1 },
      { texto: 'Limpar e organizar a casa', perfil: 'D', pontos: 1 },
      { texto: 'Ler um livro', perfil: 'E', pontos: 1 },
      { texto: 'Interagir socialmente', perfil: 'C', pontos: 1 },
      { texto: 'Responder verificações/mensagens', perfil: 'B', pontos: 1 },
    ],
  },
  {
    id: 8,
    pergunta: 'Quais hábitos você gostaria de abandonar?',
    tipo: 'multi',
    opcoes: [
      { texto: 'Chegar sempre atrasado', perfil: 'B', pontos: 1 },
      { texto: 'Uso excessivo de redes sociais', perfil: 'E', pontos: 1 },
      { texto: 'Duvidar de mim mesmo', perfil: 'C', pontos: 1 },
      { texto: 'Consumir doces/comida processada', perfil: 'B', pontos: 1 },
      { texto: 'Perder horas de sono', perfil: 'A', pontos: 1 },
      { texto: 'Roer as unhas', perfil: 'C', pontos: 1 },
      { texto: 'Maratonar séries com frequência', perfil: 'E', pontos: 1 },
    ],
  },
  {
    id: 9,
    pergunta: 'Por que você realmente evita certas tarefas?',
    tipo: 'multi',
    opcoes: [
      { texto: 'Tenho medo de falhar', perfil: 'C', pontos: 1 },
      { texto: 'Acho que não são importantes', perfil: 'D', pontos: 1 },
      { texto: 'Não sei por onde começar', perfil: 'E', pontos: 1 },
      { texto: 'As tarefas me estressam', perfil: 'A', pontos: 1 },
      { texto: 'Sinceramente, não tenho certeza', perfil: 'B', pontos: 1 },
    ],
  },
];

type Scores = Record<string, number>;

export default function QuestionarioScreen() {
  const router = useRouter();
  const [etapa, setEtapa] = useState<number>(1);
  const [respostas, setRespostas] = useState<Record<number, string[]>>({});

  const perguntaAtual = DADOS_QUESTIONARIO[etapa - 1];

  const handleSelect = (opcaoTexto: string) => {
    const respostasAtuais = respostas[etapa] || [];
    if (perguntaAtual.tipo === 'single') {
      setRespostas({ ...respostas, [etapa]: [opcaoTexto] });
    } else {
      if (respostasAtuais.includes(opcaoTexto)) {
        setRespostas({
          ...respostas,
          [etapa]: respostasAtuais.filter((item) => item !== opcaoTexto),
        });
      } else {
        setRespostas({
          ...respostas,
          [etapa]: [...respostasAtuais, opcaoTexto],
        });
      }
    }
  };

  const calcularResultado = () => {
    const scores: Scores = { A: 0, B: 0, C: 0, D: 0, E: 0 };

    Object.entries(respostas).forEach(([numEtapa, respostasUsuario]) => {
      const etapaAtual = parseInt(numEtapa) - 1;
      const perguntaInfo = DADOS_QUESTIONARIO[etapaAtual];

      respostasUsuario.forEach(respostaTexto => {
        const opcaoInfo = perguntaInfo.opcoes.find(opt => opt.texto === respostaTexto);
        if (opcaoInfo) {
          scores[opcaoInfo.perfil] += opcaoInfo.pontos;
        }
      });
    });

    const perfilDominante = Object.keys(scores).reduce((a, b) =>
      scores[a] >= scores[b] ? a : b 
    );
    
    return perfilDominante;
  };

  const handleProximo = () => {
    if (etapa < DADOS_QUESTIONARIO.length) {
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
    if (etapa > 1) {
      setEtapa(etapa - 1);
    } else {
      router.back();
    }
  };
  
  const progresso = (etapa / DADOS_QUESTIONARIO.length) * 100;

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.container}>
        <View style={styles.header}>
          <TouchableOpacity onPress={handleAnterior}>
            <Text style={styles.backButton}>←</Text>
          </TouchableOpacity>
          <Text style={styles.progressoTexto}>{etapa} de {DADOS_QUESTIONARIO.length}</Text>
        </View>

        <View style={styles.barraProgressoContainer}>
          <View style={[styles.barraProgresso, { width: `${progresso}%` }]} />
        </View>

        <ScrollView contentContainerStyle={styles.scrollContainer}>
          <Text style={styles.pergunta}>{perguntaAtual.pergunta}</Text>
          <View style={styles.opcoesContainer}>
            {perguntaAtual.opcoes.map((opcao, index) => {
              const isSelected = respostas[etapa]?.includes(opcao.texto);
              return (
                <TouchableOpacity
                  key={index}
                  style={[styles.opcao, isSelected && styles.opcaoSelecionada]}
                  onPress={() => handleSelect(opcao.texto)}
                >
                  <View style={[styles.radioCheck, isSelected && styles.radioCheckSelecionado]} />
                  <Text style={styles.textoOpcao}>{opcao.texto}</Text>
                </TouchableOpacity>
              );
            })}
          </View>
        </ScrollView>

        <TouchableOpacity style={styles.botaoContinuar} onPress={handleProximo}>
          <Text style={styles.textoBotaoContinuar}>Continuar</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: 'white' },
  container: { flex: 1, padding: 20, },
  header: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10, },
  backButton: { fontSize: 28, color: '#333', },
  progressoTexto: { fontSize: 16, color: '#666', },
  barraProgressoContainer: { height: 8, backgroundColor: '#E0E0E0', borderRadius: 4, width: '100%', marginBottom: 30, },
  barraProgresso: { height: '100%', backgroundColor: '#007BFF', borderRadius: 4, },
  scrollContainer: { flexGrow: 1, },
  pergunta: { fontSize: 24, fontWeight: 'bold', color: '#333', marginBottom: 30, },
  opcoesContainer: { width: '100%', },
  opcao: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#F9F9F9', borderWidth: 1, borderColor: '#E0E0E0', borderRadius: 10, padding: 15, marginBottom: 10, },
  opcaoSelecionada: { borderColor: '#007BFF', backgroundColor: '#E9F5FF', },
  radioCheck: { width: 24, height: 24, borderRadius: 12, borderWidth: 2, borderColor: '#C0C0C0', marginRight: 15, },
  radioCheckSelecionado: { backgroundColor: '#007BFF', borderColor: '#007BFF', },
  textoOpcao: { flex: 1, fontSize: 16, color: '#333', },
  botaoContinuar: { backgroundColor: '#007BFF', padding: 15, borderRadius: 10, alignItems: 'center', marginTop: 20, },
  textoBotaoContinuar: { color: 'white', fontSize: 18, fontWeight: 'bold', },
});
