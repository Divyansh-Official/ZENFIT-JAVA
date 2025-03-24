package com.stream.zenfit.MainActivityClasses;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Adapter.ExerciseModeAdapter;
import com.stream.zenfit.Adapter.SportsModeAdapter;
import com.stream.zenfit.MainActivity;
import com.stream.zenfit.Modal.ExerciseModeModal;
import com.stream.zenfit.Modal.SportsModeModal;
import com.stream.zenfit.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainSportsMode {

    ActivityMainBinding binding;
    FirebaseFirestore firebaseFirestore;
    SportsModeAdapter sportsModeAdapter;
    Context context;
    private List<SportsModeModal> sportsList = new ArrayList<>();

    public MainSportsMode(Context context, ActivityMainBinding binding){
        this.context = context;
        this.binding = binding;
    }

    public void setupSportsRequirements(){

        setupSportsModeRecyclerView();

    }


    public void setupSportsModeRecyclerView() {
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
                                    loadDataSports();
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
                        Toast.makeText(context, "No Sports Data Found In The Database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Firestore errors
                    Log.e("SportsModeFunctionality", "Error retrieving documents", e);
                    Toast.makeText(context, "Error retrieving documents", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadDataSports() {

        binding.sportsMode.sportsModeOptionsRecyclerViewShimmerEffectPlaceHolder.setVisibility(View.INVISIBLE);
        binding.sportsMode.sportsModeOptions.setVisibility(View.GONE);

        Log.d("SportsModeLoadData", "sportsList size: " + sportsList.size());
        Log.d("SportsModeLoadData", "sportsList data: " + sportsList.toString());

        // Pass data to the adapter
        sportsModeAdapter = new SportsModeAdapter(context, sportsList);

        if (sportsModeAdapter != null) {
            Log.d("SportsModeLoadData", "sportsModeAdapter is successfully set.");
        } else {
            Log.e("SportsModeLoadData", "Failed to set sportsModeAdapter.");
        }

        Log.d("SportsModeLoadData", "Data Loaded");
        Log.d("SportsModeLoadData", "Data Size: " + sportsList.size());
        Log.d("SportsModeLoadData", "Data: " + sportsList.toString());

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.sportsMode.sportsModeOptionsRecyclerView.setLayoutManager(layoutManager);

        // Set the adapter to the RecyclerView
        binding.sportsMode.sportsModeOptionsRecyclerView.setAdapter(sportsModeAdapter);

        // Notify the adapter that data has changed
        if (sportsModeAdapter != null) {
            sportsModeAdapter.notifyDataSetChanged();
        }

        if (binding.sportsMode.sportsModeOptionsRecyclerView != null) {
            Log.d("SportsModeLoadData", "sportsModeOptionsRecyclerView is successfully set.");

            binding.sportsMode.sportsModeOptions.setVisibility(View.VISIBLE);
            binding.sportsMode.sportsModeOptionsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            Log.e("SportsModeLoadData", "Failed to set sportsModeOptionsRecyclerView.");
        }
    }


}
