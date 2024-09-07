/* eslint-disable @typescript-eslint/no-non-null-assertion */
import React, { useState, useEffect } from 'react';
import { IonContent, IonHeader, IonInput, IonPage, IonTitle, IonToolbar, IonGrid, IonRow, IonCol, IonButton, IonAlert } from '@ionic/react';
import { doc, getDoc, updateDoc, getFirestore } from 'firebase/firestore';
import { app } from '../firebase-config'; // Certifique-se de que a instância Firestore está corretamente exportada
import './AddContact.css'; // Arquivo de estilos
import { useHistory } from 'react-router-dom'; // Importando o hook useHistory

const db = getFirestore(app);

interface EditContactProps {
  match: {
    params: {
      id: string;
    };
  };
}

const EditContact: React.FC<EditContactProps> = ({ match }) => {
  const [name, setName] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [number, setNumber] = useState<string>('');
  const [showAlert, setShowAlert] = useState<boolean>(false);
  const history = useHistory(); // Inicializando o hook useHistory

  // Função para buscar os dados do contato com base no ID
  useEffect(() => {
    const getContact = async () => {
      const docRef = doc(db, "contacts", match.params.id);
      const docSnap = await getDoc(docRef);

      if (docSnap.exists()) {
        const contact = docSnap.data();
        setName(contact.name || '');
        setEmail(contact.email || '');
        setNumber(contact.number || '');
      } else {
        console.error("Contato não encontrado!");
      }
    };

    getContact();
  }, [match.params.id]);

  // Função para lidar com a edição do contato
  const handleEditContact = async () => {
    // Verificar se todos os campos estão preenchidos
    if (!name || !email || !number) {
      setShowAlert(true);
      return;
    }

    // Atualizar o contato no Firestore
    const contact = {
      name,
      email,
      number,
    };

    try {
      const contactRef = doc(db, "contacts", match.params.id);
      await updateDoc(contactRef, contact);
      console.log('Contato Atualizado:', contact);

      // Redirecionar para Home após a edição bem-sucedida
      history.push('/home');
    } catch (e) {
      console.error("Erro ao atualizar contato: ", e);
    }
  };

  return (
    <IonPage>
      <IonContent fullscreen>
        <IonHeader collapse="condense">
          <IonToolbar>
            <IonTitle size="large">Editar Contato</IonTitle>
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
              <IonButton expand="block" onClick={handleEditContact}>
                Salvar Alterações
              </IonButton>
            </IonCol>
          </IonRow>
        </IonGrid>

        <IonAlert
          isOpen={showAlert}
          onDidDismiss={() => setShowAlert(false)}
          header={'Campos Obrigatórios'}
          message={'Por favor, preencha todos os campos antes de salvar as alterações.'}
          buttons={['OK']}
        />
      </IonContent>
    </IonPage>
  );
};

export default EditContact;
