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

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.MyViewHolder> {
    BaseActivity baseActivity;
    SettingInterface settingInterface;

    public SettingAdapter(BaseActivity baseActivity, SettingInterface settingInterface) {
        this.baseActivity = baseActivity;
        this.settingInterface = settingInterface;
    }

    String[] items = {"Profile", "My Feeds", "My Reports", "Reminders", "Notes", "Terms", "Privacy Policy", "Need Insurance?", "Sign Out"};
    int[] itemsIcon = {R.drawable.dog_seating, R.drawable.reports, R.drawable.search, R.drawable.reminders, R.drawable.notes,R.drawable.terms, R.drawable.privacy, R.drawable.insurance, R.drawable.baseline_logout_24};

    @NonNull
    @Override
    public SettingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SettingAdapter.MyViewHolder holder, int position) {
        holder.imageIV.setImageResource(itemsIcon[position]);
        holder.itemTV.setText(items[position]);
        holder.itemView.setOnClickListener(v-> settingInterface.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIV;
        TextView itemTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTV = itemView.findViewById(R.id.itemTV);
            imageIV = itemView.findViewById(R.id.imageIV);

        }
    }
}
