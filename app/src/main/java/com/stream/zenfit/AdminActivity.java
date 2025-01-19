package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.stream.zenfit.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sportsModeAddItemManagementButton.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, SportsModeItemManagementActivity.class));
        });

    }
}