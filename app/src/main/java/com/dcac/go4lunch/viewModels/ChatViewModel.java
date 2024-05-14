package com.dcac.go4lunch.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dcac.go4lunch.models.chat.Message;
import com.dcac.go4lunch.repository.ChatRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;

    public ChatViewModel(ChatRepository chatRepository) {
        this.chatRepository=chatRepository;
    }



    public LiveData<Query> getAllMessagesForChat(String chatId) {
        return chatRepository.getMessagesLiveData(chatId);
    }

    public void sendMessage(String chatId, Message message) {
        chatRepository.sendMessage(chatId, message);
    }

}
