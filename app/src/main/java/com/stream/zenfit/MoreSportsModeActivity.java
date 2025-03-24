package com.stream.zenfit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Adapter.MoreSportsModeAdapter;
import com.stream.zenfit.Adapter.SportsModeAdapter;
import com.stream.zenfit.Modal.SportsModeModal;
import com.stream.zenfit.databinding.ActivityMoreSportsModeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoreSportsModeActivity extends AppCompatActivity {

    ActivityMoreSportsModeBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    MoreSportsModeAdapter sportsModeAdapter;
    private List<SportsModeModal> sportsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding = ActivityMoreSportsModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setupChangeStatusBarColor();
        sportsMode();

    }

    public void sportsMode() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference sportsCollection = firebaseFirestore.collection("Sports Mode");

        // Retrieve all documents inside the "Sports Mode" collection
        sportsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // StringBuilder to store all document names for logging/debugging
                        StringBuilder documentNames = new StringBuilder();

                        // Loop through each document to fetch individual fields
                        for (DocumentSnapshot document : task.getResult()) {
                            String documentName = document.getId(); // Get the document ID (name)

                            // Append the document name for logging/debugging
                            documentNames.append(documentName).append("\n");

                            Map<String, Object> documentData = document.getData();
                            Log.d("SportsMode", "Document Name: " + documentName);
                            Log.d("SportsMode", "Document Data: " + documentData);

                            if (documentData != null) {
                                String sportsName = document.getString("SportsName");
                                String iconLink = document.getString("SportsIconLink");
                                String imageLink = document.getString("SportsImageLink");
                                String averageWeight = document.getString("AverageWeight");
                                String averageHeight = document.getString("AverageHeight");
                                String averageCalories = document.getString("AverageCalories");
                                String weightFromCriteria = document.getString("WeightFromCriteria");
                                String heightFromCriteria = document.getString("HeightFromCriteria");
                                String caloriesFromCriteria = document.getString("CaloriesFromCriteria");
                                String weightToCriteria = document.getString("WeightToCriteria");
                                String heightToCriteria = document.getString("HeightToCriteria");
                                String caloriesToCriteria = document.getString("CaloriesToCriteria");
                                String ytLinkLessWeight = document.getString("YTLinkLessWeight");
                                String ytLinkLessHeight = document.getString("YTLinkLessHeight");
                                String ytLinkLessCalories = document.getString("YTLinkLessCalories");
                                String informationTitle = document.getString("InformationTitle");  // Retrieve InformationTitle
                                String informationText01 = document.getString("InformationText01");
                                String informationText02 = document.getString("InformationText02");
                                String informationText03 = document.getString("InformationText03");
                                String informationText04 = document.getString("InformationText04");
                                String informationText05 = document.getString("InformationText05");
                                String tutorialLink01 = document.getString("TutorialLink01");
                                String tutorialLink02 = document.getString("TutorialLink02");
                                String tutorialLink03 = document.getString("TutorialLink03");
                                String tutorialLink04 = document.getString("TutorialLink04");
                                String tutorialLink05 = document.getString("TutorialLink05");

                                // Logging the retrieved data
                                Log.d("SportsMode", "Sports Name: " + sportsName);
                                Log.d("SportsMode", "Information Title: " + informationTitle);  // Log the information title
                                // Other logs...

                                // Creating a new instance of SportsModeModel with all the parameters
                                SportsModeModal newSportsMode = new SportsModeModal(
                                        sportsName, iconLink, imageLink, averageWeight, averageHeight, averageCalories,
                                        weightFromCriteria, heightFromCriteria, caloriesFromCriteria,
                                        weightToCriteria, heightToCriteria, caloriesToCriteria,
                                        ytLinkLessWeight, ytLinkLessHeight, ytLinkLessCalories,
                                        informationTitle,  // Pass the information title
                                        informationText01, informationText02, informationText03, informationText04, informationText05,
                                        tutorialLink01, tutorialLink02, tutorialLink03, tutorialLink04, tutorialLink05
                                );

                                // Adding the new SportsModeModel instance to the list
                                boolean isAdded = sportsList.add(newSportsMode);

                                if (isAdded) {
                                    Log.d("SportsMode", "Item successfully added: " + sportsName);
                                    loadData();
                                } else {
                                    Log.e("SportsMode", "Failed to add item: " + sportsName);
                                }
                            }


                        }

                        // Log all document names for debugging
                        Log.d("SportsMode", "Fetched Document Names:\n" + documentNames.toString());

                    }
                    else {
                        // Handle failure
                        Log.d("SportsModeFunctionality", "No documents found or query failed.");
                        Toast.makeText(MoreSportsModeActivity.this, "No Sports Data Found In The Database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Firestore errors
                    Log.e("SportsModeFunctionality", "Error retrieving documents", e);
                    Toast.makeText(MoreSportsModeActivity.this, "Error retrieving documents", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadData() {
        // Show shimmer effect while processing the data
//        shimmerFrameLayout.startShimmer();
//        shimmerFrameLayout.setVisibility(View.VISIBLE);
//        binding.sportsMode.sportsModeOptions.setVisibility(View.GONE);

        Log.d("SportsModeLoadData", "sportsList size: " + sportsList.size());
        Log.d("SportsModeLoadData", "sportsList data: " + sportsList.toString());

        // Pass data to the adapter
        sportsModeAdapter = new MoreSportsModeAdapter(this, sportsList);

        if (sportsModeAdapter != null) {
            Log.d("SportsModeLoadData", "sportsModeAdapter is successfully set.");
        } else {
            Log.e("SportsModeLoadData", "Failed to set sportsModeAdapter.");
        }

        Log.d("SportsModeLoadData", "Data Loaded");
        Log.d("SportsModeLoadData", "Data Size: " + sportsList.size());
        Log.d("SportsModeLoadData", "Data: " + sportsList.toString());

        binding.sportsModeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter to the RecyclerView
        binding.sportsModeRecyclerView.setAdapter(sportsModeAdapter);

        // Notify the adapter that data has changed
        if (sportsModeAdapter != null) {
            sportsModeAdapter.notifyDataSetChanged();
        }

        if (binding.sportsModeRecyclerView != null) {
            Log.d("SportsModeLoadData", "sportsModeOptionsRecyclerView is successfully set.");
//            shimmerFrameLayout.stopShimmer();
//            shimmerFrameLayout.setVisibility(View.GONE);
//            binding.sportsMode.sportsModeOptions.setVisibility(View.VISIBLE);
//            binding.sportsMode.sportsModeOptionsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            Log.e("SportsModeLoadData", "Failed to set sportsModeOptionsRecyclerView.");
        }
    }

    private void setupChangeStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.Dark3));
    }
}