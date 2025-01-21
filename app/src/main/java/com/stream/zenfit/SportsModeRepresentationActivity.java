package com.stream.zenfit;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Adapter.SportsModeAdapter;
import com.stream.zenfit.Modal.SportsModeModal;
import com.stream.zenfit.databinding.ActivitySportsModeRepresentationBinding;

import java.util.ArrayList;
import java.util.List;

public class SportsModeRepresentationActivity extends AppCompatActivity {

    ActivitySportsModeRepresentationBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    List<SportsModeModal> sportsList = new ArrayList<>();
    SportsModeAdapter adapter = new SportsModeAdapter(this, sportsList);
    String userID;
    String sportsName;
    String sportsItem;
    List<String> sportsItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();

        binding = ActivitySportsModeRepresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        fetchData();
    }

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

                            SportsModeModal sports = new SportsModeModal(name, iconLink, imageLink,
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