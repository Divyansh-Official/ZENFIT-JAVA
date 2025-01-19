package com.stream.zenfit;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.stream.zenfit.databinding.ActivityMoreSportsModeBinding;

import java.util.ArrayList;

public class MoreSportsModeActivity extends AppCompatActivity {

    ActivityMoreSportsModeBinding binding;
    ArrayList<String> sportsModeList = new ArrayList<>();
    ArrayList<Integer> sportsModeImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_more_sports_mode);

        binding = ActivityMoreSportsModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sportsName();
        sportsImage();

    }

    private void sportsName() {
        sportsModeList.add("FOOTBALL");
        sportsModeList.add("TENNIS");
        sportsModeList.add("CRICKET");
        sportsModeList.add("BADMINTON");
        sportsModeList.add("BASEBALL");
        sportsModeList.add("ATHLETICS");
    }

    private void sportsImage() {
        sportsModeImageList.add(R.drawable.football_img);
        sportsModeImageList.add(R.drawable.tennis_img);
        sportsModeImageList.add(R.drawable.cricket_img);
        sportsModeImageList.add(R.drawable.badminton_img);
        sportsModeImageList.add(R.drawable.baseball_img);
        sportsModeImageList.add(R.drawable.athletics_img);
    }
}