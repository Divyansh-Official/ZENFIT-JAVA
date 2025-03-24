package com.stream.zenfit.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.stream.zenfit.Modal.ChatBotModal;
import com.stream.zenfit.R;

import java.util.List;

public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ChatBotViewHolder> {

    private List<ChatBotModal> conversationList;
    private Context context;

    public ChatBotAdapter(Context context, List<ChatBotModal> conversationList) {
        this.context = context;
        this.conversationList = conversationList;
    }

    @NonNull
    @Override
    public ChatBotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbot_conversation_container, parent, false);
        ChatBotViewHolder chatBotViewHolder = new ChatBotViewHolder(chatView);
        return chatBotViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBotViewHolder holder, int position) {
        ChatBotModal chat = conversationList.get(position);
        if (chat.getSender().equals(ChatBotModal.SENT_BY_USER)) {
            holder.botChatContainer.setVisibility(View.GONE);
            holder.userChatContainer.setVisibility(View.VISIBLE);
            holder.userChatText.setText(chat.getMessage());
//            holder.userQueryTime.setText(chat.getTimeStamp()); // Set timestamp for user message
        } else {
            holder.userChatContainer.setVisibility(View.GONE);
            holder.botChatContainer.setVisibility(View.VISIBLE);
            holder.botChatText.setText(chat.getMessage());
//            holder.botReplyTime.setText(chat.getTimeStamp()); // Set timestamp for bot message
        }
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ChatBotViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout botChatContainer, userChatContainer;
        TextView botChatText, userChatText;
        TextView botReplyTime, userQueryTime;

        public ChatBotViewHolder(@NonNull View itemView) {
            super(itemView);
            botChatContainer = itemView.findViewById(R.id.botChatContainer);
            userChatContainer = itemView.findViewById(R.id.userChatContainer);
            botChatText = itemView.findViewById(R.id.botChatText);
            userChatText = itemView.findViewById(R.id.userChatText);
//            botReplyTime = itemView.findViewById(R.id.botReplyTime);
//            userQueryTime = itemView.findViewById(R.id.userQueryTime);

            // Set long click listeners for copying text
            botChatText.setOnLongClickListener(view -> {
                // Copy text to clipboard
                copyTextToClipboard(botChatText.getText().toString());

                // Vibrate for 100 milliseconds
                Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(200);
                    }
                }

                return true;
            });


            userChatText.setOnLongClickListener(view -> {
                copyTextToClipboard(userChatText.getText().toString());
                return true;
            });
        }
    }

    private void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Text copied to clipboard!", Toast.LENGTH_SHORT).show();
    }
}
