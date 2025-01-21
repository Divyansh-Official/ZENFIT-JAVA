package com.stream.zenfit;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Database.UserProfileImageStoreSQLite;
import com.stream.zenfit.databinding.ActivityProfileBinding;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Dialog editUserDetailsDialog, checkUserPasswordDialog, editUserPasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupIntentReceiveUserData();
        setupCheckUserPassword();
        setupEditUserImageButton();
        setupChangeStatusBarColor();

    }

    private void setupIntentReceiveUserData() {
        if (firebaseUser != null) {
            String userEmail = firebaseUser.getEmail();
            if (userEmail != null) {
                DocumentReference documentReference = firebaseFirestore.collection("Users").document(userEmail);
                documentReference.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String userName = documentSnapshot.getString("name");
                                String userPhone = documentSnapshot.getString("phone");
                                String userAge = documentSnapshot.getString("age");
                                String userPassword = documentSnapshot.getString("password");

                                // Call the method to update UI
                                setupSetUserData(userName, userEmail, userPhone, userAge, userPassword);
                            } else {
                                Toast.makeText(ProfileActivity.this, "User Data Not Found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ProfileActivity.this, "Failed To Retrieve User Data", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }


    private void setupSetUserData(String userName, String userEmail, String userPhone, String userAge, String userPassword) {
        binding.userName.setText(userName);
        binding.userEmail.setText(userEmail);
        binding.userAge.setText(userAge);

        setupEditUserDetailsButton(userName, userEmail, userPhone, userAge, userPassword);
    }

    private void setupCheckUserPassword() {
        Intent receiveIntent = getIntent();
        String userPassword = receiveIntent.getStringExtra("Password");

        checkUserPasswordDialog = new Dialog(ProfileActivity.this);
        checkUserPasswordDialog.setContentView(R.layout.activity_profile_password_check_dialog_box);
        checkUserPasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        checkUserPasswordDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_bg));
        checkUserPasswordDialog.setCancelable(true);

        EditText checkCurrentPasswordInput = checkUserPasswordDialog.findViewById(R.id.checkUserCurrentPassword);
        Button checkCurrentPasswordButton = checkUserPasswordDialog.findViewById(R.id.checkUserCurrentPasswordButton);

        binding.userPasswordEditButton.setOnClickListener(v -> {
                editUserPasswordDialog.show();
        });

        checkCurrentPasswordButton.setOnClickListener(v -> {
            String password = checkCurrentPasswordInput.getText().toString().trim();

            if (password == null || password.isEmpty() || password.length() < 6)
            {
                Toast.makeText(ProfileActivity.this, "Please Enter In A Valid Format Which Should Be > 6", Toast.LENGTH_SHORT).show();
            }
            else if (password.equals(userPassword))
            {
                checkUserPasswordDialog.dismiss();
                setupEditUserPassword(password);
            }
            else
            {
                Toast.makeText(ProfileActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEditUserPassword(String userPassword) {
        editUserPasswordDialog = new Dialog(ProfileActivity.this);
        editUserPasswordDialog.setContentView(R.layout.activity_profile_password_edit_dialog_box);
        editUserPasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editUserPasswordDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_bg));
        editUserPasswordDialog.setCancelable(true);

        EditText editNewPasswordInput = editUserPasswordDialog.findViewById(R.id.editUserNewPassword);
        EditText editConfirmNewPasswordInput = editUserPasswordDialog.findViewById(R.id.editUserConfirmNewPassword);
        Button editPasswordChangeButton = editUserPasswordDialog.findViewById(R.id.editUserPasswordChangeButton);

        editPasswordChangeButton.setOnClickListener(v -> {
            String newPassword = editNewPasswordInput.getText().toString().trim();
            String confirmNewPassword = editConfirmNewPasswordInput.getText().toString().trim();

            if (newPassword.length() > 6 && newPassword.equals(confirmNewPassword))
            {
                firebaseUser.updatePassword(newPassword);
                Toast.makeText(ProfileActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(ProfileActivity.this, "New Password Is Not Equal To Confirm Password Or It Is Not Greater Than 6 Characters", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEditUserDetailsButton(String name, String email, String phone, String age, String password) {
        editUserDetailsDialog = new Dialog(ProfileActivity.this);
        editUserDetailsDialog.setContentView(R.layout.activity_profile_details_edit_dialog_box);
        editUserDetailsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editUserDetailsDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_bg));
        editUserPasswordDialog.setCancelable(true);

        EditText editName = editUserDetailsDialog.findViewById(R.id.editUserName);
        EditText editEmail = editUserDetailsDialog.findViewById(R.id.editUserEmail);
        EditText editPhone = editUserDetailsDialog.findViewById(R.id.editUserPhone);
        EditText editAge = editUserDetailsDialog.findViewById(R.id.editUserAge);
        Button editSaveButton = editUserDetailsDialog.findViewById(R.id.editUserInformationSaveButton);

        editName.setText(name);
        editEmail.setText(email);
        editPhone.setText(phone);
        editAge.setText(age);

        String userPassword = password;

        editSaveButton.setOnClickListener(v -> {
            String userName = editName.getText().toString().trim();
            String userEmail = editEmail.getText().toString().trim();
            String userPhone = editPhone.getText().toString().trim();
            String userAge = editAge.getText().toString().trim();

            // Check input validity
            if (!conditionOfTheInputsOfADialogBox(userName, userEmail, userPhone, userAge)) return;

            // Update email if it has changed
            if (!firebaseUser.getEmail().equals(userEmail)) {
                firebaseUser.updateEmail(userEmail)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            // Update password if required (e.g., check a condition)
            if (!userPassword.isEmpty()) {
                firebaseUser.updatePassword(userPassword)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            DocumentReference documentReference = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(firebaseUser.getEmail());

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", userName);
            updates.put("phone", userPhone);
            updates.put("age", userAge);

            documentReference.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });


        binding.userProfileDetailsEditButton.setOnClickListener(v -> {
            editUserDetailsDialog.show();
        });
    }

    private boolean conditionOfTheInputsOfADialogBox(String userName, String userEmail, String userPhone, String userAge) {
        // Check if userName is empty or too short
        if (userName == null || userName.isEmpty() || userName.length() < 3) {
            Toast.makeText(this, "Name must be at least 3 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if userEmail is valid
        if (userEmail == null || userEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if userPhone is valid (10 digits for most regions)
        if (userPhone == null || userPhone.isEmpty() || !userPhone.matches("\\d{10}")) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if userAge is valid (e.g., between 13 and 100)
        try {
            int age = Integer.parseInt(userAge);
            if (age < 13 || age > 100) {
                Toast.makeText(this, "Age must be between 13 and 100.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Age must be a valid number.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // All inputs are valid
        return true;
    }



    private void setupEditUserImageButton() {
        binding.userProfileImageEditButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryActivityResultLauncher.launch(intent);
        });
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null)
                {
                    Uri selectedImageUri = result.getData().getData();

                    if (selectedImageUri != null)
                    {
                        try {
                            // Set the selected image to the ImageView
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                            // Changing saturation of an image
                            ColorMatrix colorMatrix = new ColorMatrix();
                            colorMatrix.setSaturation(0);
                            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

                            binding.userProfileImage.setImageBitmap(bitmap);
                            binding.userProfileImage.setColorFilter(filter);

                            String uriString = selectedImageUri.toString();

                            saveImageOnProfile(uriString);
                        } catch (Exception e) {
                            Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private void saveImageOnProfile(String uri) {
        binding.userSaveImageButton.setVisibility(View.VISIBLE);

        binding.userSaveImageButton.setOnClickListener(v -> {
            UserProfileImageStoreSQLite database = new UserProfileImageStoreSQLite(this);
            database.addImage(uri);

            binding.userSaveImageButton.setVisibility(View.GONE);
        });
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            // Redirect to login or main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

}