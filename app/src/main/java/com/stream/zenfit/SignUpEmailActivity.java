package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stream.zenfit.databinding.ActivitySignUpEmailBinding;
import com.stream.zenfit.databinding.ActivitySignUpNameBinding;

public class SignUpEmailActivity extends AppCompatActivity {

    ActivitySignUpEmailBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        binding = ActivitySignUpEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();

        binding.nextButton.setOnClickListener(v -> {
            Intent receive = getIntent();
            String userName = receive.getStringExtra("userName");

            Toast.makeText(this, userName, Toast.LENGTH_SHORT).show();

            String userEmail = binding.emailInput.getText().toString().trim();

            if (userEmail.isEmpty())
            {
                Toast.makeText(this, "Please Enter Your Email Correctly", Toast.LENGTH_SHORT).show();
            }
            else if (userEmail.endsWith("@gmail.com") && !userEmail.isEmpty() && userEmail != null)
            {
                Intent intent = new Intent(this, SignUpPhoneActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);

                Toast.makeText(this, "Email: " + userEmail, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

}