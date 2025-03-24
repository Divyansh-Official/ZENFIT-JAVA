package com.stream.zenfit;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Modal.ExerciseModeEquipmentModal;
import com.stream.zenfit.Modal.ExerciseModeModal;
import com.stream.zenfit.databinding.ActivityExerciseRepresentationSplashScreenBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ExerciseRepresentationSplashScreenActivity extends AppCompatActivity {

    ActivityExerciseRepresentationSplashScreenBinding binding;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    int[] randomVideos = { R.raw.biceps_video_01, R.raw.triceps_video_01 };
    ExerciseModeEquipmentModal exerciseModeEquipmentModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityExerciseRepresentationSplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setupChangeStatusBarColor();
        setupExerciseDetailsCall();
//
        Random random = new Random();
        int randomIndex = random.nextInt(randomVideos.length);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + randomVideos[randomIndex]);

        binding.exerciseRepresentationSplashVideoView.setVideoURI(videoUri);
        binding.exerciseRepresentationSplashVideoView.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent signupIntent = new Intent(ExerciseRepresentationSplashScreenActivity.this, ExerciseRepresentationActivity.class);
                ExerciseRepresentationSplashScreenActivity.this.startActivity(signupIntent);
                ExerciseRepresentationSplashScreenActivity.this.finish();
            }
        }, 2000);
    }

    private void setupExerciseDetailsCall() {

        Intent receiveIntent = getIntent();
        String exerciseName = receiveIntent.getStringExtra("exerciseName");
        String exerciseIconLink = receiveIntent.getStringExtra("exerciseIconLink");
        String exerciseImageLink01 = receiveIntent.getStringExtra("exerciseImageLink01");
        String exerciseImageLink02 = receiveIntent.getStringExtra("exerciseImageLink02");
        String exerciseDifficultyLevel = receiveIntent.getStringExtra("exerciseDifficultyLevel");
        String exerciseDescription = receiveIntent.getStringExtra("exerciseDescription");
        String exerciseVideoLink01 = receiveIntent.getStringExtra("exerciseVideoLink01");
        String exerciseVideoLink02 = receiveIntent.getStringExtra("exerciseVideoLink02");

        SpannableString spannable = new SpannableString(exerciseName.toUpperCase() + " SECTION");
        binding.welcomeText.setText(spannable);

        Toast.makeText(this, "Exercise Name: " + exerciseName, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Exercise Icon Link: " + exerciseIconLink, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Exercise Image Link 01: " + exerciseImageLink01, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Exercise Image Link 02: " + exerciseImageLink02, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Exercise Difficulty Level: " + exerciseDifficultyLevel, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Exercise Description: " + exerciseDescription, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Exercise Video Link 01: " + exerciseVideoLink01, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Exercise Video Link 02: " + exerciseVideoLink02, Toast.LENGTH_SHORT).show();

        Log.d("ExerciseRepresentation", "Exercise Name: " + exerciseName);
        Log.d("ExerciseRepresentation", "Exercise Icon Link: " + exerciseIconLink);
        Log.d("ExerciseRepresentation", "Exercise Image Link 01: " + exerciseImageLink01);
        Log.d("ExerciseRepresentation", "Exercise Image Link 02: " + exerciseImageLink02);
        Log.d("ExerciseRepresentation", "Exercise Difficulty Level: " + exerciseDifficultyLevel);
        Log.d("ExerciseRepresentation", "Exercise Description: " + exerciseDescription);
        Log.d("ExerciseRepresentation", "Exercise Video Link 01: " + exerciseVideoLink01);
        Log.d("ExerciseRepresentation", "Exercise Video Link 02: " + exerciseVideoLink02);

//        Intent sendIntent = new Intent(ExerciseRepresentationSplashScreenActivity.this, ExerciseRepresentationActivity.class);
//        sendIntent.putExtra("exerciseName", exerciseName);
//        sendIntent.putExtra("exerciseIconLink", exerciseIconLink);
//        sendIntent.putExtra("exerciseImageLink01", exerciseImageLink01);
//        sendIntent.putExtra("exerciseImageLink02", exerciseImageLink02);
//        sendIntent.putExtra("exerciseDifficultyLevel", exerciseDifficultyLevel);
//        sendIntent.putExtra("exerciseDescription", exerciseDescription);
//        sendIntent.putExtra("exerciseVideoLink01", exerciseVideoLink01);
//        sendIntent.putExtra("exerciseVideoLink02", exerciseVideoLink02);
//        startActivity(sendIntent);

        setupEquipmentDetailsCall(exerciseName);

    }

    private void setupEquipmentDetailsCall(String exerciseName) {

        firebaseFirestore = FirebaseFirestore.getInstance();

        CollectionReference equipmentCollection = firebaseFirestore.collection("Exercise Mode")
                .document(exerciseName)
                .collection("Equipment used in " + exerciseName);

        equipmentCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String equipmentId = document.getId(); // Equipment document ID
                    Log.d("FireStoreData", "Equipment: " + equipmentId);

                    // Fetch subcollections inside this document
                    document.getReference().get().addOnSuccessListener(documentSnapshot -> {

                        String EquipmentName = documentSnapshot.getString("Equipment Name");
                        String EquipmentIconLink = documentSnapshot.getString("Equipment Icon Link");
                        String EquipmentImageLink01 = documentSnapshot.getString("Equipment Image Link 01");
                        String EquipmentImageLink02 = documentSnapshot.getString("Equipment Image Link 02");
                        String EquipmentTutorialVideoLink01 = documentSnapshot.getString("Equipment Tutorial Video Link 01");
                        String EquipmentTutorialVideoLink02 = documentSnapshot.getString("Equipment Tutorial Video Link 02");
                        String EquipmentSets = documentSnapshot.getString("Equipment Sets");
                        String EquipmentReps = documentSnapshot.getString("Equipment Reps");
                        String EquipmentDescription = documentSnapshot.getString("Equipment Description");

                        Map<String, String> equipmentData = new HashMap<>();
                        equipmentData.put("Equipment Name", EquipmentName);
                        equipmentData.put("Equipment Icon Link", EquipmentIconLink);
                        equipmentData.put("Equipment Image Link 01", EquipmentImageLink01);
                        equipmentData.put("Equipment Image Link 02", EquipmentImageLink02);
                        equipmentData.put("Equipment Tutorial Video Link 01", EquipmentTutorialVideoLink01);
                        equipmentData.put("Equipment Tutorial Video Link 02", EquipmentTutorialVideoLink02);
                        equipmentData.put("Equipment Sets", EquipmentSets);
                        equipmentData.put("Equipment Reps", EquipmentReps);
                        equipmentData.put("Equipment Description", EquipmentDescription);



                    });
                }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

}