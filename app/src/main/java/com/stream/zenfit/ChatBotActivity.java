package com.stream.zenfit;

import static android.app.ProgressDialog.show;
import static android.view.View.GONE;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stream.zenfit.Adapter.ChatBotAdapter;
import com.stream.zenfit.Database.ChatBotSQLiteDatabase;
import com.stream.zenfit.Modal.ChatBotModal;
import com.stream.zenfit.databinding.ActivityChatbotBinding;
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

public class ChatBotActivity extends AppCompatActivity {

    private ActivityChatbotBinding binding;
    private ChatBotSQLiteDatabase chatBotSQLiteDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private List<ChatBotModal> conversationList;
    private ChatBotAdapter chatBotAdapter;
    private DatabaseReference databaseReference;
    private final String apiURL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey = "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"; // Replace with a secure source
//    private final String apiKey = "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"; // Replace with a secure source
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        binding = ActivityChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatBotSQLiteDatabase = new ChatBotSQLiteDatabase(this);

        setupChangeStatusBarColor();
        initializeChat();
        setupSearch();

        binding.sendButton.setOnClickListener(v -> {
            String userMessage = binding.chatTextInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendUserMessage(userMessage);
                binding.welcomeText.setVisibility(GONE);
            }
            else {
                binding.chatTextInput.setError("Please Enter A Query");
            }
        });

        binding.clearChatButton.setOnClickListener(v -> clearChat());

        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.scroll_down_search_anim);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.scroll_up_search_anim);


        binding.searchChatButton.setOnClickListener(v -> {
            if (binding.autoCompleteSearchInput.getVisibility() == GONE) {
                binding.autoCompleteSearchInput.setVisibility(View.VISIBLE);
                binding.searchChatButton.setImageResource(R.drawable.cross_icon04);
                binding.autoCompleteSearchInput.startAnimation(slideDown);

            } else {
                binding.autoCompleteSearchInput.setVisibility(GONE);
                binding.searchChatButton.setImageResource(R.drawable.search_icon01);
                binding.autoCompleteSearchInput.startAnimation(slideUp);
            }
        });

    }

    private void setupSearch() {
        AutoCompleteTextView searchInput = binding.autoCompleteSearchInput;

        // Add text change listener to update suggestions dynamically
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    updateSearchSuggestions(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Set the OnItemClickListener for search suggestions
        searchInput.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMessage = (String) parent.getItemAtPosition(position);
            Toast.makeText(ChatBotActivity.this, "Selected: " + selectedMessage, Toast.LENGTH_SHORT).show();

            // Scroll to the selected message in the chat
            for (int i = 0; i < conversationList.size(); i++) {
                if (conversationList.get(i).getMessage().equals(selectedMessage)) {
                    binding.chatRecyclerView.smoothScrollToPosition(i);
                    break;
                }
            }
        });
    }

    private void updateSearchSuggestions(String query) {
        // Fetch matching messages from the database
        List<String> suggestions = chatBotSQLiteDatabase.searchMessages(query);

        // Update AutoCompleteTextView with new suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                suggestions
        );
        binding.autoCompleteSearchInput.setAdapter(adapter);
    }

    private void clearChat() {
        // Clear all data from SQLite database
        chatBotSQLiteDatabase.clearAllMessages();

        // Clear the conversation list in memory
        conversationList.clear();

        // Notify the adapter and update the UI
        chatBotAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Chat cleared!", Toast.LENGTH_SHORT).show();
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.Dark3));
    }

    private void sendUserMessage(String message) {
        binding.chatTextInput.setText("");
        conversationList.add(new ChatBotModal(ChatBotModal.SENT_BY_USER, message));
        chatBotSQLiteDatabase.insertMessage(ChatBotModal.SENT_BY_USER, message); // Save to database
        chatBotAdapter.notifyDataSetChanged();
        binding.chatRecyclerView.smoothScrollToPosition(conversationList.size() - 1);

        // Call OpenAI API
        callOpenAIAPI(message, 0);
    }

    private void addBotResponse(String response) {
        runOnUiThread(() -> {
            conversationList.remove(conversationList.size() - 1); // Remove "TYPING..." message
            conversationList.add(new ChatBotModal(ChatBotModal.SENT_BY_BOT, response));
            chatBotSQLiteDatabase.insertMessage(ChatBotModal.SENT_BY_BOT, response); // Save to database
            chatBotAdapter.notifyDataSetChanged();
            binding.chatRecyclerView.smoothScrollToPosition(conversationList.size() - 1);
        });
    }

    private void callOpenAIAPI(String userMessage, int retryCount) {
        conversationList.add(new ChatBotModal(ChatBotModal.SENT_BY_BOT, "TYPING..."));
        chatBotAdapter.notifyDataSetChanged();

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("model", "gpt-4o-mini");
            requestJson.put("temperature", 1);

            JSONArray messages = new JSONArray();
            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "user");
            messageObject.put("content", userMessage);
            messages.put(messageObject);

            requestJson.put("messages", messages);

            RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
            Request request = new Request.Builder()
                    .url(apiURL)
                    .addHeader("Authorization", "Bearer " + apiKey.trim())
                    .addHeader("Content-Type", "application/json")
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
                            addBotResponse("Failed to parse response: " + e.getMessage());
                        }
                    } else if (response.code() == 429) {
                        if (retryCount < 2) {  // Max number of retries
                            long waitTime = (long) (Math.pow(2, retryCount) * 1000); // Exponential backoff
                            addBotResponse("Too many requests - retrying in " + waitTime / 1000 + " seconds...");
                            new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    callOpenAIAPI(userMessage, retryCount + 1), waitTime);
                        } else {
                            addBotResponse("Error: Too Many Requests (429)");
                        }
                    } else if (response.code() == 404) {
                        addBotResponse("Error: Not Found (404) - Check the API endpoint URL.");
                    } else {
                        addBotResponse("Error: " + response.message() + " (" + response.code() + ")");
                    }
                }
            });

        } catch (JSONException e) {
            addBotResponse("Failed to build API request: " + e.getMessage());
        }
    }

    private void initializeChat() {
        conversationList = new ArrayList<>();
        chatBotSQLiteDatabase = new ChatBotSQLiteDatabase(this);
        conversationList.addAll(chatBotSQLiteDatabase.getAllMessages()); // Load messages from database

        chatBotAdapter = new ChatBotAdapter(this, conversationList);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(chatBotAdapter);
    }

}