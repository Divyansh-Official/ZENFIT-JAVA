package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.stream.zenfit.R;
import com.stream.zenfit.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSignUpTheUser();
        setupForgotPassword();
        setupChangeStatusBarColor();
        setupSignInTheUser();

    }

    private void setupSignUpTheUser() {
        binding.signupButton.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpNameActivity.class));
        });
    }

    private void setupForgotPassword() {
        binding.forgotPasswordButton.setOnClickListener(v -> {
            String email = binding.userEmailInput.getText().toString().trim();

            if (email.endsWith("@gmail.com"))
            {
                setupPasswordResetEmail(email);
            }
            else
            {
                Toast.makeText(this, "Please Enter Your Email Correctly", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPasswordResetEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Password Reset Email Sent", Toast.LENGTH_SHORT).show();
        })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Email Not Found In The Database", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupSignInTheUser() {
        binding.submitButton.setOnClickListener(v -> {
            String email = binding.userEmailInput.getText().toString().trim();
            String password = binding.userPasswordInput.getText().toString().trim();
            binding.signingInProgressIndicator.setVisibility(View.VISIBLE);

            if (!setupCheckEmailAndPasswordConditions(email, password)) return;
        });
    }

    private boolean setupCheckEmailAndPasswordConditions(String email, String password) {
            if (email.isEmpty()) {
                binding.userEmailInput.setError("Email Is Required");
                return false;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.userEmailInput.setError("Enter A Valid Email");
                return false;
            }

            if (!email.toLowerCase().endsWith("@gmail.com")) {
                binding.userEmailInput.setError("Only Gmail Addresses Are Allowed");
                return false;
            }

            if (password.isEmpty()) {
                binding.userPasswordInput.setError("Password Is Required");
                return false;
            }

            if (password.length() < 6) {
                binding.userPasswordInput.setError("Password Should Be At Least 6 Characters");
                return false;
            }

            setupSignInTheUserWithFirebase(email, password);
            return true;
    }

    private void setupSignInTheUserWithFirebase(String email, String password) {
        binding.signingInProgressIndicator.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        binding.signingInProgressIndicator.setVisibility(View.GONE);
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish();
                    }
                    else
                    {
                        binding.signingInProgressIndicator.setVisibility(View.GONE);
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                        Toast.makeText(SignInActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }

                });

    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

}