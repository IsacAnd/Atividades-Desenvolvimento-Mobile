import React, { useState } from 'react';
import { View, Text, TextInput, Button, FlatList, StyleSheet, Alert, ScrollView } from 'react-native';
import { collection, addDoc, getFirestore } from 'firebase/firestore';
import { initializeApp } from 'firebase/app';

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

type Person = {
  name: string;
  amount: string;
  isEdited: boolean;
};

type AccountLog = {
  accountName: string;
  totalAmount: string;
  people: Person[];
};

export default function App() {
  const [billAmount, setBillAmount] = useState('');
  const [peopleCount, setPeopleCount] = useState('');
  const [people, setPeople] = useState<Person[]>([]);
  const [amountPerPerson, setAmountPerPerson] = useState<string | null>(null);
  const [accountName, setAccountName] = useState('');
  const [isCalculationDone, setIsCalculationDone] = useState(false);

  const initializePeople = () => {
    const count = parseInt(peopleCount);
    if (isNaN(count) || count <= 0) {
      Alert.alert('Erro', 'Por favor, insira um número de pessoas válido.');
      return;
    }

    const newPeople: Person[] = Array.from({ length: count }, (_, index) => ({
      name: `Pessoa ${index + 1}`,
      amount: '',
      isEdited: false,
    }));
    setPeople(newPeople);
  };

  const saveAccountToFirebase = async (account: AccountLog) => {
    try {
      await addDoc(collection(db, 'accounts'), account);
      Alert.alert('Sucesso', 'Conta salva com sucesso!');
    } catch (error) {
      Alert.alert('Erro', 'Erro ao salvar a conta no Firebase.');
      console.error('Erro ao salvar a conta no Firebase:', error);
    }
  };

  const calculateAmount = () => {
    const totalBill = parseFloat(billAmount);
    if (isNaN(totalBill) || totalBill <= 0) {
      Alert.alert('Erro', 'Por favor, insira um valor de conta válido.');
      return;
    }
  
    const numberOfPeople = people.length;
    if (numberOfPeople === 0) {
      Alert.alert('Erro', 'Por favor, gere uma lista de pessoas.');
      return;
    }
  
    const totalEditedAmount = people.reduce((acc, person) => {
      const amount = parseFloat(person.amount);
      return acc + (isNaN(amount) ? 0 : amount);
    }, 0);
  
    const uneditedPeople = people.filter(person => !person.amount || person.amount.trim() === '');
  
    if (totalEditedAmount > totalBill) {
      Alert.alert('Erro', 'O valor total editado não pode exceder o valor da conta.');
      return;
    }
  
    if (uneditedPeople.length === 0) {
      Alert.alert('Erro', 'Todas as pessoas editaram seus valores. Não há pessoas não editadas para dividir o restante da conta.');
      return;
    }
  
    const remainingBill = totalBill - totalEditedAmount;
  
    // Se não houver valor restante, todos devem pagar 0
    if (remainingBill <= 0) {
      Alert.alert('Erro', 'O valor total editado é igual ou maior que a conta total. Todos devem pagar R$ 0,00.');
      setAmountPerPerson('0.00');
      const updatedPeople = people.map(person => ({ ...person, amount: '0.00' }));
      setPeople(updatedPeople);
      setIsCalculationDone(true);
      return;
    }
  
    const amount = (remainingBill / uneditedPeople.length).toFixed(2);
  
    const updatedPeople = people.map(person =>
      person.amount ? person : { ...person, amount }
    );
  
    setPeople(updatedPeople);
    setAmountPerPerson(amount);
    setIsCalculationDone(true);
  }; 

  const resetFields = () => {
    setBillAmount('');
    setPeopleCount('');
    setPeople([]);
    setAccountName('');
    setAmountPerPerson(null);
    setIsCalculationDone(false); 
  };

  const handleSaveAccount = () => {
    // Verifica se o cálculo já foi feito, se não, executa o cálculo antes de salvar
    if (!isCalculationDone) {
      calculateAmount();
    }
  
    // Depois do cálculo, salva os dados
    const newAccountLog: AccountLog = {
      accountName,
      totalAmount: billAmount,
      people,
    };
  
    // Salva a conta no Firebase
    saveAccountToFirebase(newAccountLog);
  
    // Reseta os campos
    setIsCalculationDone(false); 
    resetFields();
  };
  

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Cálculo de Conta</Text>

      <TextInput
        style={styles.input}
        placeholder="Nome da Conta"
        value={accountName}
        onChangeText={setAccountName}
      />

      <TextInput
        style={styles.input}
        placeholder="Valor da Conta"
        keyboardType="numeric"
        value={billAmount}
        onChangeText={setBillAmount}
      />

      <TextInput
        style={styles.input}
        placeholder="Número de Pessoas"
        keyboardType="numeric"
        value={peopleCount}
        onChangeText={setPeopleCount}
      />

      <Button title="Gerar Pessoas" onPress={initializePeople} />

      <FlatList
        data={people}
        keyExtractor={(item, index) => index.toString()}
        renderItem={({ item, index }) => (
          <View style={styles.personContainer}>
            <TextInput
              style={[styles.input, styles.nameInput]}
              placeholder={`Nome da Pessoa`}
              value={item.name}
              onChangeText={text => {
                const updatedPeople = [...people];
                updatedPeople[index].name = text; // Atualiza o nome da pessoa
                updatedPeople[index].isEdited = true; // Marca como editado
                setPeople(updatedPeople);
              }}
            />
            <TextInput
              style={[styles.input, styles.amountInput]}
              placeholder={`Valor`}
              keyboardType="numeric"
              value={item.amount}
              onChangeText={text => {
                const updatedPeople = [...people];
                updatedPeople[index].amount = text;
                updatedPeople[index].isEdited = true; // Marca como editado
                setPeople(updatedPeople);
              }}
            />
            {item.isEdited && <Text style={styles.editedTag}>Editado</Text>}
          </View>
        )}
      />

      <Button title="Calcular" onPress={calculateAmount} />

      {amountPerPerson && (
        <Text style={styles.result}>
          Cada pessoa não editada deve pagar: R$ {amountPerPerson}
        </Text>
      )}

      {/* Botão para salvar a conta, desabilitado até que o cálculo seja feito */}
      <Button
        title="Salvar Conta"
        onPress={handleSaveAccount}
        disabled={!isCalculationDone} // Só habilita quando o cálculo foi feito
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 26,
    textAlign: 'center',
    marginBottom: 20,
    fontWeight: 'bold',
  },
  input: {
    height: 40,
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 5,
    marginBottom: 10,
    paddingLeft: 10,
    backgroundColor: '#fff',
  },
  nameInput: {
    flex: 2,
  },
  amountInput: {
    flex: 1,
    marginLeft: 10,
  },
  result: {
    marginTop: 20,
    fontSize: 20,
    textAlign: 'center',
    color: '#333',
    fontWeight: 'bold',
  },
  personContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  editedTag: {
    color: 'red',
    fontWeight: 'bold',
    marginLeft: 10,
  },
});
