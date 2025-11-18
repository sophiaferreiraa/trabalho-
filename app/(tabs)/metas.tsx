import AsyncStorage from '@react-native-async-storage/async-storage';
import { useEffect, useState } from 'react';
import { Button, Modal, SafeAreaView, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';

type Meta = {
  id: string;
  titulo: string;
  descricao: string;
  progresso: number;
  vencimento: string;
};

export default function MetasScreen() {
  const [metas, setMetas] = useState<Meta[]>([]);
  const [modalVisivel, setModalVisivel] = useState(false);
  const [descricaoMeta, setDescricaoMeta] = useState('');
  const [vencimentoMeta, setVencimentoMeta] = useState('');
  const [progressoMeta, setProgressoMeta] = useState('');

  // Carrega metas do AsyncStorage
  useEffect(() => {
    (async () => {
      const salvas = await AsyncStorage.getItem('metas');
      if (salvas) setMetas(JSON.parse(salvas));
    })();
  }, []);

  // Salva metas sempre que houver mudança
  useEffect(() => {
    AsyncStorage.setItem('metas', JSON.stringify(metas));
  }, [metas]);

  const handleAddMeta = () => {
    if (!descricaoMeta || !vencimentoMeta) {
      alert('Por favor, preencha a descrição e o vencimento.');
      return;
    }
    const progressoNumerico = parseInt(progressoMeta, 10) || 0;

    const novaMeta: Meta = {
      id: Date.now().toString(),
      titulo: `Meta ${metas.length + 1}`,
      descricao: descricaoMeta,
      vencimento: vencimentoMeta,
      progresso: Math.min(Math.max(progressoNumerico, 0), 100),
    };

    setMetas([...metas, novaMeta]);
    setDescricaoMeta('');
    setVencimentoMeta('');
    setProgressoMeta('');
    setModalVisivel(false);
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.header}>
        <View>
          <Text style={styles.headerTitle}>Relate suas metas</Text>
          <Text style={styles.headerSubtitle}>
            Torne seus objetivos mais claros e {'\n'}
            fortaleça seu foco
          </Text>
        </View>
        <TouchableOpacity style={styles.addButton} onPress={() => setModalVisivel(true)}>
          <Text style={styles.addButtonText}>+</Text>
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.gridContainer}>
        {metas.length > 0 ? metas.map(meta => (
          <View key={meta.id} style={styles.card}>
            <Text style={styles.cardTitle}>{meta.titulo}</Text>
            <Text style={styles.cardDescriptionLabel}>Descrição:</Text>
            <Text style={styles.cardDescriptionText}>{meta.descricao}</Text>
            <View style={styles.cardFooter}>
              {meta.progresso === 100 ? (
                <Text style={styles.cardCompletedText}>Completado {meta.vencimento}</Text>
              ) : (
                <>
                  <View style={styles.progressBarBackground}>
                    <View style={[styles.progressBarFill, { width: `${meta.progresso}%` }]} />
                  </View>
                  <Text style={styles.cardDueDate}>Vencimento {meta.vencimento}</Text>
                </>
              )}
            </View>
          </View>
        )) : (
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>Você ainda não tem metas.</Text>
            <Text style={styles.emptySubtext}>Clique no botão '+' para adicionar sua primeira meta!</Text>
          </View>
        )}
      </ScrollView>

      <Modal animationType="slide" transparent visible={modalVisivel} onRequestClose={() => setModalVisivel(false)}>
        <View style={styles.modalContainer}>
          <View style={styles.modalView}>
            <Text style={styles.modalTitle}>Nova Meta</Text>
            <TextInput
              placeholder="Descrição da meta"
              placeholderTextColor="#555"
              style={[styles.input, { color: '#000' }]}
              value={descricaoMeta}
              onChangeText={setDescricaoMeta}
            />
            <TextInput
              placeholder="Vencimento (ex: dez 2025)"
              placeholderTextColor="#555"
              style={[styles.input, { color: '#000' }]}
              value={vencimentoMeta}
              onChangeText={setVencimentoMeta}
            />
            <TextInput
              placeholder="Progresso inicial (0-100)"
              placeholderTextColor="#555"
              style={[styles.input, { color: '#000' }]}
              keyboardType="numeric"
              value={progressoMeta}
              onChangeText={setProgressoMeta}
            />
            <View style={styles.modalButtons}>
              <Button title="Cancelar" onPress={() => setModalVisivel(false)} color="gray" />
              <Button title="Salvar" onPress={handleAddMeta} />
            </View>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#FFFFFF' },
  header: {
    backgroundColor: '#007BFF',
    paddingHorizontal: 20,
    paddingTop: 50,
    paddingBottom: 25,
    borderBottomLeftRadius: 30,
    borderBottomRightRadius: 30,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  headerTitle: { fontSize: 24, fontWeight: 'bold', color: 'white' },
  headerSubtitle: { fontSize: 15, color: 'white', marginTop: 5 },
  addButton: {
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    width: 44,
    height: 44,
    borderRadius: 22,
    justifyContent: 'center',
    alignItems: 'center',
  },
  addButtonText: { color: 'white', fontSize: 28, fontWeight: 'bold' },
  gridContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-around',
    padding: 10,
  },
  card: {
    width: '46%',
    backgroundColor: 'white',
    borderRadius: 15,
    padding: 15,
    marginVertical: 10,
    borderWidth: 1.5,
    borderColor: '#929090ff',
    minHeight: 180,
    justifyContent: 'space-between',
  },
  cardTitle: { fontSize: 18, fontWeight: 'bold', color: '#333' },
  cardDescriptionLabel: { fontSize: 14, color: 'gray', marginTop: 10 },
  cardDescriptionText: { fontSize: 14, color: '#333', marginTop: 2 },
  cardFooter: { marginTop: 'auto', paddingTop: 10 },
  cardCompletedText: { fontSize: 12, color: 'gray', fontStyle: 'italic' },
  progressBarBackground: {
    height: 8,
    backgroundColor: '#929292ff',
    borderRadius: 4,
    width: '100%',
  },
  progressBarFill: {
    height: '100%',
    backgroundColor: '#767676',
    borderRadius: 4,
  },
  cardDueDate: { fontSize: 12, color: 'gray', marginTop: 5 },
  emptyContainer: {
    flex: 1,
    width: '100%',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 100,
  },
  emptyText: { fontSize: 18, color: 'gray' },
  emptySubtext: { fontSize: 14, color: '#A0A0A0', marginTop: 10 },
  modalContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalView: {
    width: '85%',
    backgroundColor: 'white',
    borderRadius: 20,
    padding: 20,
    alignItems: 'center',
    elevation: 5,
  },
  modalTitle: { fontSize: 20, fontWeight: 'bold', marginBottom: 20 },
  input: {
    width: '100%',
    borderWidth: 1,
    borderColor: '#ddd',
    padding: 10,
    borderRadius: 10,
    marginBottom: 15,
  },
  modalButtons: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    width: '100%',
    marginTop: 10,
  },
});
