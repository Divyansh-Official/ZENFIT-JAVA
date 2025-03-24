package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.stream.zenfit.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();

        binding.sportsModeAddItemManagementButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AddSportsVarietyActivity.class));
        });

        binding.exerciseModeAddItemManagementButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AddExerciseVarietyActivity.class));
        });
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark2));
    }

}