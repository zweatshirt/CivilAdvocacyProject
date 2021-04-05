package com.zach.civiladvocacy;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {
        TextView officialTitle;
        TextView officialName;
        TextView officialParty;

    public OfficialViewHolder(@NonNull View itemView) {
        super(itemView);

        officialTitle = itemView.findViewById(R.id.titleView);
        officialName = itemView.findViewById(R.id.officialNameView);
        officialParty = itemView.findViewById(R.id.officialPartyView);
    }
}

