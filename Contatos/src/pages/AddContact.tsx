/* eslint-disable @typescript-eslint/no-non-null-assertion */
import React, { useState } from 'react';
import { IonButtons, IonContent, IonHeader, IonInput, IonMenuButton, IonPage, IonTitle, IonToolbar, IonGrid, IonRow, IonCol, IonButton, IonAlert } from '@ionic/react';
import { collection, addDoc, getFirestore } from 'firebase/firestore';
import { app } from '../firebase-config'; // Certifique-se de que a instância Firestore está corretamente exportada
import './AddContact.css'; // Arquivo de estilos

const db = getFirestore(app);

const AddContact: React.FC = () => {
  // Definir os estados para os campos de entrada
  const [name, setName] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [number, setNumber] = useState<string>('');
  const [showAlert, setShowAlert] = useState<boolean>(false);

  // Função para lidar com a adição do contato
  const handleAddContact = async () => {
    // Verificar se todos os campos estão preenchidos
    if (!name || !email || !number) {
      setShowAlert(true);
      return;
    }

    // Criar um novo contato
    const newContact = {
      name,
      email,
      number,
    };

    try {
      // Adicionar o contato ao Firestore
      await addDoc(collection(db, "contacts"), newContact);
      console.log('Novo Contato Adicionado:', newContact);
      
      // Limpar os campos após a adição
      setName('');
      setEmail('');
      setNumber('');
    } catch (e) {
      console.error("Erro ao adicionar contato: ", e);
    }
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonButtons slot="start">
            <IonMenuButton />
          </IonButtons>
          <IonTitle>Adicionar Contato</IonTitle>
        </IonToolbar>
      </IonHeader>

      <IonContent fullscreen>
        <IonHeader collapse="condense">
          <IonToolbar>
            <IonTitle size="large">Adicionar Contato</IonTitle>
          </IonToolbar>
        </IonHeader>

        <IonGrid className="contact-form-grid">
          <IonRow>
            <IonCol size="12" sizeMd="8" offsetMd="2">
              <IonInput
                label="Nome do contato"
                labelPlacement="floating"
                fill="outline"
                placeholder="Nome do contato"
                value={name}
                onIonInput={e => setName(e.detail.value!)}
              ></IonInput>
            </IonCol>
          </IonRow>
          <IonRow>
            <IonCol size="12" sizeMd="8" offsetMd="2">
              <IonInput
                label="Email do contato"
                labelPlacement="floating"
                fill="outline"
                placeholder="Email do contato"
                value={email}
                onIonInput={e => setEmail(e.detail.value!)}
              ></IonInput>
            </IonCol>
          </IonRow>
          <IonRow>
            <IonCol size="12" sizeMd="8" offsetMd="2">
              <IonInput
                label="Número do contato"
                labelPlacement="floating"
                fill="outline"
                placeholder="Número do contato"
                value={number}
                onIonInput={e => setNumber(e.detail.value!)}
              ></IonInput>
            </IonCol>
          </IonRow>
          <IonRow>
            <IonCol size="12" sizeMd="8" offsetMd="2">
              <IonButton expand="block" onClick={handleAddContact}>
                Adicionar Contato
              </IonButton>
            </IonCol>
          </IonRow>
        </IonGrid>

        <IonAlert
          isOpen={showAlert}
          onDidDismiss={() => setShowAlert(false)}
          header={'Campos Obrigatórios'}
          message={'Por favor, preencha todos os campos antes de adicionar um contato.'}
          buttons={['OK']}
        />
      </IonContent>
    </IonPage>
  );
};

export default AddContact;
