package com.stream.zenfit;

import static android.app.ProgressDialog.show;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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";
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
        setupWallpaper();

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
            if (binding.autoCompleteSearch.getVisibility() == GONE) {
                binding.autoCompleteSearch.setVisibility(VISIBLE);
                binding.searchChatButton.setImageResource(R.drawable.cross_icon03);
                binding.autoCompleteSearch.startAnimation(slideDown);

            } else {
                binding.autoCompleteSearch.setVisibility(GONE);
                binding.autoCompleteSearchInput.setText("");
                closeKeyboard();
                binding.searchChatButton.setImageResource(R.drawable.search_icon01);
                binding.autoCompleteSearch.startAnimation(slideUp);
            }
        });
    }

    private void setupWallpaper() {
        binding.wallpaperChangeButton.setOnClickListener(v -> {
            Toast.makeText(this, "This Feature Will Be Available Soon ...", Toast.LENGTH_SHORT).show();
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus(); // Get the currently focused view
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // Hide the keyboard
        }
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
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.Dark6));
    }

    // List of allowed topics (Sports, Health, Fitness)
    private static final List<String> ALLOWED_KEYWORDS = Arrays.asList(
            "hey", "hello",

            // General Sports
            "sports", "athletics", "football", "cricket", "tennis", "basketball", "volleyball",
            "badminton", "hockey", "rugby", "golf", "cycling", "swimming", "boxing", "wrestling",
            "karate", "judo", "mma", "taekwondo", "archery", "table tennis", "snooker", "skiing",
            "surfing", "skating", "gymnastics", "marathon", "triathlon", "rowing", "fencing",

            // Fitness & Exercise
            "fitness", "workout", "exercise", "strength training", "cardio", "weightlifting",
            "bodybuilding", "calisthenics", "stretching", "HIIT", "pilates", "crossfit", "endurance training",
            "resistance training", "plyometrics", "functional training", "powerlifting", "mobility training",
            "core exercises", "recovery exercises",

            // Gym & Equipment
            "gym", "dumbbells", "barbell", "kettlebell", "treadmill", "rowing machine", "elliptical",
            "leg press", "bench press", "squat rack", "pull-up bar", "yoga mat", "exercise bike",
            "resistance bands", "battle ropes", "medicine ball",

            // Health & Wellness
            "health", "wellness", "mental health", "physical health", "self-care", "hydration",
            "immune system", "posture correction", "sleep quality", "stress management",
            "muscle recovery", "rest days", "mindfulness", "meditation", "breathing exercises",

            // Nutrition & Diet
            "nutrition", "diet", "protein", "carbohydrates", "fats", "vitamins", "minerals",
            "calories", "macros", "keto diet", "vegan diet", "paleo diet", "intermittent fasting",
            "meal planning", "supplements", "electrolytes", "hydration", "sports drinks", "energy bars",
            "post-workout meals", "pre-workout meals", "fiber", "antioxidants",

            // Injuries & Recovery
            "sports injuries", "muscle strain", "sprain", "ligament tear", "ACL injury",
            "hamstring injury", "tennis elbow", "shin splints", "stress fractures", "concussion",
            "ankle sprain", "shoulder injury", "back pain", "joint pain", "knee pain",
            "rehabilitation", "physical therapy", "chiropractic care", "RICE method",
            "massage therapy", "foam rolling", "ice therapy", "heat therapy", "sports medicine",

            // Body & Performance Optimization
            "muscle growth", "fat loss", "weight gain", "weight loss", "metabolism", "VO2 max",
            "body fat percentage", "muscle endurance", "strength gains", "agility training",
            "speed training", "reaction time", "hand-eye coordination", "posture improvement",
            "balance exercises", "injury prevention", "sports performance", "muscle soreness",

            // Yoga & Recovery
            "yoga", "meditation", "pranayama", "asanas", "yoga poses", "yoga for flexibility",
            "yoga for relaxation", "hot yoga", "restorative yoga", "yoga for recovery",
            "yoga for athletes", "breathing exercises", "mindfulness", "holistic health"
    );

    private void sendUserMessage(String message) {
        binding.chatTextInput.setText("");

        // Add user message to chat UI and database
        conversationList.add(new ChatBotModal(ChatBotModal.SENT_BY_USER, message));
        chatBotSQLiteDatabase.insertMessage(ChatBotModal.SENT_BY_USER, message);

        // Notify adapter immediately to ensure the message is displayed
        chatBotAdapter.notifyDataSetChanged();
        binding.chatRecyclerView.smoothScrollToPosition(conversationList.size() - 1);

        // Check if the query is related to sports, health, fitness or specific questions
        if (isAllowedQuery(message)) {
            callOpenRouterAPI(message);
        } else if (isQueryAboutAI(message)) {
            addBotResponse("I am ZenFit AI, here to help you with health and fitness.");
        } else if (isQueryAboutDeveloper(message)) {
            addBotResponse("I am developed by Divyansh Tiwari.");
        } else {
            addBotResponse("Sorry, I can't assist you with this query.");
        }
    }

    // Function to check if the query is related to AI name
    private boolean isQueryAboutAI(String message) {
        String lowerCaseMessage = message.toLowerCase(); // Convert the message to lowercase
        return lowerCaseMessage.contains("name")
                && (lowerCaseMessage.contains("ai")
                || lowerCaseMessage.contains("what are you")
                || lowerCaseMessage.contains("who are you")
                || lowerCaseMessage.contains("what is your name")
                || lowerCaseMessage.contains("who are you")
                || lowerCaseMessage.contains("tell me your name")
                || lowerCaseMessage.contains("identify yourself")
                || lowerCaseMessage.contains("what's your name")
                || lowerCaseMessage.contains("who is this")
                || lowerCaseMessage.contains("who am i talking to"));
    }


    // Function to check if the query is related to developer
    private boolean isQueryAboutDeveloper(String message) {
        String lowerCaseMessage = message.toLowerCase();
        return lowerCaseMessage.contains("developer")
                || lowerCaseMessage.contains("who made you")
                || lowerCaseMessage.contains("who created you")
                || lowerCaseMessage.contains("who is the creator")
                || lowerCaseMessage.contains("who is the developer")
                || lowerCaseMessage.contains("who developed you")
                || lowerCaseMessage.contains("creator")
                || lowerCaseMessage.contains("made you")
                || lowerCaseMessage.contains("created you")
                || lowerCaseMessage.contains("who built you")
                || lowerCaseMessage.contains("who is behind you")
                || lowerCaseMessage.contains("who is the author");
    }


    // Function to get the current time
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }


    // Function to check if the query is related to sports, health, or fitness
    private boolean isAllowedQuery(String message) {
        String lowerCaseMessage = message.toLowerCase();
        for (String keyword : ALLOWED_KEYWORDS) {
            if (lowerCaseMessage.contains(keyword)) {
                return true; // Query is allowed
            }
        }
        return false; // Query is not allowed
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

    private void callOpenRouterAPI(String userMessage) {
        conversationList.add(new ChatBotModal(ChatBotModal.SENT_BY_BOT, "TYPING..."));
        binding.botReplyLoadingAnimation.setVisibility(View.VISIBLE);
        chatBotAdapter.notifyDataSetChanged();

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("model", "openai/gpt-3.5-turbo");

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", userMessage));

            requestJson.put("messages", messages);

            Log.d("API Request", "Payload: " + requestJson.toString());

            RequestBody requestBody = RequestBody.create(requestJson.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY.trim())
                    .addHeader("HTTP-Referer", "<YOUR_SITE_URL>")
                    .addHeader("X-Title", "<YOUR_SITE_NAME>")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        addBotResponse("Failed to connect: " + e.getMessage());
                        binding.botReplyLoadingAnimation.setVisibility(View.GONE);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        try {
                            if (!response.isSuccessful()) {
                                addBotResponse("Error: " + response.message() + " (" + response.code() + ")");
                            } else {
                                String responseBody = response.body().string();
                                Log.d("API Response", "Body: " + responseBody);

                                JSONObject responseJson = new JSONObject(responseBody);
                                JSONArray choices = responseJson.optJSONArray("choices");

                                if (choices != null && choices.length() > 0) {
                                    JSONObject messageObject = choices.getJSONObject(0).optJSONObject("message");
                                    if (messageObject != null) {
                                        String botMessage = messageObject.optString("content", "No response");
                                        addBotResponse(botMessage.trim());
                                    } else {
                                        addBotResponse("Error: No valid response from AI.");
                                    }
                                } else {
                                    addBotResponse("Error: No valid response from AI.");
                                }
                            }
                        } catch (JSONException | IOException e) {
                            addBotResponse("Failed to parse response: " + e.getMessage());
                        } finally {
                            binding.botReplyLoadingAnimation.setVisibility(View.GONE);
                        }
                    });
                }
            });

        } catch (JSONException e) {
            runOnUiThread(() -> addBotResponse("Failed to build API request: " + e.getMessage()));
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