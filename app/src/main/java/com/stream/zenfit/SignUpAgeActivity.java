package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.stream.zenfit.databinding.ActivitySignUpAgeBinding;
import com.stream.zenfit.databinding.ActivitySignUpNameBinding;

public class SignUpAgeActivity extends AppCompatActivity {

    ActivitySignUpAgeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_age);

        binding = ActivitySignUpAgeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();

        binding.nextButton.setOnClickListener(v -> {
            Intent receive = getIntent();
            String userName = receive.getStringExtra("userName");
            String userEmail = receive.getStringExtra("userEmail");
            String userPhone = receive.getStringExtra("userPhone");

            String userAge = binding.ageInput.getText().toString().trim();

            if (!userAge.isEmpty())
            {
                Intent intent = new Intent(this, SignUpPasswordActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("userPhone", userPhone);
                intent.putExtra("userAge", userAge);
                startActivity(intent);

                Toast.makeText(this, "Age: " + userAge, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Please Tell Your Age", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

}