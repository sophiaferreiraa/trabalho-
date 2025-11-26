import React, { useEffect, useState } from 'react';
import { Modal, View, Text, TouchableOpacity, StyleSheet, Dimensions, Animated } from 'react-native';

type Props = {
  visible: boolean;
  titulo: string;
  texto: string;
  onProximo: () => void;
  botaoTexto: string;
};

const { width } = Dimensions.get('window');

export function TutorialOverlay({ visible, titulo, texto, onProximo, botaoTexto }: Props) {
  const [mostrarBotao, setMostrarBotao] = useState(false);
  const fadeAnim = useState(new Animated.Value(0))[0]; // Animação de opacidade

  useEffect(() => {
    if (visible) {
      setMostrarBotao(false);
      // Simula o tempo de leitura antes de mostrar o botão (2 segundos)
      const timer = setTimeout(() => {
        setMostrarBotao(true);
        Animated.timing(fadeAnim, {
          toValue: 1,
          duration: 500,
          useNativeDriver: true,
        }).start();
      }, 2000); 
      return () => clearTimeout(timer);
    }
  }, [visible]);

  if (!visible) return null;

  return (
    <Modal transparent visible={visible} animationType="fade">
      <View style={styles.overlay}>
        <View style={styles.card}>
          <Text style={styles.titulo}>{titulo}</Text>
          <Text style={styles.texto}>{texto}</Text>
          
          {mostrarBotao && (
            <Animated.View style={{ opacity: fadeAnim, width: '100%' }}>
              <TouchableOpacity style={styles.botao} onPress={onProximo}>
                <Text style={styles.botaoTexto}>{botaoTexto}</Text>
              </TouchableOpacity>
              <Text style={styles.dicaSeta}>👆 Clique para avançar</Text>
            </Animated.View>
          )}
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.85)', // Fundo escuro
    justifyContent: 'center',
    alignItems: 'center',
  },
  card: {
    backgroundColor: 'white',
    width: width * 0.85,
    padding: 30,
    borderRadius: 20,
    alignItems: 'center',
    elevation: 10,
  },
  titulo: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#007BFF',
    textAlign: 'center',
  },
  texto: {
    fontSize: 16,
    textAlign: 'center',
    marginBottom: 30,
    color: '#444',
    lineHeight: 24,
  },
  botao: {
    backgroundColor: '#28a745',
    paddingVertical: 15,
    borderRadius: 10,
    width: '100%',
    alignItems: 'center',
  },
  botaoTexto: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 18,
  },
  dicaSeta: {
    marginTop: 10,
    textAlign: 'center',
    color: '#aaa',
    fontSize: 12
  }
});