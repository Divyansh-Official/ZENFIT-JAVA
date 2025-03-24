package com.stream.zenfit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.stream.zenfit.Adapter.MoreSportsModeAdapter;
import com.stream.zenfit.Modal.SportsModeModal;
import com.stream.zenfit.databinding.ActivitySportsModeRepresentationBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class SportsModeRepresentationActivity extends AppCompatActivity {

    ActivitySportsModeRepresentationBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    float aWeight, aHeight, aCalories;
    List<SportsModeModal> sportsList = new ArrayList<>();
    MoreSportsModeAdapter adapter = new MoreSportsModeAdapter(this, sportsList);
    String userID;
    String sportsName, iconLink, imageLink, averageWeight, averageHeight, averageCalories;
    String weightFromCriteria, heightFromCriteria, caloriesFromCriteria;
    String weightToCriteria, heightToCriteria, caloriesToCriteria;
    String ytLinkLessWeight, ytLinkLessHeight, ytLinkLessCalories;
    String informationTitle, informationText1, informationText2, informationText3, informationText4, informationText5;
    String tutorialLink1, tutorialLink2, tutorialLink3, tutorialLink4, tutorialLink5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();

        binding = ActivitySportsModeRepresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupChangeStatusBarColor();
        setupAnimations();
        fetchData();

        binding.sportsBasicData.backButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupAnimations() {
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.scroll_down_sports_input_anim);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.scroll_up_sports_input_anim);

        binding.sportsBasicData.sportsModeHeaderContainer.setAnimation(slideDown);

