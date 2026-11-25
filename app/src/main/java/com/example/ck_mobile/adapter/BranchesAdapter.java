package com.example.ck_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.R;
import com.example.ck_mobile.model.Branch;

import java.util.List;

public class BranchesAdapter extends RecyclerView.Adapter<BranchesAdapter.BranchViewHolder> {

    private final List<Branch> branchList;

    public BranchesAdapter(List<Branch> branchList) {
        this.branchList = branchList;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch_row, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch branch = branchList.get(position);
        holder.bind(branch);
    }

    @Override
    public int getItemCount() {
        return branchList.size();
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView tvBranchName, tvBranchAddress, tvBranchDistance, tvBranchHours;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBranchName = itemView.findViewById(R.id.tvBranchName);
            tvBranchAddress = itemView.findViewById(R.id.tvBranchAddress);
            tvBranchDistance = itemView.findViewById(R.id.tvBranchDistance);
            tvBranchHours = itemView.findViewById(R.id.tvBranchHours);
        }

        public void bind(Branch branch) {
            tvBranchName.setText(branch.getName());
            tvBranchAddress.setText(branch.getAddress());
            tvBranchDistance.setText(branch.getDistanceKm() + " km");
            tvBranchHours.setText("Giờ mở cửa: " + branch.getOpenHours());
        }
    }
}
