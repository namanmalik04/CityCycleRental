package com.example.citycycle;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.citycycle.databinding.ActivityAllCycleBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import java.util.ArrayList;
import java.util.List;

public class AllCycleActivity extends AppCompatActivity {
    private ActivityAllCycleBinding binding;
    private RecyclerView recyclerView;
    private AllCycleAdapter adapter;
    private List<Cycle> cycleList;
    private DBHelper dbHelper;
    private EditText etNameSearch;
    private Spinner spBranch;
    private String selectedBranch = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllCycleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigation();

        recyclerView = binding.recyclerViewAllCycles;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etNameSearch = binding.etNameSearch;
        spBranch = binding.spBranch;

        dbHelper = new DBHelper(this);

        cycleList = dbHelper.getAllCycles();
        if (cycleList == null) {
            cycleList = new ArrayList<>();
        }


        if (cycleList == null || cycleList.isEmpty()) {
            Log.e("AllCycleActivity", "No cycles available");
        } else {
            adapter = new AllCycleAdapter(this, cycleList);
            recyclerView.setAdapter(adapter);
        }

        setupSearchFilter();
        setupBranchSpinner();
    }


    private void setupSearchFilter() {
        etNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCycles(s.toString(), selectedBranch);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    private void setupBranchSpinner() {
        List<String> branches = dbHelper.getAllBranches();
        branches.add(0, "All");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, branches);
        spBranch.setAdapter(adapter);

        spBranch.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedBranch = branches.get(position);
                filterCycles(etNameSearch.getText().toString(), selectedBranch);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }


    private void filterCycles(String query, String branch) {
        List<Cycle> filteredList = new ArrayList<>();

        for (Cycle cycle : cycleList) {
            boolean matchesName = cycle.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesBranch = branch.equals("All") || cycle.getCity().equalsIgnoreCase(branch);

            if (matchesName && matchesBranch) {
                filteredList.add(cycle);
            }
        }

        adapter.updateList(filteredList);
    }

    private void bottomNavigation() {
        ChipNavigationBar bottomNav = binding.bottomNavigation;
        bottomNav.setItemSelected(R.id.all_cycle, true);

        bottomNav.setOnItemSelectedListener(id -> {
            Intent intent = null;

            if (id == R.id.home) {
                intent = new Intent(AllCycleActivity.this, MainActivity.class);
            } else if (id == R.id.activity) {
                intent = new Intent(AllCycleActivity.this, ActivityHistory.class);
            } else if (id == R.id.profile) {
                intent = new Intent(AllCycleActivity.this, ProfileActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }
}
