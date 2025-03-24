package com.stream.zenfit.Adapter;

import android.content.Context;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.stream.zenfit.Modal.ExerciseModeEquipmentModal;
import com.stream.zenfit.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExerciseModeEquipmentAdapter extends RecyclerView.Adapter<ExerciseModeEquipmentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ExerciseModeEquipmentModal> exerciseModeEquipmentModalList;
    private String exerciseName;
    private FirebaseFirestore firebaseFirestore;

    public ExerciseModeEquipmentAdapter(Context context, ArrayList<ExerciseModeEquipmentModal> exerciseModeEquipmentModalList, String exerciseName) {
        this.context = context;
        this.exerciseModeEquipmentModalList = exerciseModeEquipmentModalList;
        this.exerciseName = exerciseName;
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_add_exercise_equipment_details_recycler_view_item, parent, false);
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

        holder.equipmentSaveButton.setOnClickListener(v -> {
            String enteredEquipmentName = holder.equipmentName.getText().toString().trim();

            if (enteredEquipmentName.isEmpty() || enteredEquipmentName == null || enteredEquipmentName.equals("")) {
                Toast.makeText(context, "Enter Equipment Name", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> equipmentData = new HashMap<>();
            equipmentData.put("Equipment Name", enteredEquipmentName);
            equipmentData.put("Equipment Icon Link", holder.equipmentIconLink.getText().toString().trim());
            equipmentData.put("Equipment Image Link01", holder.equipmentImageLink01.getText().toString().trim());
            equipmentData.put("Equipment Image Link02", holder.equipmentImageLink02.getText().toString().trim());
            equipmentData.put("Equipment Tutorial Video Link 01", holder.equipmentTutorialVideoLink01.getText().toString().trim());
            equipmentData.put("Equipment Tutorial Video Link 02", holder.equipmentTutorialVideoLink02.getText().toString().trim());
            equipmentData.put("Equipment Number Of Sets", holder.equipmentNumberOfSets.getText().toString().trim());
            equipmentData.put("Equipment Number Of Reps", holder.equipmentNumberOfReps.getText().toString().trim());
            equipmentData.put("Equipment Description", holder.equipmentDescription.getText().toString().trim());

            firebaseFirestore.collection("Exercise Mode")
                    .document(exerciseName.toUpperCase())
                    .collection("Equipment used in " + exerciseName)
                    .document(enteredEquipmentName) // Use equipment name as document ID
                    .set(equipmentData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("FireStoreSuccess", "Document added with ID: ");
                        Toast.makeText(context, "Equipment " + enteredEquipmentName + " added successfully!", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                        holder.equipmentScrollView.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FireStoreError", "Error adding document", e);
                        Toast.makeText(context, "Error adding equipment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
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
