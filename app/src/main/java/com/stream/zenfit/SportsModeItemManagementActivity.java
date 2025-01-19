package com.stream.zenfit;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.databinding.ActivitySportsModeItemManagementBinding;

import java.util.HashMap;
import java.util.Map;

public class SportsModeItemManagementActivity extends AppCompatActivity {

    ActivitySportsModeItemManagementBinding binding;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding = ActivitySportsModeItemManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();

        binding.sportsDetailsSaveButton.setOnClickListener(v -> {
            String name = binding.sportsNameInput.getText().toString().trim();
            String iconLink = binding.sportsIconLinkInput.getText().toString().trim();
            String imageLink = binding.sportsImageLinkInput.getText().toString().trim();
            String averageWeight = binding.sportsIAverageWeightInput.getText().toString().trim();
            String averageHeight = binding.sportsAverageHeightInput.getText().toString().trim();
            String averageCalories = binding.sportsAverageCaloriesIntakeInput.getText().toString().trim();
            String ytLinkLessWeight = binding.sportsYTLinkLessWeightInput.getText().toString().trim();
            String ytLinkMoreWeight = binding.sportsYTLinkMoreWeightInput.getText().toString().trim();
            String ytLinkLessHeight = binding.sportsYTLinkLessHeightInput.getText().toString().trim();
            String ytLinkMoreHeight = binding.sportsYTLinkMoreHeightInput.getText().toString().trim();
            String ytLinkLessCalories = binding.sportsYTLinkLessCaloriesIntakeInput.getText().toString().trim();
            String ytLinkMoreCalories = binding.sportsYTLinkMoreCaloriesIntakeInput.getText().toString().trim();

            setupUpdateTheDataInAllUsersAccount(name, iconLink, imageLink, averageWeight, averageHeight, averageCalories, ytLinkLessWeight, ytLinkMoreWeight, ytLinkLessHeight, ytLinkMoreHeight, ytLinkLessCalories, ytLinkMoreCalories);
        });

    }

    private void setupUpdateTheDataInAllUsersAccount(String name, String iconLink, String imageLink, String averageWeight, String averageHeight, String averageCalories, String ytLinkLessWeight, String ytLinkMoreWeight, String ytLinkLessHeight, String ytLinkMoreHeight, String ytLinkLessCalories, String ytLinkMoreCalories) {
        Map<String, Object> sportsItem = new HashMap<>();
        sportsItem.put("SportsName", name);
        sportsItem.put("SportsIconLink", iconLink);
        sportsItem.put("SportsImageLink", imageLink);
        sportsItem.put("AverageWeight", averageWeight);
        sportsItem.put("AverageHeight", averageHeight);
        sportsItem.put("AverageCalories", averageCalories);
        sportsItem.put("YTLinkLessWeight", ytLinkLessWeight);
        sportsItem.put("YTLinkMoreWeight", ytLinkMoreWeight);
        sportsItem.put("YTLinkLessHeight", ytLinkLessHeight);
        sportsItem.put("YTLinkMoreHeight", ytLinkMoreHeight);
        sportsItem.put("YTLinkLessCalories", ytLinkLessCalories);
        sportsItem.put("YTLinkMoreCalories", ytLinkMoreCalories);

        firebaseFirestore.collection("Users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null)
                    {
                        for (DocumentSnapshot document : task.getResult()) {
                            String userId = document.getId();

                            // Update the sportsitem child for each user
                            firebaseFirestore.collection("Users").document(userId)
                                    .collection("Sports Mode").document("SportsItem")
                                    .collection(name).document(name + " Details")
                                    .set(sportsItem)
                                    .addOnSuccessListener(unused -> {
                                        Log.d("SportsItemUpdate", "Updated Sports Item For User: " + userId);
                                        Toast.makeText(this, "Updated Sports Item For User: " + userId, Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("SportsItemUpdate", "Failed to update Sports Item For User: " + userId, e);
                                        Toast.makeText(this, "Failed to update Sports Item For User: " + userId, Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                });

    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

}