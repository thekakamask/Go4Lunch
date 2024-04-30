package com.dcac.go4lunch.viewModels;

import com.dcac.go4lunch.models.chat.Message;
import com.dcac.go4lunch.repository.ChatRepository;
import com.dcac.go4lunch.repository.interfaceRepository.IStreamChat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatManager {

    private static volatile ChatManager instance;
    //private ChatRepository chatRepository;

    private IStreamChat chatRepository;

    private ChatManager() {
        chatRepository = ChatRepository.getInstance();
    }

    public static ChatManager getInstance() {
        ChatManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatManager.class) {
            if (instance == null) {
                instance = new ChatManager();
            }
            return instance;
        }
    }

    public Query getAllMessageForChat(String chat){
        return chatRepository.getAllMessageForChat(chat);
    }

    public void sendMessage(String chatId, Message message) {
        FirebaseFirestore.getInstance().collection("chats").document(chatId)
                .collection("messages").add(message);
    }

}
