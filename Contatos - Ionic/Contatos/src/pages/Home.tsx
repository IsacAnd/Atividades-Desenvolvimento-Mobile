import React, { useState, useEffect } from "react";
import {
  IonButtons,
  IonContent,
  IonHeader,
  IonItem,
  IonLabel,
  IonList,
  IonMenuButton,
  IonPage,
  IonSearchbar,
  IonTitle,
  IonToolbar,
} from "@ionic/react";
import "../styles/searchbar.css";
import { collection, getDocs, getFirestore } from "firebase/firestore";
import { app } from "../firebase-config";
import { useLocation, useHistory } from "react-router-dom";

// Definindo a interface de contato
interface Contact {
  id: string; // ID é uma string
  name: string;
  email: string;
  number: string;
}

// Obtendo a instância do Firestore
const db = getFirestore(app);

const Home: React.FC = () => {
  const [searchText, setSearchText] = useState("");
  const [contacts, setContacts] = useState<Contact[]>([]);
  const [filteredContacts, setFilteredContacts] = useState<Contact[]>([]);
  
  const location = useLocation(); // Hook para obter a localização atual
  const history = useHistory(); // Hook para navegação

  // Função para buscar contatos do Firestore
  const getContacts = async () => {
    try {
      const querySnapshot = await getDocs(collection(db, "contacts"));
      const loadedContacts = querySnapshot.docs.map(doc => ({
        id: doc.id,
        ...(doc.data() as Omit<Contact, "id">), 
      }));
      setContacts(loadedContacts);
      setFilteredContacts(loadedContacts);
    } catch (e) {
      console.error("Erro ao recuperar contatos: ", e);
    }
  };

  useEffect(() => {
    getContacts();
  }, [location]); // Chama o getContacts toda vez que a localização (rota) mudar

  const handleSearch = (event: CustomEvent) => {
    const query = (event.detail.value as string)?.toLowerCase() || "";
    setSearchText(query);
    setFilteredContacts(
      contacts.filter(
        (contact) =>
          contact.name.toLowerCase().includes(query) ||
          contact.email.toLowerCase().includes(query) ||
          contact.number.includes(query)
      )
    );
  };

  const handleClick = (contact: Contact) => {
    // Redireciona para a página de edição de contato
    history.push(`/edit-contact/${contact.id}`);
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonButtons slot="start">
            <IonMenuButton />
          </IonButtons>
          <IonTitle>Lista de Contatos</IonTitle>
        </IonToolbar>
      </IonHeader>

      <IonContent fullscreen>
        <IonHeader collapse="condense">
          <IonToolbar>
            <IonTitle size="large">Lista de Contatos</IonTitle>
          </IonToolbar>
        </IonHeader>
        <IonSearchbar
          className="custom-searchbar"
          placeholder="Pesquisar contato"
          value={searchText}
          onIonInput={handleSearch}
          style={{ width: "75%" }}
        ></IonSearchbar>

        <IonList>
          {filteredContacts.map((contact) => (
            <IonItem key={contact.id} button onClick={() => handleClick(contact)}>
              <IonLabel>
                <h2>{contact.name}</h2>
                <p>{contact.number}</p>
                <p>{contact.email}</p>
              </IonLabel>
            </IonItem>
          ))}
        </IonList>
      </IonContent>
    </IonPage>
  );
};

export default Home;
