package com.codeninjas.bowwow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.base.BaseActivity;
import com.codeninjas.bowwow.interfaces.SettingInterface;
import com.codeninjas.bowwow.models.ReportsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder> {

    private final ArrayList<ReportsModel> reportsModelArrayList;
    private final BaseActivity baseActivity;

    private SettingInterface settingInterface;

    public ReportsAdapter(ArrayList<ReportsModel> reportsModelArrayList, BaseActivity baseActivity, SettingInterface settingInterface) {
        this.reportsModelArrayList = reportsModelArrayList;
        this.baseActivity = baseActivity;
        this.settingInterface = settingInterface;
    }

    public ReportsAdapter(ArrayList<ReportsModel> reportsModelArrayList, BaseActivity baseActivity) {
        this.reportsModelArrayList = reportsModelArrayList;
        this.baseActivity = baseActivity;
    }

    @NonNull
    @Override
    public ReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.report_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.MyViewHolder holder, int position) {
        ReportsModel reportsModel = reportsModelArrayList.get(position);
        holder.userNameTV.setText(reportsModel.getName());
        holder.reportTV.setText(reportsModel.getReport());
        Picasso.get().load(reportsModel.getProfilePic()).placeholder(R.drawable.dog).into(holder.profileCIV);
        Picasso.get().load(reportsModel.getImageUrl()).placeholder(R.drawable.dog).into(holder.pictureIV);
        holder.typeTV.setText(reportsModel.getType());
        holder.timeTV.setText(baseActivity.getTimeAgo(reportsModel.getCreated_at(), "EEEE dd,MM yyyy - hh:mm"));

        if (reportsModel.getType().equals("Lost")) {
            holder.typeTV.setTextColor(baseActivity.getColor(R.color.red));
        } else {
            holder.typeTV.setTextColor(baseActivity.getColor(R.color.greenColor));
        }

        if (settingInterface!=null) {
            holder.deleteBT.setVisibility(View.VISIBLE);
        } else {
            holder.deleteBT.setVisibility(View.GONE);
        }

        holder.deleteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingInterface.onItemClick(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return reportsModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileCIV;
        TextView userNameTV, timeTV, typeTV, reportTV;
        ImageView pictureIV;
        Button deleteBT;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCIV = itemView.findViewById(R.id.profileCIV);
            userNameTV = itemView.findViewById(R.id.userNameTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            typeTV = itemView.findViewById(R.id.typeTV);
            pictureIV = itemView.findViewById(R.id.pictureIV);
            reportTV = itemView.findViewById(R.id.reportTV);
            deleteBT = itemView.findViewById(R.id.deleteBT);

        }
    }

}
