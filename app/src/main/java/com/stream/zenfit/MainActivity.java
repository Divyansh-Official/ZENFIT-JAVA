package com.stream.zenfit;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Adapter.ExerciseModeAdapter;
import com.stream.zenfit.Adapter.MainHeaderImageAdapter;
import com.stream.zenfit.Adapter.SportsModeAdapter;
import com.stream.zenfit.MainActivityClasses.MainExerciseMode;
import com.stream.zenfit.MainActivityClasses.MainSportsMode;
import com.stream.zenfit.Modal.ExerciseModeModal;
import com.stream.zenfit.Modal.SportsModeModal;
import com.stream.zenfit.databinding.ActivityMainBinding;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ActivityMainBinding binding;
    private SensorManager pedometerSensorManager;
    private Sensor stepCounterSensor;
    FirebaseAuth firebaseAuth;
    private ViewPager viewPager;
    private DotsIndicator dotsIndicator;
    private int currentPosition = 0;
    EditText userFeedback;
    RecyclerView sportsRecyclerView;
    private Handler handler;
    private Runnable autoScrollRunnable;
    FirebaseFirestore firebaseFirestore;
    ArrayList<Integer> headerImages = new ArrayList<>();
    ShimmerFrameLayout shimmerFrameLayout;
    private boolean stepCounterSensorRunning = false;
    int stepCount = 0;
    private static final String CHANNEL_ID = "PersistentNotificationChannel";
    int stepCountTarget;
    MainHeaderImageAdapter mainHeaderImageAdapter = new MainHeaderImageAdapter(this, headerImages);
    int baseStepCount = 0;
    FirebaseUser firebaseUser;
    float distanceCovered = 0.0f;
    private static final int PERMISSION_REQUEST_CODE = 100;
    Dialog dialogUpdates, dialogAdmin, dialogFeedback;
    Button closeDialogButton, loginDialogButton;
    AppCompatButton submitDialogButton;
    private EditText adminUserName, adminUserPassword;
    String userID, userEmail;
    SportsModeAdapter sportsModeAdapter;
    ExerciseModeAdapter exerciseModeAdapter;
    private List<SportsModeModal> sportsList = new ArrayList<>();
    MainSportsMode mainSportsMode;
    MainExerciseMode mainExerciseMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userID = firebaseAuth.getCurrentUser().getUid();
        userEmail = firebaseAuth.getCurrentUser().getEmail();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (firebaseAuth.getCurrentUser() != null)
        {
            setupToolBarAppNameFunctionality();
            setupMoreFeaturesButtonFunctionality();
            setupShimmerEffectsForDifferentModes();
            setupDifferentModesOnLongPress();
            setupLogoIconDialogueBox();
            setupChangeStatusBarColor();
            setupAboutUsLinkOpen();
            setupAnimationsOnStart();
            setupHeaderImageDragUpDown();
            setupPedometerVisibilityButton();
            setupNavigationDrawer(userEmail);
            setupButtons();
            setupPedometerPermission();
            setupHeaderImageNameSetup();

            mainSportsMode = new MainSportsMode(this, binding);
            mainSportsMode.setupSportsRequirements();

            mainExerciseMode = new MainExerciseMode(this, binding);
            mainExerciseMode.setupExerciseRequirements();
        }
    }

    private void setupHeaderImageNameSetup() {
        if (firebaseUser != null) {
            String userEmail = firebaseUser.getEmail();
            if (userEmail != null) {
                DocumentReference documentReference = firebaseFirestore.collection("Users").document(userEmail);
                documentReference.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String userName = documentSnapshot.getString("Name");

                                binding.headerImage.headerWelcomeUserText.setText("WELCOME " + userName.toUpperCase() + " !");

                            } else {
                                Toast.makeText(this, "User Data Not Found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed To Retrieve User Data", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void setupToolBarAppNameFunctionality() {
        binding.toolbar.appName.setOnClickListener(v -> {

        });
    }

    private void setupMoreFeaturesButtonFunctionality() {
        TextView textView = new TextView(this);
        textView.setText("This Feature Will Be Available Very Soon... \nPlease Keep Patience Till Then...");
        textView.setTextSize(18);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setPadding(50, 150, 50, 150);
        textView.setGravity(Gravity.CENTER);
        Dialog dialogMoreFeatures = new Dialog(MainActivity.this);
        dialogMoreFeatures.setContentView(textView);
        dialogMoreFeatures.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogMoreFeatures.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_bg));
        dialogMoreFeatures.setCancelable(true);

        binding.addMoreFeaturesButton.setOnClickListener(v -> {
            dialogMoreFeatures.show();
        });
    }

    private void setupShimmerEffectsForDifferentModes() {
        binding.sportsMode.sportsModeOptions.setVisibility(View.INVISIBLE);
        shimmerFrameLayout = binding.sportsMode.sportsModeOptionsRecyclerViewShimmerEffectPlaceHolder;
        shimmerFrameLayout.showShimmer(true);
        shimmerFrameLayout.startShimmer();
    }

    private void setupDifferentModesOnLongPress() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        binding.sportsMode.sportsModeMainContainer.setOnLongClickListener(v -> {
            binding.addMoreFeaturesButton.setVisibility(View.GONE);
            binding.sportsMode.checkButton.setVisibility(View.VISIBLE);
            binding.sportsMode.unCheckButton.setVisibility(View.VISIBLE);
            sportsModeOptionsClickable(false, 10);

            if (vibrator != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(250);
                }
            }
            return true;
        });

        binding.sportsMode.checkButton.setOnClickListener(v -> {
            binding.sportsMode.sportsModeMainContainer.setVisibility(View.GONE);
            sportsModeOptionsClickable(true, 0);

            binding.addMoreFeaturesButton.setVisibility(View.VISIBLE);
            binding.sportsMode.checkButton.setVisibility(View.GONE);
            binding.sportsMode.unCheckButton.setVisibility(View.GONE);
        });

        binding.sportsMode.unCheckButton.setOnClickListener(v -> {
            binding.sportsMode.sportsModeMainContainer.setVisibility(View.VISIBLE);
            sportsModeOptionsClickable(true, 0);

            binding.addMoreFeaturesButton.setVisibility(View.VISIBLE);
            binding.sportsMode.checkButton.setVisibility(View.GONE);
            binding.sportsMode.unCheckButton.setVisibility(View.GONE);
        });
    }

    private void sportsModeOptionsClickable(boolean clickable, int padding) {
        binding.sportsMode.moreButton.setClickable(clickable);

        binding.sportsMode.sportsModeMainContainer.setPadding(padding, padding, padding, padding);
    }

    private void setupLogoIconDialogueBox() {
        dialogUpdates = new Dialog(MainActivity.this);
        dialogUpdates.setContentView(R.layout.activity_main_logo_icon_dialog_box);
        dialogUpdates.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogUpdates.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_bg));
        dialogUpdates.setCancelable(false);

        binding.toolbar.appLogo.setOnClickListener(v -> {
            dialogUpdates.show();
        });

        closeDialogButton = dialogUpdates.findViewById(R.id.closeDialogButton);
        closeDialogButton.setOnClickListener(v -> {
            dialogUpdates.dismiss();
        });
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }

    private void setupAboutUsLinkOpen() {
        Uri instaURI = Uri.parse("https://www.instagram.com/codewithdivyansh/profilecard/");
        Uri linkedinURI = Uri.parse("https://www.linkedin.com/in/divyansh-tiwari-100299288/");
        Uri githubURI = Uri.parse("https://github.com/Divyansh-Official/");
        Uri xURI = Uri.parse("https://x.com/");

        binding.aboutUs.instagramButton.setOnClickListener(v -> {
            Intent instaIntent = new Intent(Intent.ACTION_VIEW, instaURI);
            startActivity(instaIntent);
        });

        binding.aboutUs.linkedinButton.setOnClickListener(v -> {
            Intent linkedinIntent = new Intent(Intent.ACTION_VIEW, linkedinURI);
            startActivity(linkedinIntent);
        });

        binding.aboutUs.githubButton.setOnClickListener(v -> {
            Intent githubIntent = new Intent(Intent.ACTION_VIEW, githubURI);
            startActivity(githubIntent);
        });

        binding.aboutUs.xButton.setOnClickListener(v -> {
            Intent githubIntent = new Intent(Intent.ACTION_VIEW, xURI);
            startActivity(githubIntent);
        });
    }

    private void setupAnimationsOnStart() {
        Animation scrollUpAnim = AnimationUtils.loadAnimation(this, R.anim.scroll_up_anim);
        Animation scrollDownAnim = AnimationUtils.loadAnimation(this, R.anim.scroll_down_anim);
        Animation scrollLeftAnim = AnimationUtils.loadAnimation(this, R.anim.scroll_left_anim);
        Animation scrollRightAnim = AnimationUtils.loadAnimation(this, R.anim.scroll_right_anim);

        binding.chatbot.floatingButton.startAnimation(scrollLeftAnim);
        binding.chatbot.floatingButton.setAnimation(R.raw.robot_icon03);
        binding.chatbot.floatingButton.playAnimation();

        binding.toolbar.appName.startAnimation(scrollDownAnim);
        binding.headerImage.headerMenuContentViewPager.startAnimation(scrollDownAnim);

        binding.toolbar.menuButton.startAnimation(scrollLeftAnim);
        binding.toolbar.profileButton.startAnimation(scrollLeftAnim);
        binding.sportsMode.moreButton.startAnimation(scrollLeftAnim);
        binding.addMoreFeaturesButton.startAnimation(scrollLeftAnim);

        binding.toolbar.appLogo.startAnimation(scrollRightAnim);
    }

    private void setupHeaderImageDragUpDown() {
        // Initialize your image resources
        headerImages.add(R.drawable.main_header_img_01);
        headerImages.add(R.drawable.main_header_img_02);
        headerImages.add(R.drawable.main_header_img_03);
        headerImages.add(R.drawable.main_header_img_04);
        headerImages.add(R.drawable.main_header_img_05);

        // Set up the ViewPager2
        ViewPager viewPager = binding.headerImage.headerMenuContentViewPager;

        // Ensure the adapter is properly initialized
        MainHeaderImageAdapter mainHeaderImageAdapter = new MainHeaderImageAdapter(this, headerImages);
        viewPager.setAdapter(mainHeaderImageAdapter);

        // Connect the DotsIndicator
        binding.headerImage.headerMenuContentDotsIndicator.setViewPager(viewPager);

        // Start the auto-scroll
        setupStartAutoScroll(viewPager);
    }

    private void setupStartAutoScroll(ViewPager viewPager) {
        handler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getAdapter() != null) {
                    currentPosition = (currentPosition + 1) % viewPager.getAdapter().getCount();
                    viewPager.setCurrentItem(currentPosition, true);
                    handler.postDelayed(this, 6000);
                }
            }
        };
        handler.postDelayed(autoScrollRunnable, 6000);
    }



    private void setupPedometerVisibilityButton() {
        Animation scrollLeftAnim = AnimationUtils.loadAnimation(this, R.anim.scroll_left_pedometer_slider);

//        binding.chatbot.chatbotPopupMessage.popupCloseButton.setOnClickListener(v -> binding.chatbot.chatbotPopupMessageContainer.setVisibility(View.GONE));
        binding.pedometer.pedometerIconButton.setOnClickListener(v ->
        {
            if (binding.pedometer.pedometerContainer.getVisibility() == View.VISIBLE) {
                ViewGroup.LayoutParams layoutParamsPedometer = binding.pedometer.stepCountContainer.getLayoutParams();
                layoutParamsPedometer.width = ViewGroup.LayoutParams.WRAP_CONTENT;

                int heightInPx = (int) (65 * getResources().getDisplayMetrics().density);
                layoutParamsPedometer.height = heightInPx;

                binding.pedometer.pedometerIconButton.setImageResource(R.drawable.walking_icon01);
                binding.pedometer.stepCountContainer.setLayoutParams(layoutParamsPedometer);
                binding.pedometer.stepCountContainer.startAnimation(scrollLeftAnim);
                binding.pedometer.pedometerContainer.setVisibility(View.GONE);
            } else {
                ViewGroup.LayoutParams layoutParamsPedometer = binding.pedometer.stepCountContainer.getLayoutParams();
                layoutParamsPedometer.width = ViewGroup.LayoutParams.MATCH_PARENT;

                int heightInPx = (int) (110 * getResources().getDisplayMetrics().density);
                layoutParamsPedometer.height = heightInPx;

                binding.pedometer.pedometerContainer.setVisibility(View.VISIBLE);
                binding.pedometer.pedometerIconButton.setImageResource(R.drawable.walking_icon02);
                binding.pedometer.stepCountContainer.startAnimation(scrollLeftAnim);
                binding.pedometer.stepCountContainer.setLayoutParams(layoutParamsPedometer);
            }
        });
    }

    private void setupNavigationDrawer(String userEmail) {
        dialogAdmin = new Dialog(MainActivity.this);
        dialogAdmin.setContentView(R.layout.activity_main_admin_details);
        dialogAdmin.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogAdmin.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_bg));
        dialogAdmin.setCancelable(false);

        binding.toolbar.menuButton.setOnClickListener(v -> {
            if (binding.navigationDrawer.getVisibility() == View.VISIBLE) {
                binding.toolbar.menuButton.setImageResource(R.drawable.menu_icon02);
                Animation scrollLeftAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_navigation_drawer);
                binding.navigationDrawer.startAnimation(scrollLeftAnim);
                binding.navigationDrawer.setVisibility(View.GONE);
            } else {
                binding.toolbar.menuButton.setImageResource(R.drawable.cross_icon03);
                Animation scrollRightAnim = AnimationUtils.loadAnimation(this, R.anim.slide_right_navigation_drawer);
                binding.navigationDrawer.startAnimation(scrollRightAnim);
                binding.navigationDrawer.setVisibility(View.VISIBLE);
            }
        });

        dialogFeedback = new Dialog(this);
        dialogFeedback.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFeedback.setContentView(R.layout.activity_main_feedback_bottom_navigation);

        binding.navigationDrawer.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.feedback) {

                // Set the dialog's window properties
                if (dialogFeedback.getWindow() != null) {
                    dialogFeedback.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialogFeedback.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_bg));
                    dialogFeedback.getWindow().setGravity(Gravity.BOTTOM);

                    // Apply animation using a style defined in styles.xml
                    dialogFeedback.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                }

                dialogFeedback.setCancelable(true);
                dialogFeedback.show();
            } else if (item.getItemId() == R.id.admin) {
                dialogAdmin.show();
            } else if (item.getItemId() == R.id.logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SignUpNameActivity.class));
                finish();
            }
            return true;
        });

        adminUserName = dialogAdmin.findViewById(R.id.adminUserNameInput);
        adminUserPassword = dialogAdmin.findViewById(R.id.adminUserPasswordInput);

        loginDialogButton = dialogAdmin.findViewById(R.id.adminLoginButton);
        loginDialogButton.setOnClickListener(v -> {
            String userNameOfAdmin = adminUserName.getText().toString().trim();
            String userPasswordOfAdmin = adminUserPassword.getText().toString().trim();

            if (userNameOfAdmin.equals("") &&
                    userPasswordOfAdmin.equals("")) {
                handleAdminLogin();
            }
            else {
                Toast.makeText(MainActivity.this, "Invalid Details Can't Access Admin Controls", Toast.LENGTH_SHORT).show();
                dialogAdmin.dismiss();
            }
        });

        closeDialogButton = dialogAdmin.findViewById(R.id.adminCloseButton);
        closeDialogButton.setOnClickListener(v -> {
            dialogAdmin.dismiss();
        });

        submitDialogButton = dialogFeedback.findViewById(R.id.userFeedbackSubmitButton);
        userFeedback = dialogFeedback.findViewById(R.id.userFeedbackInput);
        submitDialogButton.setOnClickListener(v -> {
            String feedback = userFeedback.getText().toString().trim();

            if (feedback.isEmpty()) {
                userFeedback.setError("Please Enter Your Feedback");
            } else {
                // Initialize Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Create a HashMap to store feedback
                Map<String, Object> feedbackData = new HashMap<>();
                feedbackData.put("Feedback", feedback);
                feedbackData.put("FeedbackTimeStamp", FieldValue.serverTimestamp()); // Save the time of submission

                // Define the desired format
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // Get the current date and time
                Date now = new Date();

                db.collection("Feedbacks")
                        .document(userEmail)
                        .collection("Submit Status -> ").document(sdf.format(now))
                        .set(feedbackData) // Add the feedback data
                        .addOnSuccessListener(docRef -> {
                            Toast.makeText(MainActivity.this, "Feedback Submitted", Toast.LENGTH_SHORT).show();
                            dialogFeedback.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void handleAdminLogin() {
        startActivity(new Intent(this, AdminActivity.class));
    }

    private void setupButtons() {
        TextView textView = new TextView(this);
        textView.setText("Coming Soon...");
        textView.setTextSize(18);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        textView.setPadding(50, 150, 50, 150);
        textView.setGravity(Gravity.CENTER);
        Dialog dialogMoreSportsMode = new Dialog(MainActivity.this);
        dialogMoreSportsMode.setContentView(textView);
        dialogMoreSportsMode.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogMoreSportsMode.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box_bg));
        dialogMoreSportsMode.setCancelable(true);

        binding.sportsMode.moreButton.setOnClickListener(v -> {
//            startActivity(new Intent(this, MoreSportsModeActivity.class));
            dialogMoreSportsMode.show();

        });

        binding.chatbot.floatingButton.setOnClickListener(v -> startActivity(new Intent(this, ChatBotActivity.class)));
        binding.toolbar.profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        binding.sportsMode.moreButton.setOnClickListener(v -> startActivity(new Intent(this, MoreSportsModeActivity.class)));
        binding.toolbar.refreshButton.setOnClickListener(v -> {
//            recreate();
//            Intent intent = getIntent();
//            finish();
//            startActivity(intent);
        });
    }

    private void setupPedometerPermission() {
        setupStartPedometer();
    }

    private void setupStartPedometer() {
        pedometerSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = pedometerSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounterSensor != null) {
            Log.d("Pedometer", "Step Counter Sensor is available.");
//            Toast.makeText(this, "Step Counter Sensor Is Available", Toast.LENGTH_SHORT).show();
            stepCounterSensorRunning = true;

            // Retrieve saved target from SharedPreferences
            SharedPreferences sharedPreferencesSavedTarget = getSharedPreferences("pedometerSavedTarget", MODE_PRIVATE);
            stepCountTarget = sharedPreferencesSavedTarget.getInt("target", 10000); // Default target: 10,000 steps

            // Update UI elements
            binding.pedometer.pedometerProgressBar.setMax(stepCountTarget);
            binding.pedometer.stepTarget.setText(String.valueOf(stepCountTarget));
            binding.pedometer.changeTargetInput.setText(String.valueOf(stepCountTarget));

            // Handle target input changes
            binding.pedometer.changeTargetAfterTakingInputButton.setOnClickListener(v -> {
                String input = binding.pedometer.changeTargetInput.getText().toString().trim();
                if (!input.isEmpty()) {
                    try {
                        int changedTarget = Integer.parseInt(input);

                        // Save new target to SharedPreferences
                        SharedPreferences.Editor preferenceTarget = sharedPreferencesSavedTarget.edit();
                        preferenceTarget.putInt("target", changedTarget).apply();

                        // Update UI
                        stepCountTarget = changedTarget;
                        binding.pedometer.stepTarget.setText(String.valueOf(changedTarget));
                        binding.pedometer.pedometerProgressBar.setMax(changedTarget);

                    }
                    catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid target value. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Target input cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            });

            // Register the sensor listener
            pedometerSensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("Pedometer", "Step Counter Sensor is not available.");
            Toast.makeText(this, "Step Counter Sensor Not Detected In The Device", Toast.LENGTH_SHORT).show();
            stepCounterSensorRunning = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = (int) event.values[0];

            // Get the current date
            SharedPreferences sharedPreferences = getSharedPreferences("PedometerData", MODE_PRIVATE);
            long lastResetTimestamp = sharedPreferences.getLong("lastResetTimestamp", 0); // Default to 0 if not set

            // Get today's date (in milliseconds since epoch)
            long currentTimestamp = System.currentTimeMillis();

            // Check if a new day has started (midnight has passed)
            if (isNewDay(lastResetTimestamp, currentTimestamp)) {
                // Reset step count and distance covered to 0
                stepCount = 0;
                distanceCovered = 0.0f;

                Map<String, Object> pedometerData = new HashMap<>();
                pedometerData.put("Steps", stepCount);
                pedometerData.put("Distance", distanceCovered);

                // Update SharedPreferences with reset values
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("steps", stepCount);
                editor.putFloat("distance", distanceCovered);
                editor.putLong("lastResetTimestamp", currentTimestamp); // Update the last reset timestamp
                editor.apply();

                // Update UI with reset values
                binding.pedometer.numberOfSteps.setText(String.valueOf(stepCount));
                binding.pedometer.coveredDistance.setText(String.format("%.2f", distanceCovered));
                binding.pedometer.pedometerProgressBar.setProgress(stepCount);
                binding.pedometer.stepTarget.setTextColor(ContextCompat.getColor(this, R.color.IconThemedColor4));
            }

            if (!stepCounterSensorRunning) {
                // Initialize the base step count when the sensor starts
                baseStepCount = stepCount;
                stepCounterSensorRunning = true;
            }

            if (stepCount >= stepCountTarget) {
                binding.pedometer.stepTarget.setText("Completed");
                binding.pedometer.stepTarget.setTextColor(ContextCompat.getColor(this, R.color.IconThemedColor5));
            }

            stepCount = stepCount - baseStepCount;

            binding.pedometer.numberOfSteps.setText(String.valueOf(stepCount));

            // Calculate distance covered (average step length = 0.762 meters)
            distanceCovered = (float) (stepCount * 0.762 / 1000.0);
            binding.pedometer.coveredDistance.setText(String.format("%.2f", distanceCovered));

            binding.pedometer.pedometerProgressBar.setProgress(stepCount);

            // Update SharedPreferences with the current step count and distance
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("steps", stepCount);
            editor.putFloat("distance", distanceCovered);
            editor.apply();

            binding.pedometer.stepCountText.setOnClickListener(v -> {
                Toast.makeText(this, "Steps Count - " + stepCount, Toast.LENGTH_SHORT).show();
            });

            setupPedometerForegroundNotification(stepCount, distanceCovered);
        }
    }

    private void setupPedometerForegroundNotification(int stepCount, float distanceCovered) {
        setupCreateNotificationChannel();

        // Intent to open the main activity when notification is clicked
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Ensure compatibility with Android 12+ using FLAG_IMMUTABLE
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "pedometer_updated")
                .setSmallIcon(R.drawable.logo_icon03)
                .setColor(ContextCompat.getColor(this, R.color.IconThemedColor4)) // Icon for the notification
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pedometer_icon02)) // Large icon (optional)
                .setContentTitle("PEDOMETER") // Title
                .setTicker("PEDOMETER") // Status bar message
                .setContentText("Steps Count - " + stepCount + "\nDistance Covered - " + distanceCovered + " Km") // Content Text
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority to make it noticeable
                .setOngoing(true) // Makes the notification persistent (cannot be dismissed by sliding)
                .setAutoCancel(false) // Ensures the notification doesn't cancel when clicked
                .setOnlyAlertOnce(true) // Prevents sound from repeating when the notification is updated
                .setContentIntent(pendingIntent); // Set the pending intent for notification click

        // Get the NotificationManager system service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification with a unique ID
        notificationManager.notify(1, builder.build());
    }

    private void setupCreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pedometer Notifications";
            String description = "Channel for pedometer notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("pedometer_updated", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Method to check if a new day has started
    private boolean isNewDay(long lastResetTimestamp, long currentTimestamp) {
        // Convert timestamps to days (milliseconds in a day)
        long oneDayInMillis = 24 * 60 * 60 * 1000;

        // Compare the day part of the timestamps (ignore the time part)
        return (currentTimestamp - lastResetTimestamp) >= oneDayInMillis;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    Log.d("SensorAccuracy", "High accuracy for step counter.");
//                    Toast.makeText(this, "High Accuracy Step Counter", Toast.LENGTH_SHORT).show();
                    break;

                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    Log.d("SensorAccuracy", "Medium accuracy for step counter.");
//                    Toast.makeText(this, "Medium Accuracy Step Counter", Toast.LENGTH_SHORT).show();
                    break;

                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    Log.d("SensorAccuracy", "Low accuracy for step counter.");
                    Toast.makeText(this, "Low Accuracy Step Counter", Toast.LENGTH_SHORT).show();
                    break;

                case SensorManager.SENSOR_STATUS_NO_CONTACT:
                    Log.d("SensorAccuracy", "No contact with step counter sensor.");
                    Toast.makeText(this, "No Contact With The Step Counter Sensor", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(this, "Unknown Accuracy Level For The Step Counter", Toast.LENGTH_SHORT).show();
                    Log.d("SensorAccuracy", "Unknown accuracy level for step counter.");
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pedometerSensorManager != null) {
            pedometerSensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pedometerSensorManager != null && stepCounterSensor != null) {
            pedometerSensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Step Counter Sensor not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (pedometerSensorManager != null) {
//            pedometerSensorManager.unregisterListener(this);
//        }
        setupPedometerPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setupPedometerPermission();
        setupCreateNotificationChannel();

        if (handler != null && autoScrollRunnable != null) {
            handler.removeCallbacks(autoScrollRunnable);
        }
    }

}
