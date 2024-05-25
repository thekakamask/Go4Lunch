package com.dcac.go4lunch.ui.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dcac.go4lunch.databinding.FragmentChatBinding;
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.models.chat.Message;
import com.dcac.go4lunch.viewModels.ChatViewModel;
import com.dcac.go4lunch.views.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.UUID;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private String userId;
    private String userName;
    private String userProfilePicUrl;


    private ChatViewModel chatViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            userName = user.getDisplayName();
            userProfilePicUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            Log.d("ChatFragment", "userId: " + userId + ", userName: " + userName + ", userProfilePicUrl: " + userProfilePicUrl);
        } else {
            Log.e("ChatFragment", "User is not authenticated");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactory factory = ViewModelFactory.getInstance(requireContext().getApplicationContext());
        chatViewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);

        chatViewModel.getAllMessagesForChat("chatId").observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();

                adapter = new ChatAdapter(options);
                binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.chatRecyclerView.setAdapter(adapter);
                adapter.startListening();
            } else {
                Log.e("ChatFragment", "Query is null");
            }
        });

        binding.sendButton.setOnClickListener(v -> {
            String messageText = binding.chatEditText.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(messageText, null);
                binding.chatEditText.setText("");
            }
        });

        binding.addFileButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
    }

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    uploadImage(result);
                }
            });

    public void sendMessage(String text, String imageUrl) {
        if (userId != null && userName != null) {
            Message message = new Message(text, userId, userName, userProfilePicUrl, imageUrl, new Date());
            Log.d("ChatFragment", "Sending message: " + message);
            chatViewModel.sendMessage("chatId", message);
        } else {
            Log.e("ChatFragment", "Failed to send message: userId or userName is null");
            Log.e("ChatFragment", "userId: " + userId + ", userName: " + userName + ", userProfilePicUrl: " + userProfilePicUrl);
        }
    }

    public void uploadImage(Uri imageUri) {
        String fileName = "images/" + UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(fileName);

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            sendMessage(null, downloadUrl);
        })).addOnFailureListener(e -> {
            // Gérer l'erreur
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}