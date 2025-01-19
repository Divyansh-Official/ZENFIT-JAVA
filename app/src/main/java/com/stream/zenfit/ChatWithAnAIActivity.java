package com.stream.zenfit;

import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stream.zenfit.Adapter.ChatBotAdapter;
import com.stream.zenfit.Model.ChatBotModel;
import com.stream.zenfit.databinding.ActivityChatWithAnAiBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatWithAnAIActivity extends AppCompatActivity {

    private ActivityChatWithAnAiBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private List<ChatBotModel> conversationList;
    private ChatBotAdapter chatBotAdapter;
    private DatabaseReference databaseReference;

    private final String apiURL = "https://api.openai.com/v1/chat/completions";
//    private final String apiKey = "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"; // Move this to a safer location!
    private final String apiKey = "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"; // Move this to a safer location!

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        binding = ActivityChatWithAnAiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();
        initializeChat();

        binding.sendButton.setOnClickListener(v -> {
            String userMessage = binding.chatTextInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendUserMessage(userMessage);
            } else {
                binding.chatTextInput.setError("Please Enter A Query");
            }
        });
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

    private void initializeChat() {
        conversationList = new ArrayList<>();
        chatBotAdapter = new ChatBotAdapter(conversationList);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(chatBotAdapter);
    }

    private void sendUserMessage(String message) {
        binding.chatTextInput.setText("");
        conversationList.add(new ChatBotModel(ChatBotModel.SENT_BY_USER, message));
        chatBotAdapter.notifyDataSetChanged();
        binding.chatRecyclerView.smoothScrollToPosition(conversationList.size() - 1);

        // Call OpenAI API
        callOpenAIAPI(message);
    }

    private void callOpenAIAPI(String userMessage) {
        conversationList.add(new ChatBotModel(ChatBotModel.SENT_BY_BOT, "TYPING..."));
        chatBotAdapter.notifyDataSetChanged();

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("model", "gpt-4o");

            JSONArray messages = new JSONArray();
            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "user");
            messageObject.put("content", userMessage);
            messages.put(messageObject);

            requestJson.put("messages", messages);

            RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
            Request request = new Request.Builder()
                    .url(apiURL)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    addBotResponse("Failed to connect: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseJson = new JSONObject(response.body().string());
                            String botMessage = responseJson.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");
                            addBotResponse(botMessage.trim());
                        } catch (JSONException e) {
                            addBotResponse("Failed to parse response.");
                        }
                    } else {
                        addBotResponse("Error: " + response.message());
                    }
                }
            });

        } catch (JSONException e) {
            addBotResponse("Failed to build API request.");
        }
    }

    private void addBotResponse(String response) {
        runOnUiThread(() -> {
            conversationList.remove(conversationList.size() - 1); // Remove "TYPING..." message
            conversationList.add(new ChatBotModel(ChatBotModel.SENT_BY_BOT, response));
            chatBotAdapter.notifyDataSetChanged();
            binding.chatRecyclerView.smoothScrollToPosition(conversationList.size() - 1);
        });
    }
}
