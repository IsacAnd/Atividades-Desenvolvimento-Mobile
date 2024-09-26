import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { useLocalSearchParams } from 'expo-router';

const AccountDetailScreen: React.FC = () => {
  const { accountName, totalAmount, people } = useLocalSearchParams();

  // Converte a string `people` de volta para um array
  const peopleArray = JSON.parse(people as string);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Detalhes da Conta</Text>
      <Text style={styles.logText}>Conta: {accountName}</Text>
      <Text style={styles.logText}>Valor Total: R$ {totalAmount}</Text>
      <Text style={styles.subtitle}>Participantes:</Text>
      {peopleArray.map((person: { name: string; amount: string; isEdited: boolean }, index: number) => (
        <Text key={index} style={styles.logText}>
          {person.name} - R$ {person.amount} {person.isEdited ? '(Editado)' : ''}
        </Text>
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    textAlign: 'center',
    marginBottom: 20,
  },
  logText: {
    fontSize: 18,
  },
  subtitle: {
    fontSize: 20,
    marginVertical: 10,
  },
});

export default AccountDetailScreen;
