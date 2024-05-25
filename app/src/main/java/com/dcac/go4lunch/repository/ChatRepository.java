package com.dcac.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.models.chat.Message;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ChatRepository {

    private static final String CHAT_COLLECTION = "chats";
    private static final String MESSAGE_COLLECTION = "messages";
    private static volatile ChatRepository instance;

    private final MutableLiveData<Query> messagesLiveData = new MutableLiveData<>();
    private final CollectionReference chatCollection;

    private ChatRepository() {
        chatCollection = FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }

    public static synchronized ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }
        return instance;
    }

    public LiveData<Query> getMessagesLiveData(String chatId) {
        Query query = chatCollection.document(chatId)
                .collection(MESSAGE_COLLECTION)
                .orderBy("dateCreated")
                .limit(50);
        messagesLiveData.setValue(query);
        return messagesLiveData;
    }

    public void sendMessage(String chatId, Message message) {
        chatCollection.document(chatId)
                .collection(MESSAGE_COLLECTION)
                .add(message)
                .addOnSuccessListener(documentReference -> Log.d("ChatRepository", "Message sent successfully"))
                .addOnFailureListener(e -> Log.e("ChatRepository", "Error sending message", e));
    }

}
