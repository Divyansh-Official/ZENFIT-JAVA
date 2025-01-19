package com.stream.zenfit;

import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.stream.zenfit.Model.SportsModeModel;

class SportsMode extends MainActivity {
    private void fetchData() {
        firebaseFirestore.collection("Users").document(userID)
                .collection("Sports Mode").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String name = document.getString("SportsName");
                            String iconLink = document.getString("SportsIconLink");
                            String imageLink = document.getString("SportsImageLink");
                            String ytLinkLessWeight = document.getString("YTLinkLessWeight");
                            String ytLinkMoreWeight = document.getString("YTLinkMoreWeight");
                            String ytLinkLessHeight = document.getString("YTLinkLessHeight");
                            String ytLinkMoreHeight = document.getString("YTLinkMoreHeight");
                            String ytLinkLessCalories = document.getString("YTLinkLessCalories");
                            String ytLinkMoreCalories = document.getString("YTLinkMoreCalories");

                            SportsModeModel sports = new SportsModeModel(name, iconLink, imageLink,
                                    ytLinkLessWeight, ytLinkMoreWeight, ytLinkLessHeight,
                                    ytLinkMoreHeight, ytLinkLessCalories, ytLinkMoreCalories);

                            sportsList.add(sports);
                        }

                        adapter.notifyDataSetChanged(); // Notify adapter about data change
                    } else {
                        Toast.makeText(this, "Failed to fetch sports data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