//        binding.sportsBasicData.upDownButton.setOnClickListener(v -> {
//            if (binding.sportsBasicData.inputReceiveContainer.getVisibility() == VISIBLE) {
//                binding.sportsBasicData.upDownButton.setImageResource(R.drawable.bottom_arrow_icon01);
//                binding.sportsBasicData.inputReceiveContainer.setAnimation(slideUp);
//                binding.sportsBasicData.upDownButton.setAnimation(slideUp);
//                binding.sportsBasicData.inputReceiveContainer.setVisibility(GONE);
//            }
//            else {
//                binding.sportsBasicData.upDownButton.setImageResource(R.drawable.up_arrow_icon01);
//                binding.sportsBasicData.inputReceiveContainer.setAnimation(slideDown);
//                binding.sportsBasicData.upDownButton.setAnimation(slideDown);
//                binding.sportsBasicData.inputReceiveContainer.setVisibility(VISIBLE);
//            }
//        });
    }

    private void fetchData() {
        Intent receiveData = getIntent();

        // Fetch data from Intent using the new keys
        sportsName = receiveData.getStringExtra("sportsName");
        iconLink = receiveData.getStringExtra("iconLink");
        imageLink = receiveData.getStringExtra("imageLink");
        averageWeight = receiveData.getStringExtra("averageWeight");
        averageHeight = receiveData.getStringExtra("averageHeight");
        averageCalories = receiveData.getStringExtra("averageCalories");

        weightFromCriteria = receiveData.getStringExtra("weightFromCriteria");
        heightFromCriteria = receiveData.getStringExtra("heightFromCriteria");
        caloriesFromCriteria = receiveData.getStringExtra("caloriesFromCriteria");

        weightToCriteria = receiveData.getStringExtra("weightToCriteria");
        heightToCriteria = receiveData.getStringExtra("heightToCriteria");
        caloriesToCriteria = receiveData.getStringExtra("caloriesToCriteria");

        ytLinkLessWeight = receiveData.getStringExtra("ytLinkLessWeight");
        ytLinkLessHeight = receiveData.getStringExtra("ytLinkLessHeight");
        ytLinkLessCalories = receiveData.getStringExtra("ytLinkLessCalories");

        // Fetch information and tutorial text
        informationTitle = receiveData.getStringExtra("informationTitle");
        informationText1 = receiveData.getStringExtra("informationText1");
        informationText2 = receiveData.getStringExtra("informationText2");
        informationText3 = receiveData.getStringExtra("informationText3");
        informationText4 = receiveData.getStringExtra("informationText4");
        informationText5 = receiveData.getStringExtra("informationText5");

        tutorialLink1 = receiveData.getStringExtra("tutorialLink1");
        tutorialLink2 = receiveData.getStringExtra("tutorialLink2");
        tutorialLink3 = receiveData.getStringExtra("tutorialLink3");
        tutorialLink4 = receiveData.getStringExtra("tutorialLink4");
        tutorialLink5 = receiveData.getStringExtra("tutorialLink5");

        putData(sportsName, iconLink, imageLink, averageWeight, averageHeight, averageCalories, ytLinkLessWeight, ytLinkLessHeight, ytLinkLessCalories, weightFromCriteria, heightFromCriteria, caloriesFromCriteria, weightToCriteria, heightToCriteria, caloriesToCriteria, informationTitle, informationText1, informationText2, informationText3, informationText4, informationText5, tutorialLink1, tutorialLink2, tutorialLink3, tutorialLink4, tutorialLink5);
    }

    private void putData(String sportsName, String iconLink, String imageLink, String averageWeight, String averageHeight, String averageCalories, String ytLinkLessWeight, String ytLinkLessHeight, String ytLinkLessCalories, String weightFromCriteria, String heightFromCriteria, String caloriesFromCriteria, String weightToCriteria, String heightToCriteria, String caloriesToCriteria, String informationTitle, String informationText1, String informationText2, String informationText3, String informationText4, String informationText5, String tutorialLink1, String tutorialLink2, String tutorialLink3, String tutorialLink4, String tutorialLink5) {
        binding.sportsBasicData.sportsName.setText(sportsName);

        Glide.with(this)
                .load(iconLink)
                .error(R.drawable.error_icon01)
                .into(binding.sportsBasicData.sportsIcon);

        Glide.with(this)
                .load(imageLink)  // imageLink is the URL of the image
                .error(R.drawable.error_icon01)  // Optional: image to show in case of an error
                .into(binding.sportsBasicData.backgroundOfTheContainer.backgroundImage);

        aWeight = Float.parseFloat(averageWeight);  // Convert to float
        aHeight = Float.parseFloat(averageHeight);  // Convert to float
        aCalories = Float.parseFloat(averageCalories);  // Convert to float

        Log.d("SportsModeRepresentation", "aWeight: " + aWeight);
        Log.d("SportsModeRepresentation", "aHeight: " + aHeight);
        Log.d("SportsModeRepresentation", "aCalories: " + aCalories);

        Log.d("SportsModeRepresentation", "informationTitle: " + informationTitle);
        Log.d("SportsModeRepresentation", "informationText1: " + informationText1);
        Log.d("SportsModeRepresentation", "informationText2: " + informationText2);
        Log.d("SportsModeRepresentation", "informationText3: " + informationText3);
        Log.d("SportsModeRepresentation", "informationText4: " + informationText4);
        Log.d("SportsModeRepresentation", "informationText5: " + informationText5);


        // Update the UI with the information text
        binding.sportInformationLayout.informationTitle.setText(informationTitle + "  F O R   -   " + "[ " + sportsName + " ]");
        binding.sportInformationLayout.informationText01.setText(informationText1);
        binding.sportInformationLayout.informationText02.setText(informationText2);
        binding.sportInformationLayout.informationText03.setText(informationText3);
        binding.sportInformationLayout.informationText04.setText(informationText4);
        binding.sportInformationLayout.informationText05.setText(informationText5);

        binding.sportsBasicData.calculateButton.setOnClickListener(v -> {
            String userHeight = binding.sportsBasicData.userHeightInput.getText().toString().trim();
            String userWeight = binding.sportsBasicData.userWeightInput.getText().toString().trim();
            String userCalorie = binding.sportsBasicData.userCalorieInput.getText().toString().trim();

            
        });

    }

