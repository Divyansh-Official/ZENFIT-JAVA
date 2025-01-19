package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "SignUpNamePhoneActivity";
    private String userName, userEmail, userPhone, userAge, userPassword, userAuthenticationCode;
    String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupReceiveUserSignUpDetails();

    }

    private void setupReceiveUserSignUpDetails() {
        Intent receiveInputIntent = getIntent();
        userName = receiveInputIntent.getStringExtra("userName");
        userEmail = receiveInputIntent.getStringExtra("userEmail");
        userPhone = receiveInputIntent.getStringExtra("userPhone");
        userAge = receiveInputIntent.getStringExtra("userAge");
        userPassword = receiveInputIntent.getStringExtra("userPassword");
        userAuthenticationCode = receiveInputIntent.getStringExtra("userAuthenticationCode");

        String OTP = binding.otpInput.getText().toString();
    }
}
