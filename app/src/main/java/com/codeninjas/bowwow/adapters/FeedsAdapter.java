package com.codeninjas.bowwow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.base.BaseActivity;
import com.codeninjas.bowwow.interfaces.SettingInterface;
import com.codeninjas.bowwow.models.FeedsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.MyViewHolder> {
    private final ArrayList<FeedsModel> feedsModelArrayList;
    private final BaseActivity baseActivity;
    private SettingInterface settingInterface;

    public FeedsAdapter(ArrayList<FeedsModel> feedsModelArrayList, BaseActivity baseActivity, SettingInterface settingInterface) {
        this.feedsModelArrayList = feedsModelArrayList;
        this.baseActivity = baseActivity;
        this.settingInterface = settingInterface;
    }

    public FeedsAdapter(ArrayList<FeedsModel> feedsModelArrayList, BaseActivity baseActivity) {
        this.feedsModelArrayList = feedsModelArrayList;
        this.baseActivity = baseActivity;
    }

    @NonNull
    @Override
    public FeedsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FeedsAdapter.MyViewHolder holder, int position) {
        FeedsModel feedsModel = feedsModelArrayList.get(position);
        holder.userNameTV.setText(feedsModel.getName());
        holder.feedTV.setText(feedsModel.getFeed());
        Picasso.get().load(feedsModel.getProfilePic()).placeholder(R.drawable.dog).into(holder.profileCIV);
        holder.feedTypeTV.setText(feedsModel.getType());
        holder.timeTV.setText(baseActivity.getTimeAgo(feedsModel.getCreated_at(), "EEEE dd,MM yyyy - hh:mm"));

        if (feedsModel.getType().equals("Story")) {
            holder.cardView.setCardBackgroundColor(baseActivity.getColor(R.color.light_red));
            holder.feedTypeTV.setTextColor(baseActivity.getColor(R.color.light_red));
        } else {
            holder.cardView.setCardBackgroundColor(baseActivity.getColor(R.color.light_blue));
            holder.feedTypeTV.setTextColor(baseActivity.getColor(R.color.light_blue));
        }
        holder.privacyIV.setVisibility(View.VISIBLE);
        if (!feedsModel.isPrivacy()) {

            holder.privacyIV.setImageResource(R.drawable.baseline_public_24);
        } else {
            holder.privacyIV.setImageResource(R.drawable.ic_lock_open);
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
        return feedsModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileCIV;
        CardView cardView;
        TextView userNameTV, timeTV, feedTV, feedTypeTV;
        ImageView privacyIV;
        Button deleteBT;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileCIV = itemView.findViewById(R.id.profileCIV);
            userNameTV = itemView.findViewById(R.id.userNameTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            feedTV = itemView.findViewById(R.id.feedTV);
            feedTypeTV = itemView.findViewById(R.id.feedTypeTV);
            privacyIV = itemView.findViewById(R.id.privacyIV);
            cardView = itemView.findViewById(R.id.cardView);
            deleteBT = itemView.findViewById(R.id.deleteBT);
        }
    }
}
