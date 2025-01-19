package com.stream.zenfit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.databinding.ActivitySignUpPasswordBinding;
import java.util.HashMap;
import java.util.Map;

public class SignUpPasswordActivity extends AppCompatActivity {

    ActivitySignUpPasswordBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding = ActivitySignUpPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();

        binding.registerButton.setOnClickListener(v -> {
            binding.signingUpProgressIndicator.setVisibility(View.VISIBLE);
            setupReceivingAnIntentDetails();
        });
    }

    private void setupReceivingAnIntentDetails() {
        Intent receivedIntent = getIntent();
        String name = receivedIntent.getStringExtra("userName");
        String email = receivedIntent.getStringExtra("userEmail");
        String age = receivedIntent.getStringExtra("userAge");
        String phone = receivedIntent.getStringExtra("userPhone");
        Toast.makeText(this, name + " " + email + " " + age + " " + phone, Toast.LENGTH_SHORT).show();
        String password = binding.passwordInput.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();

        setupCheckPasswordConditions(name, email, phone, age, password, confirmPassword);
    }

    private void setupCheckPasswordConditions(String name, String email, String phone, String age, String password, String confirmPassword) {
       if (password.isEmpty() || confirmPassword.isEmpty())
        {
            Toast.makeText(this, "Password Fields Is Empty", Toast.LENGTH_SHORT).show();
            binding.signingUpProgressIndicator.setVisibility(View.GONE);
        } else if (password.length() < 6 || confirmPassword.length() < 6)
        {
            Toast.makeText(this, "Password Length Is Less Than 6 Characters", Toast.LENGTH_SHORT).show();
            binding.signingUpProgressIndicator.setVisibility(View.GONE);
        } else if (!password.equals(confirmPassword))
        {
            Toast.makeText(this, "Password ≠ Confirm Password", Toast.LENGTH_SHORT).show();
            binding.signingUpProgressIndicator.setVisibility(View.GONE);
        } else
        {
           setupRegistrationOfTheUserWithSavingTheDetails(name, email, phone, age, password, confirmPassword);
        }
    }

    private void setupRegistrationOfTheUserWithSavingTheDetails(String name, String email, String phone, String age, String password, String confirmPassword) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    binding.signingUpProgressIndicator.setVisibility(View.VISIBLE);

                    if (task.isSuccessful())
                    {
                        firebaseAuth.getCurrentUser().sendEmailVerification()
                                .addOnSuccessListener(unused -> {

                                    DocumentReference documentReference = firebaseFirestore.collection("Users").document(email);
                                    Map<String, Object> userDetails = new HashMap<>();
                                    userDetails.put("Name", name);
                                    userDetails.put("Email", email);
                                    userDetails.put("Phone", phone);
                                    userDetails.put("Age", age);
                                    userDetails.put("Password", password);

                                    documentReference.set(userDetails)
                                            .addOnSuccessListener(unused1 -> {
                                                Log.d("User Profile Update", "OnSuccess : Profile Successfully Created For " + name);
                                                Toast.makeText(SignUpPasswordActivity.this, "Profile Successfully Created For " + name, Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(this, MainActivity.class));
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.d("User Profile Update", "OnFailure : Profile Not Created");
                                                Toast.makeText(SignUpPasswordActivity.this, "Profile Not Created!", Toast.LENGTH_SHORT).show();
                                            });
                                    Toast.makeText(this, "Verification Link Sent To " +email, Toast.LENGTH_SHORT).show();
                                })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error: Can't Send Verification To Your Mail", Toast.LENGTH_SHORT).show();
                                        });
                    }
                    else
                    {
                        Toast.makeText(SignUpPasswordActivity.this, "User Already Exists With This Mail I'D", Toast.LENGTH_SHORT).show();
                        binding.signingUpProgressIndicator.setVisibility(View.GONE);
                    }
        });
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

}