//    private void calculateWeight(float aWeight, String lessWeightUrl, String moreWeightUrl) {
//        String userWeight = binding.sportsBasicData.userWeightInput.getText().toString().trim();
//
//        if (!userWeight.isEmpty()) {
//            float weight = Float.parseFloat(userWeight);
//
//            YouTubePlayerView youTubePlayerView = binding.sportsVideos.videoViewWeight;
//            getLifecycle().addObserver(youTubePlayerView);
//
//            if (weight < aWeight - 5) {
//                playYouTubeVideo(youTubePlayerView, lessWeightUrl);
//                youTubePlayerView.setVisibility(View.VISIBLE);
//                binding.sportsVideos.weightContainer.setVisibility(VISIBLE);
//                binding.sportsVideos.perfectWeightText.setVisibility(View.GONE);
//            } else if (weight > aWeight + 5) {
//                playYouTubeVideo(youTubePlayerView, moreWeightUrl);
//                youTubePlayerView.setVisibility(View.VISIBLE);
//                binding.sportsVideos.weightContainer.setVisibility(VISIBLE);
//                binding.sportsVideos.perfectWeightText.setVisibility(View.GONE);
//            } else {
//                youTubePlayerView.setVisibility(View.GONE);
//                binding.sportsVideos.weightContainer.setVisibility(VISIBLE);
//                binding.sportsVideos.perfectWeightText.setVisibility(View.VISIBLE);
//            }
//        } else {
//            binding.sportsBasicData.userWeightInput.setError("Please enter your weight");
//        }
//    }
//
//    private void calculateHeight(float aHeight, String lessHeightUrl, String moreHeightUrl) {
//        String userHeight = binding.sportsBasicData.userHeightInput.getText().toString().trim();
//
//        if (!userHeight.isEmpty()) {
//            float height = Float.parseFloat(userHeight);
//
//            YouTubePlayerView youTubePlayerView = binding.sportsVideos.videoViewHeight;
//            getLifecycle().addObserver(youTubePlayerView);
//
//            if (height < aHeight - 0.3) {
//                playYouTubeVideo(youTubePlayerView, lessHeightUrl);
//                youTubePlayerView.setVisibility(View.VISIBLE);
//                binding.sportsVideos.heightContainer.setVisibility(VISIBLE);
//                binding.sportsVideos.perfectHeightText.setVisibility(View.GONE);
//            } else if (height > aHeight + 0.3) {
//                playYouTubeVideo(youTubePlayerView, moreHeightUrl);
//                youTubePlayerView.setVisibility(View.VISIBLE);
//                binding.sportsVideos.heightContainer.setVisibility(VISIBLE);
//                binding.sportsVideos.perfectHeightText.setVisibility(View.GONE);
//            } else {
//                youTubePlayerView.setVisibility(View.GONE);
//                binding.sportsVideos.heightContainer.setVisibility(VISIBLE);
//                binding.sportsVideos.perfectHeightText.setVisibility(View.VISIBLE);
//            }
//        } else {
//            binding.sportsBasicData.userHeightInput.setError("Please enter your height");
//        }
//    }
//
//    private void calculateCalories(float aCalories, String lessCaloriesUrl, String moreCaloriesUrl) {
//        String userCalorie = binding.sportsBasicData.userCalorieInput.getText().toString().trim();
//
//        if (!userCalorie.isEmpty()) {
//            float calorie = Float.parseFloat(userCalorie);
//
//            YouTubePlayerView youTubePlayerView = binding.sportsVideos.videoViewCalorie;
//            getLifecycle().addObserver(youTubePlayerView);
//
//            if (calorie < aCalories - 500) {
//                playYouTubeVideo(youTubePlayerView, lessCaloriesUrl);
//                youTubePlayerView.setVisibility(View.VISIBLE);
//                binding.sportsVideos.calorieContainer.setVisibility(VISIBLE);
//                binding.sportsVideos.perfectCalorieText.setVisibility(View.GONE);
//            } else if (calorie > aCalories + 500) {
//                playYouTubeVideo(youTubePlayerView, moreCaloriesUrl);
//                youTubePlayerView.setVisibility(View.VISIBLE);
//                binding.sportsVideos.calorieContainer.setVisibility(VISIBLE);
//                binding.sportsVideos.perfectCalorieText.setVisibility(View.GONE);
//            } else {
//                youTubePlayerView.setVisibility(View.GONE);
//                binding.sportsVideos.calorieContainer.setVisibility(VISIBLE);
//                binding.sportsVideos.perfectCalorieText.setVisibility(View.VISIBLE);
//            }
//        } else {
//            binding.sportsBasicData.userCalorieInput.setError("Please enter your calories");
//        }
//    }
//
//    // Helper function to play video in the correct YouTubePlayerView
//    private void playYouTubeVideo(YouTubePlayerView youTubePlayerView, String videoUrl) {
//        // Add lifecycle observer for cleanup
//        getLifecycle().addObserver(youTubePlayerView);
//
//        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                // Extract the video ID from the URL
//                String videoId = extractYouTubeVideoId(videoUrl);
//                if (videoId != null) {
//                    youTubePlayer.loadVideo(videoId, 0); // Load and play the video
//                } else {
//                    Log.e("YouTubePlayer", "Invalid YouTube URL: " + videoUrl);
//                    Toast.makeText(getApplicationContext(), "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
//                Log.e("YouTubePlayer", "YouTube Player Error: " + error.name());
//                Toast.makeText(getApplicationContext(), "Error loading video", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Helper function to extract the video ID from a YouTube URL
//    private String extractYouTubeVideoId(String url) {
//        String videoId = null;
//        String regex = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:[^/\\n\\s]+/.+/|(?:v|e(?:mbed)?)|.*[?&]v=)|youtu\\.be/)([^\"&?/\\s]{11})";
//        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(url);
//        if (matcher.find()) {
//            videoId = matcher.group(1);
//        }
//        return videoId;
//    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }
}