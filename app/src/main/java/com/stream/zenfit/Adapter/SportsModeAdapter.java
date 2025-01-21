package com.stream.zenfit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stream.zenfit.Modal.SportsModeModal;
import com.stream.zenfit.R;

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
        SportsModeModal sports = sportsList.get(position);

        holder.sportsName.setText(sports.getSportsName());
        Glide.with(context).load(sports.getImageLink()).into(holder.sportsImage);
    }

    @Override
    public int getItemCount() {
        return sportsList.size();
    }

    public static class SportsViewHolder extends RecyclerView.ViewHolder {
        TextView sportsName;
        ImageView sportsImage;

        public SportsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
