package com.zach.civiladvocacy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialListAdapter extends RecyclerView.Adapter<OfficialViewHolder> {
    private final MainActivity main;
    private final List<Official> officials;

    public OfficialListAdapter(List<Official> officials, MainActivity main) {
        this.main = main;
        this.officials = officials;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_entry, parent, false);
        itemView.setOnLongClickListener(main);
        itemView.setOnClickListener(main);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official official = officials.get(position);
        holder.officialTitle.setText(official.getOfficeTitle());
        holder.officialName.setText(official.getName());
        String party = "(" + official.getParty() + ")";
        holder.officialParty.setText(party);
    }

    @Override
    public int getItemCount() {
        return officials.size();
    }
}
