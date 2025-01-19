package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.stream.zenfit.databinding.ActivitySignUpPhoneBinding;

public class SignUpPhoneActivity extends AppCompatActivity {

    ActivitySignUpPhoneBinding binding;
    FirebaseAuth firebaseAuth;
    private static final String TAG = "SignUpNamePhoneActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        firebaseAuth = FirebaseAuth.getInstance();

        binding = ActivitySignUpPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();

        binding.nextButton.setOnClickListener(v -> {
            Intent receive = getIntent();
            String userName = receive.getStringExtra("userName");
            String userEmail = receive.getStringExtra("userEmail");

            String userPhone = binding.phoneInput.getText().toString().trim();

            if (!userPhone.isEmpty() && userPhone.length() == 10)
            {
                Intent intent = new Intent(this, SignUpAgeActivity.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("userPhone", userPhone);
                startActivity(intent);

                Toast.makeText(this, "Phone: " + userPhone, Toast.LENGTH_SHORT).show();
            }
            else if (userPhone.length() != 10)
            {
                Toast.makeText(this, "Number Format Doesn't Match", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

}