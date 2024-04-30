package com.dcac.go4lunch;

import com.dcac.go4lunch.repository.interfaceRepository.IStreamChat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class ChatRepositoryTestDouble implements IStreamChat {


    @Override
    public CollectionReference getChatCollection() {
        return null;
    }

    @Override
    public Query getAllMessageForChat(String chat) {
        return null;
    }
}
