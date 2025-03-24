package com.stream.zenfit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stream.zenfit.Modal.SportsModeModal;
import com.stream.zenfit.R;
import com.stream.zenfit.SportsModeRepresentationActivity;

import java.util.List;

public class SportsModeAdapter extends RecyclerView.Adapter<SportsModeAdapter.SportsViewHolder> {

    private Context context;
    private List<SportsModeModal> sportsList;

    public SportsModeAdapter(Context context, List<SportsModeModal> sportsList) {
        this.context = context;
        this.sportsList = sportsList;
    }

    @NonNull
    @Override
    public SportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main_sports_mode_recycler_view_item, parent, false);
        return new SportsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SportsViewHolder holder, int position) {
        SportsModeModal sportsModal = sportsList.get(position);

        // Load the sport name
        holder.sportsName.setText(sportsModal.getSportsName());

        // Load the icon using Glide
        Glide.with(context)
                .load(sportsModal.getIconLink()) // Load icon image from the link
                .error(R.drawable.error_icon01) // Optional: Fallback error icon if image loading fails
                .into(holder.sportsIcon);

        // Set click listener to pass data to the next activity
        holder.sportsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, SportsModeRepresentationActivity.class);
            intent.putExtra("sportsName", sportsModal.getSportsName());
            intent.putExtra("iconLink", sportsModal.getIconLink());
            intent.putExtra("imageLink", sportsModal.getImageLink());
            intent.putExtra("averageWeight", sportsModal.getAverageWeight());
            intent.putExtra("averageHeight", sportsModal.getAverageHeight());
            intent.putExtra("averageCalories", sportsModal.getAverageCalories());
            intent.putExtra("weightFromCriteria", sportsModal.getWeightFromCriteria());
            intent.putExtra("heightFromCriteria", sportsModal.getHeightFromCriteria());
            intent.putExtra("caloriesFromCriteria", sportsModal.getCaloriesFromCriteria());
            intent.putExtra("weightToCriteria", sportsModal.getWeightToCriteria());
            intent.putExtra("heightToCriteria", sportsModal.getHeightToCriteria());
            intent.putExtra("caloriesToCriteria", sportsModal.getCaloriesToCriteria());
            intent.putExtra("ytLinkLessWeight", sportsModal.getYtLinkLessWeight());
            intent.putExtra("ytLinkLessHeight", sportsModal.getYtLinkLessHeight());
            intent.putExtra("ytLinkLessCalories", sportsModal.getYtLinkLessCalories());

            // Information-related fields
            intent.putExtra("informationTitle", sportsModal.getInformationTitle());
            intent.putExtra("informationText1", sportsModal.getInformationText1());
            intent.putExtra("informationText2", sportsModal.getInformationText2());
            intent.putExtra("informationText3", sportsModal.getInformationText3());
            intent.putExtra("informationText4", sportsModal.getInformationText4());
            intent.putExtra("informationText5", sportsModal.getInformationText5());

            // Tutorial links
            intent.putExtra("tutorialLink1", sportsModal.getTutorialLink1());
            intent.putExtra("tutorialLink2", sportsModal.getTutorialLink2());
            intent.putExtra("tutorialLink3", sportsModal.getTutorialLink3());
            intent.putExtra("tutorialLink4", sportsModal.getTutorialLink4());
            intent.putExtra("tutorialLink5", sportsModal.getTutorialLink5());

            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return sportsList.size();
    }

    public static class SportsViewHolder extends RecyclerView.ViewHolder {
        TextView sportsName;
        ImageView sportsIcon;
        ConstraintLayout sportsButton;

        public SportsViewHolder(@NonNull View itemView) {
            super(itemView);
            sportsName = itemView.findViewById(R.id.sportsModeItemText); // Ensure this matches your XML ID
            sportsIcon = itemView.findViewById(R.id.sportsModeItemIcon); // Ensure this matches your XML ID
            sportsButton = itemView.findViewById(R.id.sportsModeItemButton); // Ensure this matches your XML ID
        }
    }
}

