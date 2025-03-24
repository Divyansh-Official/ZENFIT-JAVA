package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stream.zenfit.databinding.ActivityMailVerificationBinding;

public class MailVerificationActivity extends AppCompatActivity {

    ActivityMailVerificationBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMailVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setupChangeStatusBarColor();

//        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified()) {
                Intent mainActivityIntent = new Intent(MailVerificationActivity.this, MainActivity.class);
                startActivity(mainActivityIntent);
                finish();
            }
            else {
                Toast.makeText(this, "Email is not verified. Please check your inbox.", Toast.LENGTH_LONG).show();
            }
//        }
//        else {
//            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
//        }

        binding.redirectToMainButton.setOnClickListener(v -> {
               checkEmailVerification();
        });

        binding.signUpButton.setOnClickListener(v -> {
            Intent signupActivityIntent = new Intent(MailVerificationActivity.this, SignInActivity.class);
            startActivity(signupActivityIntent);
            finish();
        });

        binding.sendVerificationLinkButton.setOnClickListener(v -> {
            sendVerificationEmail();
        });

        binding.logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent signupActivityIntent = new Intent(MailVerificationActivity.this, SignUpNameActivity.class);
            startActivity(signupActivityIntent);
            finish();
        });

    }

    private void checkEmailVerification() {

        if (firebaseUser != null) {
            firebaseUser.reload().addOnCompleteListener(task -> { // Reload user data
                if (firebaseUser.isEmailVerified()) {
                    Toast.makeText(this, "Email verified! Redirecting...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class)); // Redirect to MainActivity
                    finish(); // Close verification activity
                } else {
                    Toast.makeText(this, "Email not verified yet. Please check again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendVerificationEmail() {

        if (firebaseUser != null && !firebaseUser.isEmailVerified()) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Verification email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Toast.makeText(this, "User is already verified or not logged in.", Toast.LENGTH_LONG).show();
        }
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

}