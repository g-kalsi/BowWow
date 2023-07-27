package com.codeninjas.bowwow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.base.BaseActivity;
import com.codeninjas.bowwow.interfaces.SettingInterface;
import com.codeninjas.bowwow.models.FeedsModel;
import com.codeninjas.bowwow.models.RemindersModel;

import java.util.ArrayList;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.MyViewHolder> {

    private final ArrayList<RemindersModel> remindersModelsArrayList;
    private final BaseActivity baseActivity;
    private SettingInterface settingInterface;

    public RemindersAdapter(ArrayList<RemindersModel> remindersModels, BaseActivity baseActivity, SettingInterface settingInterface) {
        this.remindersModelsArrayList = remindersModels;
        this.baseActivity = baseActivity;
        this.settingInterface = settingInterface;
    }

    @NonNull
    @Override
    public RemindersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.reminders_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RemindersAdapter.MyViewHolder holder, int position) {
        RemindersModel remindersModel = remindersModelsArrayList.get(position);
        holder.titleTV.setText(remindersModel.getTitle());
        holder.messageTV.setText(remindersModel.getMessage());
        holder.closeIV.setOnClickListener(v -> settingInterface.onItemClick(holder.getAdapterPosition()));

    }

    @Override
    public int getItemCount() {
        return remindersModelsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV, messageTV;
        ImageView closeIV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.titleTV);
            messageTV = itemView.findViewById(R.id.messageTV);
            closeIV = itemView.findViewById(R.id.closeIV);
        }

    }
}
