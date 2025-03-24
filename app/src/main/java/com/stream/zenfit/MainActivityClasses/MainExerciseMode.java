package com.stream.zenfit.MainActivityClasses;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.animation.content.Content;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Adapter.ExerciseModeAdapter;
import com.stream.zenfit.MainActivity;
import com.stream.zenfit.Modal.ExerciseModeModal;
import com.stream.zenfit.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainExerciseMode{

    ActivityMainBinding binding;
    FirebaseFirestore firebaseFirestore;
    ExerciseModeAdapter exerciseModeAdapter;
    Context context;
    private List<ExerciseModeModal> exerciseList = new ArrayList<>();


    public MainExerciseMode(Context context, ActivityMainBinding binding){
        this.context = context;
        this.binding = binding;
    }

    public void setupExerciseRequirements() {

        setupExerciseModeRecyclerView();

    }

    private void setupExerciseModeRecyclerView() {

        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference exerciseCollection = firebaseFirestore.collection("Exercise Mode");

        exerciseCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        StringBuilder documentNames = new StringBuilder();

                        // Loop through each document to fetch individual fields
                        for (DocumentSnapshot document : task.getResult()) {
                            String documentName = document.getId();

                            documentNames.append(documentName).append("\n");

                            Map<String, Object> documentData = document.getData();
                            Log.d("ExerciseMode", "Document Name: " + documentName);
                            Log.d("ExerciseMode", "Document Data: " + documentData);

                            if (documentData != null) {

                                String exerciseName = document.getString("ExerciseName");
                                String exerciseIconLink = document.getString("ExerciseIconLink");
                                String exerciseImageLink01 = document.getString("ExerciseImageLink01");
                                String exerciseImageLink02 = document.getString("ExerciseImageLink02");
                                String exerciseTiming = document.getString("AverageTime");
                                String exerciseDifficultyLevel = document.getString("ExerciseDifficultyLevel");
                                String exerciseCategory = document.getString("ExerciseCategory");
                                String exerciseMuscleTarget = document.getString("ExerciseMuscleTarget");
                                String exerciseTags = document.getString("ExerciseTags");
                                String exerciseTarget = document.getString("ExerciseTarget");
                                String exerciseBenefits = document.getString("ExerciseBenefits");
                                String exerciseTips = document.getString("ExerciseTips");
                                String exerciseRestTime = document.getString("ExerciseRestTime");

                                Log.d("ExerciseMode", "Exercise Name: " + exerciseName);
                                Log.d("ExerciseMode", "Exercise Icon Link: " + exerciseIconLink);
                                Log.d("ExerciseMode", "Exercise Image Link 01: " + exerciseImageLink01);
                                Log.d("ExerciseMode", "Exercise Image Link 02: " + exerciseImageLink02);
                                Log.d("ExerciseMode", "Exercise Timing: " + exerciseTiming);
                                Log.d("ExerciseMode", "Exercise Difficulty Level: " + exerciseDifficultyLevel);
                                Log.d("ExerciseMode", "Exercise Category: " + exerciseCategory);
                                Log.d("ExerciseMode", "Exercise Muscle Target: " + exerciseMuscleTarget);
                                Log.d("ExerciseMode", "Exercise Tags: " + exerciseTags);
                                Log.d("ExerciseMode", "Exercise Target: " + exerciseTarget);
                                Log.d("ExerciseMode", "Exercise Benefits: " + exerciseBenefits);
                                Log.d("ExerciseMode", "Exercise Tips: " + exerciseTips);
                                Log.d("ExerciseMode", "Exercise Rest Time: " + exerciseRestTime);

                                HashMap<String, String> exerciseDetails = new HashMap<>();

                                ExerciseModeModal newExerciseMode = new ExerciseModeModal( exerciseName, exerciseIconLink, exerciseImageLink01, exerciseImageLink02, exerciseTiming, exerciseDifficultyLevel, exerciseCategory, exerciseTags, exerciseTarget, exerciseMuscleTarget, exerciseBenefits, exerciseTips, exerciseRestTime );

                                boolean isAdded = exerciseList.add(newExerciseMode);

                                if (isAdded) {
                                    Log.d("ExerciseMode", "Item successfully added: " + exerciseName);
                                    loadDataExercise();
                                } else {
                                    Log.e("ExerciseMode", "Failed to add item: " + exerciseName);
                                }
                            }

                        }

                        Log.d("ExerciseMode", "Fetched Document Names:\n" + documentNames.toString());

                    }
                    else {
                        Log.d("SportsMExerciseModeFunctionality", "No documents found or query failed.");
                        Toast.makeText(context, "No Sports Data Found In The Database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ExerciseModeFunctionality", "Error retrieving documents", e);
                    Toast.makeText(context, "Error retrieving documents", Toast.LENGTH_SHORT).show();
                });

    }

    private void loadDataExercise() {

        binding.exerciseMode.exerciseModeOptionsRecyclerViewShimmerEffectPlaceHolder.setVisibility(View.GONE);
        binding.exerciseMode.exerciseModeOptions.setVisibility(View.GONE);

        Log.d("ExerciseModeLoadData", "exerciseList size: " + exerciseList.size());
        Log.d("ExerciseModeLoadData", "exerciseList data: " + exerciseList.toString());

        // Pass data to the adapter
        exerciseModeAdapter = new ExerciseModeAdapter(context, exerciseList);

        if (exerciseModeAdapter != null) {
            Log.d("ExerciseModeLoadData", "exerciseModeAdapter is successfully set.");
        } else {
            Log.e("ExerciseModeLoadData", "Failed to set exerciseModeAdapter.");
        }

        Log.d("ExerciseModeLoadData", "Data Loaded");
        Log.d("ExerciseModeLoadData", "Data Size: " + exerciseList.size());
        Log.d("ExerciseModeLoadData", "Data: " + exerciseList.toString());

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.exerciseMode.exerciseModeOptionsRecyclerView.setLayoutManager(layoutManager);

        binding.exerciseMode.exerciseModeOptionsRecyclerView.setAdapter(exerciseModeAdapter);

        if (exerciseModeAdapter != null) {
            exerciseModeAdapter.notifyDataSetChanged();
        }

        if (binding.exerciseMode.exerciseModeOptionsRecyclerView != null) {
            Log.d("ExerciseModeLoadData", "exerciseModeOptionsRecyclerView is successfully set.");
            binding.exerciseMode.exerciseModeOptions.setVisibility(View.VISIBLE);
            binding.exerciseMode.exerciseModeOptionsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            Log.e("ExerciseModeLoadData", "Failed to set exerciseModeOptionsRecyclerView.");
        }
    }

}
