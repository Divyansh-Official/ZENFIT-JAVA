package com.stream.zenfit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Adapter.ExerciseModeEquipmentAdapter;
import com.stream.zenfit.Modal.ExerciseModeEquipmentModal;
import com.stream.zenfit.databinding.ActivityAddExerciseVarietyBinding;
import com.stream.zenfit.databinding.ActivityAddSportsVarietyBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddExerciseVarietyActivity extends AppCompatActivity {

    ActivityAddExerciseVarietyBinding binding;
    FirebaseFirestore firebaseFirestore;
    ExerciseModeEquipmentAdapter exerciseModeEquipmentAdapter;
    ArrayList<ExerciseModeEquipmentModal> exerciseModeEquipmentModalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding = ActivityAddExerciseVarietyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();

        binding.backButton.setOnClickListener(v -> {
            finish();
        });

        setupSaveButton();
        setupChooseNumberOfEquipmentButton();
    }

    private void setupChooseNumberOfEquipmentButton() {

        binding.exerciseNumberOfEquipmentSelectButton.setOnClickListener(v -> {
            String exerciseName = binding.exerciseNameInput.getText().toString().trim();
            String numberOfEquipment = binding.exerciseNumberOfEquipmentInput.getText().toString().trim();

            final String noOfEquipment = numberOfEquipment;

            binding.exerciseNumberOfEquipmentInput.setText(noOfEquipment);

            if (!TextUtils.isEmpty(exerciseName)) {
                if (!TextUtils.isEmpty(numberOfEquipment) && Integer.parseInt(numberOfEquipment) > 0  && Integer.parseInt(numberOfEquipment) < 20) {
                    int count = Integer.parseInt(numberOfEquipment);

                    if (exerciseModeEquipmentModalList == null)
                    {
                        exerciseModeEquipmentModalList = new ArrayList<>();
                    }
                    else
                    {
                        exerciseModeEquipmentModalList.clear();
                        exerciseModeEquipmentModalList = new ArrayList<>();
                    }

                    int currentSize = exerciseModeEquipmentModalList.size();

                    // Add new items to the existing list
                    for (int i = 0; i < count; i++) {
                        exerciseModeEquipmentModalList.add(new ExerciseModeEquipmentModal(
                                " ", " ", " ", " ", " ", " ", " ", " ", " "));
                    }

                    // If adapter is null, initialize it once
                    if (exerciseModeEquipmentAdapter == null) {
                        ExerciseModeEquipmentAdapter adapter = new ExerciseModeEquipmentAdapter(this, exerciseModeEquipmentModalList, exerciseName);
                        binding.exerciseEquipmentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        binding.exerciseEquipmentDetailsRecyclerView.setAdapter(adapter);
                    } else {
                        // Notify adapter about data change
                        exerciseModeEquipmentAdapter.notifyDataSetChanged();
                    }

                    Log.d("EquipmentItems", count + " items added to the list");
                } else {
                    Toast.makeText(this, "Enter Number Of Items", LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please Enter Exercise Name", LENGTH_SHORT).show();
            }
        });

    }

    private void setupSaveButton() {
        String[] exerciseCategory = { "Strength & Muscle Building", "Cardio Workouts", "Functional & Mobility Workouts", "Fat Loss & Endurance Workouts" };
        ArrayAdapter<String> arrayCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exerciseCategory);
        arrayCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.exerciseCategoryChooseInput.setAdapter(arrayCategoryAdapter);

        String[] exerciseDifficultyLevel = { "Beginner", "Intermediate", "Advanced" };
        ArrayAdapter<String> arrayDifficultyLevelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exerciseDifficultyLevel);
        arrayDifficultyLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.exerciseDifficultyLeveInput.setAdapter(arrayDifficultyLevelAdapter);

        String[] exerciseMuscleTarget = { "Chest", "Back", "Shoulders", "Arms", "Legs", "Abs", "Full Body", "Cardio", "Other" };
        ArrayAdapter<String> arrayTargetMuscleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exerciseMuscleTarget);
        arrayTargetMuscleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.exerciseTargetedMuscleInput.setAdapter(arrayTargetMuscleAdapter);





        binding.saveButton.setOnClickListener(v -> {
            // Get text inputs
            String exerciseName = binding.exerciseNameInput.getText().toString().trim();
            String exerciseIconLink = binding.exerciseIconLinkInput.getText().toString().trim();
            String exerciseImageLink01 = binding.exerciseImageLink01Input.getText().toString().trim();
            String exerciseImageLink02 = binding.exerciseImageLink02Input.getText().toString().trim();
            String exerciseAverageTime = binding.exerciseAverageTimingInput.getText().toString().trim();
            String exerciseTarget = binding.exerciseTargetInput.getText().toString().trim();
            String exerciseBenefits = binding.exerciseBenefitsDetailsInput.getText().toString().trim();
            String exerciseTips = binding.exerciseTipsDetailsInput.getText().toString().trim();
            String exerciseRestTime = binding.exerciseAverageRestTimeInput.getText().toString().trim();
            String exerciseTags = binding.exerciseTagsInput.getText().toString().trim();
            String exerciseTutorialVideoLink01 = binding.exerciseVideoTutorial01Input.getText().toString().trim();
            String exerciseTutorialVideoLink02 = binding.exerciseVideoTutorial02Input.getText().toString().trim();
            String selectedCategory = binding.exerciseCategoryChooseInput.getSelectedItem().toString();
            String selectedMuscleTarget = binding.exerciseTargetedMuscleInput.getSelectedItem().toString();
            String selectedDifficultyLevel = binding.exerciseDifficultyLeveInput.getSelectedItem().toString();

            if (exerciseName.isEmpty()) {
                Toast.makeText(AddExerciseVarietyActivity.this, "Exercise Name is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> exerciseData = new HashMap<>();
            exerciseData.put("ExerciseName", exerciseName);
            exerciseData.put("ExerciseIconLink", exerciseIconLink);
            exerciseData.put("ExerciseImageLink01", exerciseImageLink01);
            exerciseData.put("ExerciseImageLink02", exerciseImageLink02);
            exerciseData.put("AverageTime", exerciseAverageTime);
            exerciseData.put("ExerciseTarget", exerciseTarget);
            exerciseData.put("ExerciseBenefits", exerciseBenefits);
            exerciseData.put("ExerciseTips", exerciseTips);
            exerciseData.put("ExerciseRestTime", exerciseRestTime);
            exerciseData.put("ExerciseTutorialVideoLink01", exerciseTutorialVideoLink01);
            exerciseData.put("ExerciseTutorialVideoLink02", exerciseTutorialVideoLink02);
            exerciseData.put("ExerciseTags", exerciseTags);
            if (selectedCategory == null || selectedCategory.trim().isEmpty()) {
                Toast.makeText(this, "Please select an Exercise Category", Toast.LENGTH_SHORT).show();
            } else if (selectedMuscleTarget == null || selectedMuscleTarget.trim().isEmpty()) {
                Toast.makeText(this, "Please select a Muscle Target", Toast.LENGTH_SHORT).show();
            } else if (selectedDifficultyLevel == null || selectedDifficultyLevel.trim().isEmpty()) {
                Toast.makeText(this, "Please select a Difficulty Level", Toast.LENGTH_SHORT).show();
            } else {
                // All fields are valid, add data to the HashMap
                exerciseData.put("ExerciseCategory", selectedCategory);
                exerciseData.put("ExerciseMuscleTarget", selectedMuscleTarget);
                exerciseData.put("ExerciseDifficultyLevel", selectedDifficultyLevel);

                firebaseFirestore.collection("Exercise Mode")
                        .document(exerciseName.toUpperCase())  // Using uppercase to standardize document names
                        .set(exerciseData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FireStoreSuccess", "Exercise details added for: " + exerciseName);
                            Toast.makeText(AddExerciseVarietyActivity.this, exerciseName.toUpperCase() + " details added!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FireStoreError", "Error adding document", e);
                            Toast.makeText(AddExerciseVarietyActivity.this, "Error adding exercise details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                // Proceed with the next step, e.g., saving data to Firebase
                Toast.makeText(this, "Exercise data saved successfully!", Toast.LENGTH_SHORT).show();
            }

        });
    }

        private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark2));
    }

}