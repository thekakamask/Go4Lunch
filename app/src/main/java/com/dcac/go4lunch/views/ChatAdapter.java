package com.dcac.go4lunch.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcac.go4lunch.R;
import com.dcac.go4lunch.models.chat.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.MessageViewHolder> {

    private final String currentUserName;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, String currentUserName) {
        super(options);
        this.currentUserName = currentUserName;
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
        holder.bind(model, model.getUserName().equals(currentUserName));
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView profileImageView;
        TextView messageTextView;
        ImageView messageImageView;
        TextView dateTextView;
        ConstraintLayout rootLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.UserTextView);
            profileImageView = itemView.findViewById(R.id.profileImage);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageImageView = itemView.findViewById(R.id.senderImageView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            rootLayout = itemView.findViewById(R.id.activity_mentor_chat_item_root_view);
        }

        public void bind(Message message, boolean isSentByCurrentUser) {
            userNameTextView.setText(message.getUserName());
            messageTextView.setText(message.getText());
            dateTextView.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(message.getDateCreated()));

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

            adjustConstraints(isSentByCurrentUser);
        }

        private void adjustConstraints(boolean isSentByCurrentUser) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(rootLayout);

            if (isSentByCurrentUser) {
                // Constraints for sent messages
                constraintSet.clear(R.id.profileContainer, ConstraintSet.START);
                constraintSet.clear(R.id.messageContainer, ConstraintSet.START);
                constraintSet.clear(R.id.dateTextView, ConstraintSet.START);

                constraintSet.connect(R.id.profileContainer, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                constraintSet.connect(R.id.messageContainer, ConstraintSet.END, R.id.profileContainer, ConstraintSet.START);
                constraintSet.connect(R.id.dateTextView, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                constraintSet.setHorizontalBias(R.id.messageContainer, 1.0f); // Align to right
            } else {
                // Constraints for received messages
                constraintSet.clear(R.id.profileContainer, ConstraintSet.END);
                constraintSet.clear(R.id.messageContainer, ConstraintSet.END);
                constraintSet.clear(R.id.dateTextView, ConstraintSet.END);

                constraintSet.connect(R.id.profileContainer, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.connect(R.id.messageContainer, ConstraintSet.START, R.id.profileContainer, ConstraintSet.END);
                constraintSet.connect(R.id.dateTextView, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

                constraintSet.setHorizontalBias(R.id.messageContainer, 0.0f); // Align to left
            }

            constraintSet.applyTo(rootLayout);
        }
    }
}
