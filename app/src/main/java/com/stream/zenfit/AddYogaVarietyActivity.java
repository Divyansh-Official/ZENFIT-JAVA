package com.stream.zenfit;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.databinding.ActivityAddSportsVarietyBinding;

import java.util.HashMap;
import java.util.Map;

public class AddYogaVarietyActivity extends AppCompatActivity {

    ActivityAddSportsVarietyBinding binding;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding = ActivityAddSportsVarietyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();

        binding.backButton.setOnClickListener(v -> {
            finish();
        });

        binding.sportsDetailsSaveButton.setOnClickListener(v -> {
            String name = binding.sportsNameInput.getText().toString().trim();
            String iconLink = binding.sportsIconLinkInput.getText().toString().trim();
            String imageLink = binding.sportsImageLinkInput.getText().toString().trim();
            String averageWeight = binding.sportsAverageWeightInput.getText().toString().trim();
            String averageHeight = binding.sportsAverageHeightInput.getText().toString().trim();
            String averageCalories = binding.sportsAverageCaloriesIntakeInput.getText().toString().trim();
            String weightFromCriteria = binding.sportsWeightFromCriteriaInput.getText().toString().trim();
            String heightFromCriteria = binding.sportsHeightFromCriteriaInput.getText().toString().trim();
            String caloriesFromCriteria = binding.sportsCaloriesFromCriteriaInput.getText().toString().trim();
            String weightToCriteria = binding.sportsWeightToCriteriaInput.getText().toString().trim();
            String heightToCriteria = binding.sportsHeightToCriteriaInput.getText().toString().trim();
            String caloriesToCriteria = binding.sportsCaloriesToCriteriaInput.getText().toString().trim();
            String ytLinkLessWeight = binding.sportsYTLinkLessWeightInput.getText().toString().trim();
            String ytLinkLessHeight = binding.sportsYTLinkLessHeightInput.getText().toString().trim();
            String ytLinkLessCalories = binding.sportsYTLinkLessCaloriesIntakeInput.getText().toString().trim();
            String informationTitle = binding.sportsInformationTitleInput.getText().toString().trim();
            String informationText01 = binding.sportsInformationText01Input.getText().toString().trim();
            String informationText02 = binding.sportsInformationText02Input.getText().toString().trim();
            String informationText03 = binding.sportsInformationText03Input.getText().toString().trim();
            String informationText04 = binding.sportsInformationText04Input.getText().toString().trim();
            String informationText05 = binding.sportsInformationText05Input.getText().toString().trim();
            String tutorialLink01 = binding.sportsYTSuggestionTutorialVideos01Input.getText().toString().trim();
            String tutorialLink02 = binding.sportsYTSuggestionTutorialVideos02Input.getText().toString().trim();
            String tutorialLink03 = binding.sportsYTSuggestionTutorialVideos03Input.getText().toString().trim();
            String tutorialLink04 = binding.sportsYTSuggestionTutorialVideos04Input.getText().toString().trim();
            String tutorialLink05 = binding.sportsYTSuggestionTutorialVideos05Input.getText().toString().trim();

            // Ensure all fields are filled
            if (name.isEmpty() || iconLink.isEmpty() || imageLink.isEmpty() || averageWeight.isEmpty() ||
                    averageHeight.isEmpty() || averageCalories.isEmpty() || weightFromCriteria.isEmpty() ||
                    heightFromCriteria.isEmpty() || caloriesFromCriteria.isEmpty() || weightToCriteria.isEmpty() ||
                    heightToCriteria.isEmpty() || caloriesToCriteria.isEmpty() || ytLinkLessWeight.isEmpty() ||
                    ytLinkLessHeight.isEmpty() || ytLinkLessCalories.isEmpty() || informationTitle.isEmpty() ||
                    informationText01.isEmpty() || informationText02.isEmpty() || informationText03.isEmpty() ||
                    informationText04.isEmpty() || informationText05.isEmpty() || tutorialLink01.isEmpty() ||
                    tutorialLink02.isEmpty() || tutorialLink03.isEmpty() || tutorialLink04.isEmpty() || tutorialLink05.isEmpty()) {

                // Show a message if any required field is empty
                Toast.makeText(this, "Please fill in all fields before proceeding", Toast.LENGTH_SHORT).show();
            }
            else {
                // If all fields are valid, call the setup function
                setupUpdateTheDataInAllUsersAccount(
                        name, iconLink, imageLink, averageWeight, averageHeight, averageCalories,
                        weightFromCriteria, heightFromCriteria, caloriesFromCriteria,
                        weightToCriteria, heightToCriteria, caloriesToCriteria,
                        ytLinkLessWeight, ytLinkLessHeight, ytLinkLessCalories,
                        informationTitle, informationText01, informationText02, informationText03, informationText04, informationText05,
                        tutorialLink01, tutorialLink02, tutorialLink03, tutorialLink04, tutorialLink05
                );
            }
        });

    }
    private void setupUpdateTheDataInAllUsersAccount(
            String name, String iconLink, String imageLink, String averageWeight, String averageHeight, String averageCalories,
            String weightFromCriteria, String heightFromCriteria, String caloriesFromCriteria,
            String weightToCriteria, String heightToCriteria, String caloriesToCriteria,
            String ytLinkLessWeight, String ytLinkLessHeight, String ytLinkLessCalories,
            String informationTitle, String informationText01, String informationText02, String informationText03, String informationText04, String informationText05,
            String tutorialLink01, String tutorialLink02, String tutorialLink03, String tutorialLink04, String tutorialLink05
    ) {
        Map<String, Object> sportsItem = new HashMap<>();
        sportsItem.put("SportsName", name);
        sportsItem.put("SportsIconLink", iconLink);
        sportsItem.put("SportsImageLink", imageLink);
        sportsItem.put("AverageWeight", averageWeight);
        sportsItem.put("AverageHeight", averageHeight);
        sportsItem.put("AverageCalories", averageCalories);
        sportsItem.put("WeightFromCriteria", weightFromCriteria);
        sportsItem.put("HeightFromCriteria", heightFromCriteria);
        sportsItem.put("CaloriesFromCriteria", caloriesFromCriteria);
        sportsItem.put("WeightToCriteria", weightToCriteria);
        sportsItem.put("HeightToCriteria", heightToCriteria);
        sportsItem.put("CaloriesToCriteria", caloriesToCriteria);
        sportsItem.put("YTLinkLessWeight", ytLinkLessWeight);
        sportsItem.put("YTLinkLessHeight", ytLinkLessHeight);
        sportsItem.put("YTLinkLessCalories", ytLinkLessCalories);
        sportsItem.put("InformationTitle", informationTitle);
        sportsItem.put("InformationText01", informationText01);
        sportsItem.put("InformationText02", informationText02);
        sportsItem.put("InformationText03", informationText03);
        sportsItem.put("InformationText04", informationText04);
        sportsItem.put("InformationText05", informationText05);
        sportsItem.put("TutorialLink01", tutorialLink01);
        sportsItem.put("TutorialLink02", tutorialLink02);
        sportsItem.put("TutorialLink03", tutorialLink03);
        sportsItem.put("TutorialLink04", tutorialLink04);
        sportsItem.put("TutorialLink05", tutorialLink05);

        firebaseFirestore.collection("Users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String userId = document.getId();

                            // Update the sports item for each user
                            firebaseFirestore.collection("Sports Mode").document(name)
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
        window.setStatusBarColor(getResources().getColor(R.color.Dark2));
    }

}