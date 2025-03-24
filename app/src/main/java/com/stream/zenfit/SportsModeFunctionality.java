package com.stream.zenfit;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Adapter.SportsModeAdapter;
import com.stream.zenfit.Modal.SportsModeModal;
import com.stream.zenfit.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class SportsModeFunctionality extends MainActivity {

    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityMainBinding binding;
    private ShimmerFrameLayout shimmerFrameLayout;
    private SportsModeAdapter sportsModeAdapter;
    private List<SportsModeModal> sportsList = new ArrayList<>();

//    public SportsModeFunctionality(Context context) {
//        this.context = context;
//    }

    public void sportsMode() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        String userEmail = firebaseAuth.getCurrentUser().getEmail();
        String userId = firebaseAuth.getCurrentUser().getUid();

        if (userEmail != null) {
            CollectionReference sportsCollection = firebaseFirestore.collection("Sports Mode");

            // Retrieve all documents in the "Sports Mode" collection
            sportsCollection.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Process each document ID and get the data
                                String sportsName = document.getString("sportsName");
                                String iconLink = document.getString("iconLink");
                                String imageLink = document.getString("imageLink");
                                String averageWeight = document.getString("AverageWeight");
                                String averageHeight = document.getString("AverageHeight");
                                String averageCalories = document.getString("AverageCalories");
                                String ytLinkLessWeight = document.getString("ytLinkLessWeight");
                                String ytLinkMoreWeight = document.getString("ytLinkMoreWeight");
                                String ytLinkLessHeight = document.getString("ytLinkLessHeight");
                                String ytLinkMoreHeight = document.getString("ytLinkMoreHeight");
                                String ytLinkLessCalories = document.getString("ytLinkLessCalories");
                                String ytLinkMoreCalories = document.getString("ytLinkMoreCalories");

                                // Create a modal object to store data
//                                SportsModeModal sportsModeModal = new SportsModeModal(
//                                        sportsName, iconLink, imageLink,
//                                        ytLinkLessWeight, ytLinkMoreWeight,
//                                        ytLinkLessHeight, ytLinkMoreHeight,
//                                        ytLinkLessCalories, ytLinkMoreCalories
//                                );

                                // Add it to the sports list
//                                sportsList.add(sportsModeModal);
                            }

                            // Now bind data to RecyclerView
                            loadData();
                        } else {
                            Log.d("SportsModeFunctionality", "No documents found or query failed.");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("SportsModeFunctionality", "Error retrieving documents", e));
        }
    }

    private void loadData() {
        // Simulate a data load delay using a handler (replace with actual data fetch logic)
        new android.os.Handler().postDelayed(() -> {
            // Once data is loaded, bind it to the RecyclerView
            sportsModeAdapter = new SportsModeAdapter(this, sportsList);
            binding.sportsMode.sportsModeOptionsRecyclerView.setAdapter(sportsModeAdapter);

            // Stop shimmer effect and update visibility
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            binding.sportsMode.sportsModeOptionsRecyclerView.setVisibility(View.VISIBLE);

        }, 2000); // Simulated delay of 2 seconds
    }
}