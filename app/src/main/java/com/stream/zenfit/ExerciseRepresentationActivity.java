package com.stream.zenfit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.stream.zenfit.databinding.ActivityExerciseRepresentationBinding;

import org.eazegraph.lib.models.PieModel;

public class ExerciseRepresentationActivity extends AppCompatActivity {

    ActivityExerciseRepresentationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityExerciseRepresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigationFunctionality();
        setupReceiveExerciseDetails();

    }

    private void setupNavigationFunctionality() {

        binding.exerciseRepresentationHeader.exerciseMenuButton.setOnClickListener(v -> {

            if (binding.exerciseRepresentationHeader.exerciseNavigation.getVisibility() == View.VISIBLE) {
                binding.exerciseRepresentationHeader.exerciseMenuButton.setImageResource(R.drawable.menu_icon02);
                Animation scrollLeftAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_navigation_drawer);
                binding.exerciseRepresentationHeader.exerciseNavigation.startAnimation(scrollLeftAnim);
                binding.exerciseRepresentationHeader.exerciseNavigation.setVisibility(View.GONE);
            }
            else {
                binding.exerciseRepresentationHeader.exerciseMenuButton.setImageResource(R.drawable.cross_icon03);
                Animation scrollRightAnim = AnimationUtils.loadAnimation(this, R.anim.slide_right_navigation_drawer);
                binding.exerciseRepresentationHeader.exerciseNavigation.startAnimation(scrollRightAnim);
                binding.exerciseRepresentationHeader.exerciseNavigation.setVisibility(View.VISIBLE);
            }

        });

        binding.exerciseRepresentationHeader.exerciseNavigation.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.sports) {
                startActivity(new Intent(ExerciseRepresentationActivity.this, MoreSportsModeActivity.class));
            }
            else if (item.getItemId() == R.id.yoga) {
//                startActivity(new Intent(ExerciseRepresentationActivity.this, YogaModeRepresentationActivity.class));
            }
            return true;
        });

    }

    private void setupReceiveExerciseDetails() {

        Intent receiveIntent = getIntent();

        String exerciseName = receiveIntent.getStringExtra("exerciseName");
        String exerciseIconLink = receiveIntent.getStringExtra("exerciseIconLink");
        String exerciseImageLink01 = receiveIntent.getStringExtra("exerciseImageLink01");
        String exerciseImageLink02 = receiveIntent.getStringExtra("exerciseImageLink02");
        String exerciseDifficultyLevel = receiveIntent.getStringExtra("exerciseDifficultyLevel");
        String exerciseDescription = receiveIntent.getStringExtra("exerciseDescription");
        String exerciseVideoLink01 = receiveIntent.getStringExtra("exerciseVideoLink01");
        String exerciseVideoLink02 = receiveIntent.getStringExtra("exerciseVideoLink02");

        setupSetExerciseDetails(exerciseName, exerciseIconLink, exerciseImageLink01, exerciseImageLink02, exerciseDifficultyLevel, exerciseDescription, exerciseVideoLink01, exerciseVideoLink02);

    }

    private void setupSetExerciseDetails(String exerciseName, String exerciseIconLink, String exerciseImageLink01, String exerciseImageLink02, String exerciseDifficultyLevel, String exerciseDescription, String exerciseVideoLink01, String exerciseVideoLink02) {
        binding.exerciseRepresentationHeader.exerciseName.setText(exerciseName);

        Glide.with(this)
                .load(exerciseImageLink02)
                .error(new ColorDrawable(Color.parseColor("#DDDDDD")))
                .into( binding.exerciseRepresentationHeader.backgroundOfTheContainer.backgroundImage );

        Glide.with(this)
                .load(exerciseIconLink)
                .error(new ColorDrawable(Color.parseColor("#DDDDDD")))
                .into( binding.exerciseRepresentationHeader.exerciseIcon );

        binding.exerciseRepresentationHeader.exerciseDifficultyLevel.setText(exerciseDifficultyLevel);

//        binding.exerciseVideoBody.exerciseDescription.setText(exerciseDescription);

//        setupExerciseVideos(exerciseVideoLink01, exerciseVideoLink02);

    }

}