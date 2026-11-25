package com.example.ck_mobile.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.R;
import com.example.ck_mobile.adapter.BranchesAdapter;
import com.example.ck_mobile.repository.CustomerRepository;
import com.google.android.material.appbar.MaterialToolbar;

public class ActivityNearbyBranches extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvBranches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_branches);

        toolbar = findViewById(R.id.toolbar);
        rvBranches = findViewById(R.id.rvBranches);

        toolbar.setNavigationOnClickListener(v -> finish());

        rvBranches.setLayoutManager(new LinearLayoutManager(this));
        BranchesAdapter adapter = new BranchesAdapter(CustomerRepository.getInstance().getBranches());
        rvBranches.setAdapter(adapter);
    }
}
