package com.stream.zenfit.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.stream.zenfit.Model.ChatBotModel;
import com.stream.zenfit.R;

import java.util.List;

public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ChatBotViewHolder> {

    public ChatBotAdapter(List<ChatBotModel> conversationList) {
        this.conversationList = conversationList;
    }

    List<ChatBotModel> conversationList;

    @NonNull
    @Override
    public ChatBotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_with_an_ai_carrying_container,  null);
        ChatBotViewHolder chatBotViewHolder = new ChatBotViewHolder(chatView);
        return chatBotViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBotViewHolder holder, int position) {
        ChatBotModel chat = conversationList.get(position);
        if (chat.getSender().equals(ChatBotModel.SENT_BY_USER))
        {
            holder.botChatContainer.setVisibility(View.GONE);
            holder.userChatContainer.setVisibility(View.VISIBLE);
            holder.userChatText.setText(chat.getMessage());
        }
        else
        {
            holder.userChatContainer.setVisibility(View.GONE);
            holder.botChatContainer.setVisibility(View.VISIBLE);
            holder.botChatText.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ChatBotViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout botChatContainer, userChatContainer;
        TextView botChatText, userChatText;
        TextView botReplyTime, userQueryTime;

        public ChatBotViewHolder(@NonNull View itemView) {
            super(itemView);
            botChatContainer = itemView.findViewById(R.id.botChatContainer);
            userChatContainer = itemView.findViewById(R.id.userChatContainer);
            botChatText = itemView.findViewById(R.id.botChatText);
            userChatText = itemView.findViewById(R.id.userChatText);
            botReplyTime = itemView.findViewById(R.id.botReplyTime);
            userQueryTime = itemView.findViewById(R.id.userQueryTime);
        }
    }
}
