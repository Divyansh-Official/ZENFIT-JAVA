package com.stream.zenfit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.stream.zenfit.Modal.ExerciseModeEquipmentModal;
import com.stream.zenfit.R;
import java.util.ArrayList;

public class MainExerciseModeEquipmentAdapter extends RecyclerView.Adapter<MainExerciseModeEquipmentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ExerciseModeEquipmentModal> exerciseModeEquipmentModalList;
    private String exerciseName;

    public MainExerciseModeEquipmentAdapter(Context context, ArrayList<ExerciseModeEquipmentModal> exerciseModeEquipmentModalList, String exerciseName) {
        this.context = context;
        this.exerciseModeEquipmentModalList = exerciseModeEquipmentModalList;
        this.exerciseName = exerciseName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_exercise_mode_representation_equipment_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseModeEquipmentModal equipment = exerciseModeEquipmentModalList.get(position);

        holder.equipmentName.setText(equipment.getExerciseEquipmentName());
        holder.equipmentIconLink.setText(equipment.getExerciseEquipmentIconLink());
        holder.equipmentImageLink01.setText(equipment.getExerciseEquipmentImage01Link());
        holder.equipmentImageLink02.setText(equipment.getExerciseEquipmentImage02Link());
        holder.equipmentTutorialVideoLink01.setText(equipment.getExerciseEquipmentTutorialVideo01Link());
        holder.equipmentTutorialVideoLink02.setText(equipment.getExerciseEquipmentTutorialVideo02Link());
        holder.equipmentNumberOfSets.setText(String.valueOf(equipment.getExerciseEquipmentSets()));
        holder.equipmentNumberOfReps.setText(String.valueOf(equipment.getExerciseEquipmentReps()));
        holder.equipmentDescription.setText(equipment.getExerciseEquipmentDescription());

        holder.equipmentHeader.setText("E Q U I P M E N T   D E T A I L S  OF   " + exerciseName);

    }

    @Override
    public int getItemCount() {
        return exerciseModeEquipmentModalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView equipmentHeader;
        TextInputEditText equipmentName, equipmentIconLink, equipmentImageLink01, equipmentImageLink02,
                equipmentTutorialVideoLink01, equipmentTutorialVideoLink02, equipmentNumberOfSets,
                equipmentNumberOfReps, equipmentDescription;
        AppCompatButton equipmentSaveButton;
        ScrollView equipmentScrollView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            equipmentHeader = itemView.findViewById(R.id.exerciseEquipmentHeader);
            equipmentName = itemView.findViewById(R.id.exerciseEquipmentNameInput);
            equipmentIconLink = itemView.findViewById(R.id.exerciseEquipmentIconLinkInput);
            equipmentImageLink01 = itemView.findViewById(R.id.exerciseEquipmentImageLink01Input);
            equipmentImageLink02 = itemView.findViewById(R.id.exerciseEquipmentImageLink02Input);
            equipmentTutorialVideoLink01 = itemView.findViewById(R.id.exerciseEquipmentTutorialVideoLink01Input);
            equipmentTutorialVideoLink02 = itemView.findViewById(R.id.exerciseEquipmentTutorialVideoLink02Input);
            equipmentNumberOfSets = itemView.findViewById(R.id.exerciseEquipmentSetsInput);
            equipmentNumberOfReps = itemView.findViewById(R.id.exerciseEquipmentRepsInput);
            equipmentDescription = itemView.findViewById(R.id.exerciseEquipmentDescriptionInput);
            equipmentSaveButton = itemView.findViewById(R.id.saveEquipmentButton);
            equipmentScrollView = itemView.findViewById(R.id.equipmentScrollView);
        }
    }
}