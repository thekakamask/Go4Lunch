package com.dcac.go4lunch.repository.interfaceRepository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public interface IStreamChat {

    CollectionReference getChatCollection();

    Query getAllMessageForChat(String chat);

}
