import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, Button, StyleSheet, TouchableOpacity, Alert } from 'react-native';
import { getDocs, collection, getFirestore } from 'firebase/firestore';
import { initializeApp } from 'firebase/app';
import { useNavigationState } from '@react-navigation/native';
import { Link, router } from 'expo-router';

// Configuração do Firebase
const firebaseConfig = {
  apiKey: "AIzaSyDRrLYRaiep75VQ7iSWMYAV3YcWyQ1aAXY",
  authDomain: "contas-ae5c4.firebaseapp.com",
  projectId: "contas-ae5c4",
  storageBucket: "contas-ae5c4.appspot.com",
  messagingSenderId: "161516186333",
  appId: "1:161516186333:web:db94a4b1e70cc888dfc934"
};

// Inicialize o Firebase
const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

// Defina o tipo da conta
type AccountType = {
  accountName: string;
  totalAmount: string;
  people: {
    name: string;
    amount: string;
    isEdited: boolean;
  }[];
};

const AccountListScreen: React.FC = () => {
  const navigationState = useNavigationState((state) => state);
  console.log(navigationState);

  const [accountLogs, setAccountLogs] = useState<AccountType[]>([]);

  // Função para buscar contas do Firestore
  const fetchAccountsFromFirebase = async () => {
    try {
      const querySnapshot = await getDocs(collection(db, 'accounts'));
      const accounts: AccountType[] = querySnapshot.docs.map(doc => ({
        accountName: doc.data().accountName,
        totalAmount: doc.data().totalAmount,
        people: doc.data().people,
      }));
      setAccountLogs(accounts);
    } catch (error) {
      Alert.alert('Erro', 'Erro ao buscar contas do Firebase.');
      console.error('Erro ao buscar contas do Firebase:', error);
    }
  };

  // Chamar a função para buscar contas ao carregar o componente
  useEffect(() => {
    fetchAccountsFromFirebase();
  }, []);

  function viewAccountDetails(item: AccountType): void {
    router.push({
      pathname: '/accountdetail', // Define a rota
      params: {
        accountName: item.accountName, // Passa os detalhes da conta
        totalAmount: item.totalAmount,
        people: JSON.stringify(item.people) // Serializa os dados complexos
      }
    });
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Contas Pagas</Text>

      <FlatList
        data={accountLogs}
        keyExtractor={(item, index) => index.toString()}
        renderItem={({ item }) => (
          <TouchableOpacity onPress={() => viewAccountDetails(item)}>
            <View style={styles.logContainer}>
              <Text style={styles.logText}>Conta: {item.accountName}</Text>
              <Text style={styles.logText}>Valor Total: R$ {item.totalAmount}</Text>
            </View>
          </TouchableOpacity>
        )}
      />

      <Button title="Adicionar Nova Conta" onPress={() => { router.push('/') }} />
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
  logContainer: {
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    marginBottom: 10,
  },
  logText: {
    fontSize: 18,
  },
});

export default AccountListScreen;
