package com.stream.zenfit.Adapter;

import com.stream.zenfit.R;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.divider.MaterialDivider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.ExerciseRepresentationActivity;
import com.stream.zenfit.ExerciseRepresentationSplashScreenActivity;
import com.stream.zenfit.Modal.ExerciseModeEquipmentModal;
import com.stream.zenfit.Modal.ExerciseModeModal;
import com.stream.zenfit.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExerciseModeAdapter extends RecyclerView.Adapter<ExerciseModeAdapter.ExerciseViewHolder> {

    private Context context;
    private List<ExerciseModeModal> exerciseList;
    List<ExerciseModeEquipmentModal> equipmentList;
    ExerciseModeEquipmentModal exerciseModeEquipmentModal;

    public ExerciseModeAdapter(Context context, List<ExerciseModeModal> exerciseList) {
        this.context = context;
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main_exercise_mode_recycler_view_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        if (position * 2 < exerciseList.size()) {
            ExerciseModeModal exercise1 = exerciseList.get(position * 2);
            holder.bindDataToContainer1(exercise1);
//            setupEquipmentDetailsCall(exercise1);
        }

        if ((position * 2) + 1 < exerciseList.size()) {
            ExerciseModeModal exercise2 = exerciseList.get((position * 2) + 1);
            holder.bindDataToContainer2(exercise2);
        }
        else {
            holder.hideContainer2();
        }
    }

    @Override
    public int getItemCount() {
        return (int) Math.ceil(exerciseList.size() / 2.0);
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView exerciseThumbnail01, exerciseThumbnail02;
        TextView exerciseDifficultyLevel01, exerciseDifficultyLevel02;
        ImageView exerciseIcon01, exerciseIcon02;
        TextView exerciseName01, exerciseName02;
        ConstraintLayout exerciseButton01, exerciseButton02;
        MaterialDivider materialDivider;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseThumbnail01 = itemView.findViewById(R.id.exerciseThumbnail01);
            exerciseThumbnail02 = itemView.findViewById(R.id.exerciseThumbnail02);
            exerciseDifficultyLevel01 = itemView.findViewById(R.id.exerciseDifficultyLevel01);
            exerciseDifficultyLevel02 = itemView.findViewById(R.id.exerciseDifficultyLevel02);
            exerciseIcon01 = itemView.findViewById(R.id.exerciseIcon01);
            exerciseIcon02 = itemView.findViewById(R.id.exerciseIcon02);
            exerciseName01 = itemView.findViewById(R.id.exerciseName01);
            exerciseName02 = itemView.findViewById(R.id.exerciseName02);
            exerciseButton01 = itemView.findViewById(R.id.exerciseButton01);
            exerciseButton02 = itemView.findViewById(R.id.exerciseButton02);
            materialDivider = itemView.findViewById(R.id.containerDivider);
        }

        public void bindDataToContainer1(ExerciseModeModal exercise1) {
            exerciseName01.setText(exercise1.getExerciseName().toUpperCase());

            Glide.with(context)
                    .load(exercise1.getExerciseIconLink())
                    .error(R.drawable.error_icon01)
                    .into(exerciseIcon01);

            Glide.with(context)
                    .load(exercise1.getExerciseImageLink01())
                    .error(R.drawable.error_icon01)
                    .into(exerciseThumbnail01);

            exerciseButton01.setOnClickListener(v -> navigateToExerciseDetails(exercise1));
            exerciseButton01.setVisibility(VISIBLE);
        }

        public void bindDataToContainer2(ExerciseModeModal exercise2) {
            exerciseName02.setText(exercise2.getExerciseName().toUpperCase());

            Glide.with(context)
                    .load(exercise2.getExerciseIconLink())
                    .error(R.drawable.error_icon01)
                    .into(exerciseIcon02);

            Glide.with(context)
                    .load(exercise2.getExerciseImageLink02())
                    .error(R.drawable.error_icon01)
                    .into(exerciseThumbnail02);

            exerciseButton02.setOnClickListener(v -> navigateToExerciseDetails(exercise2));
            exerciseButton02.setVisibility(VISIBLE);
            materialDivider.setVisibility(VISIBLE);
        }

        public void hideContainer2() {
            exerciseButton02.setVisibility(GONE);
            materialDivider.setVisibility(GONE);
        }

        private void navigateToExerciseDetails(ExerciseModeModal exercise) {

            Intent intent = new Intent(context, ExerciseRepresentationSplashScreenActivity.class);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            intent.putExtra("exerciseName", exercise.getExerciseName());
            intent.putExtra("exerciseIconLink", exercise.getExerciseIconLink());
            intent.putExtra("exerciseImageLink01", exercise.getExerciseEquipmentImage01Link());
            intent.putExtra("exerciseImageLink02", exercise.getExerciseEquipmentImage02Link());
            intent.putExtra("exerciseDifficultyLevel", exercise.getExerciseLevel());
            intent.putExtra("exerciseDescription", exercise.getExerciseDescription());
            intent.putExtra("exerciseVideoLink01", exercise.getExerciseEquipmentTutorialVideo01Link());
            intent.putExtra("exerciseVideoLink02", exercise.getExerciseEquipmentTutorialVideo02Link());

            context.startActivity(intent);

            Log.d("ExerciseModeAdapter", "Exercise Name: " + exercise.getExerciseName());
            Log.d("ExerciseModeAdapter", "Exercise Icon Link: " + exercise.getExerciseIconLink());
            Log.d("ExerciseModeAdapter", "Exercise Image Link 01: " + exercise.getExerciseEquipmentImage01Link());
            Log.d("ExerciseModeAdapter", "Exercise Image Link 02: " + exercise.getExerciseEquipmentImage02Link());
            Log.d("ExerciseModeAdapter", "Exercise Difficulty Level: " + exercise.getExerciseLevel());
            Log.d("ExerciseModeAdapter", "Exercise Description: " + exercise.getExerciseDescription());
            Log.d("ExerciseModeAdapter", "Exercise Video Link 01: " + exercise.getExerciseEquipmentTutorialVideo01Link());
            Log.d("ExerciseModeAdapter", "Exercise Video Link 02: " + exercise.getExerciseEquipmentTutorialVideo02Link());
        }
    }
}