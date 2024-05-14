package com.dcac.go4lunch.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcac.go4lunch.R;
import com.dcac.go4lunch.models.chat.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.MessageViewHolder> {

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
        holder.bind(model);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView profileImageView;
        TextView messageTextView;
        ImageView messageImageView;
        TextView dateTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.UserTextView);
            profileImageView = itemView.findViewById(R.id.profileImage);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageImageView = itemView.findViewById(R.id.senderImageView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(Message message) {
            userNameTextView.setText(message.getUserName());
            messageTextView.setText(message.getText());

            Glide.with(itemView.getContext())
                    .load(message.getUserProfilePicUrl())
                    .circleCrop()
                    .placeholder(R.drawable.account_icon)
                    .error(R.drawable.account_icon)
                    .into(profileImageView);

            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                messageImageView.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.account_icon)
                        .error(R.drawable.account_icon)
                        .into(messageImageView);
            } else {
                messageImageView.setVisibility(View.GONE);
            }

            if (message.getDateCreated() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(message.getDateCreated());
                dateTextView.setText(formattedDate);
            } else {
                dateTextView.setText("");
            }
        }
    }
}
