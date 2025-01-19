package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stream.zenfit.databinding.ActivitySignUpNameBinding;

public class SignUpNameActivity extends AppCompatActivity {

    ActivitySignUpNameBinding binding;
    FirebaseAuth firebaseAuth;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_name);

        firebaseAuth = FirebaseAuth.getInstance();

        binding = ActivitySignUpNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();

        if (firebaseAuth.getCurrentUser() != null)
        {
            Intent mainActivityIntent = new Intent(SignUpNameActivity.this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }

        binding.nextButton.setOnClickListener(v -> {
           userName = binding.nameInput.getText().toString().trim();

            if (userName.isEmpty() || userName == null)
            {
                Toast.makeText(this, "Please Provide Your Name", Toast.LENGTH_SHORT).show();
            }
            else if (!userName.isEmpty() && userName != null)
            {
                Intent intent = new Intent(this, SignUpEmailActivity.class);
                intent.putExtra("userName", userName);
                startActivity(intent);

                Toast.makeText(this, "Name: " + userName, Toast.LENGTH_SHORT).show();
            }
        });

        binding.signinButton.setOnClickListener(v -> {
            Intent signinIntent = new Intent(SignUpNameActivity.this, SignInActivity.class);
            startActivity(signinIntent);
//            finish();
        });

    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null)
        {
            startActivity(new Intent(SignUpNameActivity.this, MainActivity.class));
            finish();
        }
    }
}