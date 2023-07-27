package com.codeninjas.bowwow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.base.BaseActivity;
import com.codeninjas.bowwow.interfaces.SettingInterface;
import com.codeninjas.bowwow.models.NotesModel;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private final ArrayList<NotesModel> notesModelArrayList;
    private final BaseActivity baseActivity;
    private SettingInterface settingInterface;

    public NotesAdapter(ArrayList<NotesModel> notesModelArrayList, BaseActivity baseActivity, SettingInterface settingInterface) {
        this.notesModelArrayList = notesModelArrayList;
        this.baseActivity = baseActivity;
        this.settingInterface = settingInterface;
    }


    @NonNull
    @Override
    public NotesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.MyViewHolder holder, int position) {

        NotesModel notesModel = notesModelArrayList.get(position);
        holder.titleTV.setText(notesModel.getTitle());
        holder.noteTV.setText(notesModel.getNote());
        String timeAgo = baseActivity.getTimeAgo(notesModel.getCreated_at(), "hh:mm dd-yyyy-mm");
        holder.createdTV.setText(timeAgo);

        int count = holder.getAdapterPosition() % 3;

        switch (count) {
            case 1:
                holder.cardView.setCardBackgroundColor(baseActivity.getColor(R.color.light_blue));
                break;
            case 2:
                holder.cardView.setCardBackgroundColor(baseActivity.getColor(R.color.light_red));
                break;
            default:
                holder.cardView.setCardBackgroundColor(baseActivity.getColor(R.color.light_grey));
                break;
        }

        holder.itemView.setOnClickListener(v->settingInterface.onItemClick(holder.getAdapterPosition()));

    }

    @Override
    public int getItemCount() {
        return notesModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV, noteTV, createdTV;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTV = itemView.findViewById(R.id.titleTV);
            noteTV = itemView.findViewById(R.id.noteTV);
            createdTV = itemView.findViewById(R.id.createdTV);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}
