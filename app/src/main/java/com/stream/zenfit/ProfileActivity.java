package com.stream.zenfit;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private UserProfileImageStoreSQLite dbHelper;
    private FirebaseFirestore firebaseFirestore;
    private Dialog editUserDetailsDialog, checkUserPasswordDialog, editUserPasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Bind the layout
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup methods
        loadUserProfileImage();
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
                                String userName = documentSnapshot.getString("Name");
                                String userPhone = documentSnapshot.getString("Phone");
                                String userAge = documentSnapshot.getString("Age");
                                String userPassword = documentSnapshot.getString("Password");

                                setupSetUserData(userName, userEmail, userPhone, userAge, userPassword);
                            } else {
                                Toast.makeText(ProfileActivity.this, "User Data Not Found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed To Retrieve User Data", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void setupSetUserData(String userName, String userEmail, String userPhone, String userAge, String userPassword) {
        binding.userName.setText(userName);
        binding.userEmail.setText(userEmail);
        binding.userAge.setText(userAge);
        binding.userPhone.setText(userPhone);

        setupEditUserDetailsButton(userName, userEmail, userPhone, userAge, userPassword);
    }

    private void setupCheckUserPassword() {
        Intent receiveIntent = getIntent();
        String userPassword = receiveIntent.getStringExtra("Password");

        checkUserPasswordDialog = new Dialog(this);
        checkUserPasswordDialog.setContentView(R.layout.activity_profile_password_check_dialog_box);
        checkUserPasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        checkUserPasswordDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box_bg);
        checkUserPasswordDialog.setCancelable(true);

        EditText checkCurrentPasswordInput = checkUserPasswordDialog.findViewById(R.id.checkUserCurrentPassword);
        Button checkCurrentPasswordButton = checkUserPasswordDialog.findViewById(R.id.checkUserCurrentPasswordButton);

        binding.userPasswordEditButton.setOnClickListener(v -> checkUserPasswordDialog.show());

        checkCurrentPasswordButton.setOnClickListener(v -> {
            String password = checkCurrentPasswordInput.getText().toString().trim();

            if (password.isEmpty() || password.length() < 6) {
                Toast.makeText(this, "Please Enter A Valid Password", Toast.LENGTH_SHORT).show();
            } else if (password.equals(userPassword)) {
                checkUserPasswordDialog.dismiss();
                setupEditUserPassword(password);
            } else {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEditUserPassword(String userPassword) {
        editUserPasswordDialog = new Dialog(this);
        editUserPasswordDialog.setContentView(R.layout.activity_profile_password_edit_dialog_box);
        editUserPasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editUserPasswordDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box_bg);
        editUserPasswordDialog.setCancelable(true);

        EditText editNewPasswordInput = editUserPasswordDialog.findViewById(R.id.editUserNewPassword);
        EditText editConfirmNewPasswordInput = editUserPasswordDialog.findViewById(R.id.editUserConfirmNewPassword);
        Button editPasswordChangeButton = editUserPasswordDialog.findViewById(R.id.editUserPasswordChangeButton);

        editPasswordChangeButton.setOnClickListener(v -> {
            String newPassword = editNewPasswordInput.getText().toString().trim();
            String confirmNewPassword = editConfirmNewPasswordInput.getText().toString().trim();

            if (newPassword.length() > 6 && newPassword.equals(confirmNewPassword)) {
                firebaseUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        editUserPasswordDialog.dismiss();
                    } else {
                        Toast.makeText(this, "Failed To Change Password", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Passwords Do Not Match Or Are Too Short", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEditUserDetailsButton(String name, String email, String phone, String age, String password) {
        editUserDetailsDialog = new Dialog(this);
        editUserDetailsDialog.setContentView(R.layout.activity_profile_details_edit_dialog_box);
        editUserDetailsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editUserDetailsDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box_bg);
        editUserDetailsDialog.setCancelable(true);

        EditText editName = editUserDetailsDialog.findViewById(R.id.editUserName);
        EditText editEmail = editUserDetailsDialog.findViewById(R.id.editUserEmail);
        EditText editPhone = editUserDetailsDialog.findViewById(R.id.editUserPhone);
        EditText editAge = editUserDetailsDialog.findViewById(R.id.editUserAge);
        Button editSaveButton = editUserDetailsDialog.findViewById(R.id.editUserInformationSaveButton);

        editName.setText(name);
        editEmail.setText(email);
        editPhone.setText(phone);
        editAge.setText(age);

        editSaveButton.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String newPhone = editPhone.getText().toString().trim();
            String newAge = editAge.getText().toString().trim();

            if (!validateInputs(newName, newEmail, newPhone, newAge)) return;

            if (!firebaseUser.getEmail().equals(newEmail)) {
                firebaseUser.updateEmail(newEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Email Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed To Update Email", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            DocumentReference docRef = firebaseFirestore.collection("Users").document(firebaseUser.getEmail());
            Map<String, Object> updates = new HashMap<>();
            updates.put("Name", newName);
            updates.put("Phone", newPhone);
            updates.put("Age", newAge);

            docRef.update(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    editUserDetailsDialog.dismiss();
                } else {
                    Toast.makeText(this, "Failed To Update Profile", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.userProfileDetailsEditButton.setOnClickListener(v -> editUserDetailsDialog.show());
    }

    private boolean validateInputs(String name, String email, String phone, String age) {
        if (name.isEmpty() || name.length() < 3) {
            Toast.makeText(this, "Name must be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.isEmpty() || !phone.matches("\\d{10}")) {
            Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            int userAge = Integer.parseInt(age);
            if (userAge < 13 || userAge > 100) {
                Toast.makeText(this, "Age must be between 13 and 100", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid Age", Toast.LENGTH_SHORT).show();
            return false;
        }
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
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();

                    if (selectedImageUri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                            ColorMatrix colorMatrix = new ColorMatrix();
                            colorMatrix.setSaturation(0);
                            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

                            binding.userProfileImage.setImageBitmap(bitmap);
                            binding.userProfileImage.setColorFilter(filter);

                            saveImageOnProfile(selectedImageUri.toString());
                        } catch (Exception e) {
                            Toast.makeText(this, "Error Loading Image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private void saveImageOnProfile(String imageUri) {
        dbHelper.addOrUpdateUserProfileImage(imageUri);
    }

    private void loadUserProfileImage() {
        try {
            String imageUriString = dbHelper.getUserProfileImage();
            if (imageUriString != null && !imageUriString.isEmpty()) {
                Uri imageUri = Uri.parse(imageUriString);
                Log.d("UserProfileImage", "Image URI: " + imageUri.toString());

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                binding.userProfileImage.setImageBitmap(bitmap);

                // Uncomment these lines after confirming the image loads correctly
                // ColorMatrix colorMatrix = new ColorMatrix();
                // colorMatrix.setSaturation(0);
                // ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
                // binding.userProfileImage.setColorFilter(filter);
            } else {
                Toast.makeText(this, "No Profile Image Found", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File Not Found: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "IO Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error Loading Image: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("UserProfileImage", "Error Loading Image", e);
        }
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
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}